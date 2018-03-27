package com.scoresheet.discgolf;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Joe Post on 5/27/2017.
 */

public class singleCourseDisplayPage extends Fragment implements View.OnClickListener{

    View myView;
    //when we call the add_hole class, give it a default hole number of 0 to begin with
    int default_hole = 1;
    private static final String tag = "dialog";
    private static final int REQ_CODE = 1;
    private boolean done_with_course = false;
    private boolean course_created;
    private boolean edit_hole;
    private Context context = getActivity();

    private Bundle args = new Bundle();

    //create instance of the scoring table database
    ArrayList<Integer> net_scores = new ArrayList<>();
    /*
    this array list is constructed as follows:
    0: course_id
    1: hole #
    2: par #
    3: current player index
    4: player 1 score
    5: player 2 score
    ...
    N: player (N-3) score
    */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DatabaseHelperScoringTable scoringtableDBH = new DatabaseHelperScoringTable(getActivity());
        myView = inflater.inflate(R.layout.unique_course_info, container, false);

        //find all buttons and set onClickListeners for each
        final Button back_button = (Button) myView.findViewById(R.id.back_button);
        back_button.setOnClickListener(this);

//        final Button edit_hole = (Button) myView.findViewById(R.id.edit_hole);
//        edit_hole.setOnClickListener(this);
//
//        final Button done_with_course = (Button) myView.findViewById(R.id.done_with_course);
//        done_with_course.setOnClickListener(this);
        Bundle args = this.getArguments();

        TextView title = (TextView) myView.findViewById(R.id.unique_course_name);
        title.setText(args.getString("unique_course_title"));

        TableLayout score_table = (TableLayout) myView.findViewById(R.id.score_table);
        TableLayout name_table = (TableLayout) myView.findViewById(R.id.name_table);

        //initialize hole and par textviews
        TextView hole_tv = new TextView(getActivity());
        hole_tv.setText("Hole");
        hole_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
        TableRow tr_hole = new TableRow(getActivity());
        tr_hole.addView(hole_tv);
        name_table.addView(tr_hole, new TableLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        TextView par_tv = new TextView(getActivity());
        par_tv.setText("Par");
        par_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
        TableRow tr_par = new TableRow(getActivity());
        tr_par.addView(par_tv);
        name_table.addView(tr_par, new TableLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        //now get specific course information

        int unique_course_id = args.getInt("unique_course_id"); //this sole piece of info is required

        List<String> player_names = scoringtableDBH.getPlayerNamesforUniqueCourse(unique_course_id);
        List<Integer> holes = scoringtableDBH.getHolesForUniqueCourse(unique_course_id, player_names.get(0));
        ArrayList<Integer> pars = scoringtableDBH.getParsForUniqueCourse(unique_course_id, player_names.get(0));

//        for(int i = 0; i < player_names.size(); i++){
//            TableRow row_for_player_name = new TableRow(getActivity());
//            TextView tv = new TextView(getActivity());
//            tv.setText(player_names.get(i));
//            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
//            row_for_player_name.addView(tv);
//            name_table.addView(row_for_player_name, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
//        }

        TableRow row_for_hole_nums = new TableRow(getActivity());
        row_for_hole_nums.setOrientation(TableRow.HORIZONTAL);

        for(int i = 0; i < holes.size(); i++){
            TextView tv = new TextView(getActivity());
            tv.setText(holes.get(i).toString());
            tv.setWidth(100);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
            row_for_hole_nums.addView(tv);
        }

        score_table.addView(row_for_hole_nums, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        TableRow row_for_par_nums = new TableRow(getActivity());
        row_for_par_nums.setOrientation(TableRow.HORIZONTAL);

        for(int i = 0; i < pars.size(); i++){
            TextView tv = new TextView(getActivity());
            tv.setText(pars.get(i).toString());
            tv.setWidth(50);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
            row_for_par_nums.addView(tv);
        }

        score_table.addView(row_for_par_nums, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        for(int i = 0; i < player_names.size(); i++){
            ArrayList<Integer> player_scores
                = scoringtableDBH.getPlayerScoresUniqueCourse(unique_course_id, player_names.get(i));
            TableRow row_for_player_score = new TableRow(getActivity());
            row_for_player_score.setOrientation(TableRow.HORIZONTAL);

            net_scores.add(i, getPlayerScore(player_scores, pars));

            for(int j = 0; j < player_scores.size(); j++){
                TextView tv1 = new TextView(getActivity());
                tv1.setText(player_scores.get(j).toString());
                tv1.setWidth(50);
                tv1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
                row_for_player_score.addView(tv1);
            }
            score_table.addView(row_for_player_score, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

            TextView tv = new TextView(getActivity());
            if(score_table.getChildCount() > 0) {
                if (net_scores.get(i) >= 0) {
                    tv.setText(player_names.get(i) + " (+" + net_scores.get(i) + ")");
                } else {
                    tv.setText(player_names.get(i) + " (" + net_scores.get(i) + ")");
                }
            } else {
                tv.setText(player_names.get(i));
            }
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
            TableRow tr = new TableRow(getActivity());
            tr.addView(tv);

            //now add to name_table
            name_table.addView(tr, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        }

        boolean tie = false;
        int current_index = 0;
        if(score_table.getChildCount() > 0) {
            Integer best_score = net_scores.get(0);
            String best_name = player_names.get(0);
            ArrayList<Integer> best_score_index = new ArrayList<>();

            int current_best_score_index = 0;
            int current_best_score = net_scores.get(0);

            for (int j = 0; j < net_scores.size() - 1; j++) {
                for (int k = j + 1; k < net_scores.size(); k++) {
                    if (net_scores.get(j) > net_scores.get(k) && current_best_score >= net_scores.get(k)) {
                        current_best_score_index = k;
                        current_best_score = net_scores.get(k);
                    }
                }
            }
//                best_score_index.add(0, current_best_score_index);
            int loop = 0;
            //this for loop will determine if any other score in the scores arraylist matches the lowest score
            for (int j = 0; j < net_scores.size(); j++) {
                if(net_scores.get(j) == net_scores.get(current_best_score_index) &&
                        j != current_best_score_index) {
                    tie = true;
                    best_score_index.add(loop, j);
                    loop++;
                }
            }
            //this loop displays the leader(s) for a round, at the TextView atop the table
            String best = null;
            if(!tie) {
                best_name = player_names.get(current_best_score_index);
                best_score = net_scores.get(current_best_score_index);
                if (best_score >= 0) {
                    best = "Leader: " + best_name + " at " + "+" + Integer.toString(best_score);
                } else if (best_score < 0) {
                    best = "Leader: " + best_name + " at " + Integer.toString(best_score);
                }
                TextView tv = (TextView) myView.findViewById(R.id.leader);
                tv.setText(best);
            } else {
                String names = player_names.get(current_index);
                for(int i = 0; i < best_score_index.size(); i++){
                    names = names +  ", " + player_names.get(best_score_index.get(i));
                }
                if (best_score >= 0) {
                    best = "Leaders: " + names + " at " + "+" + Integer.toString(best_score);
                } else if (best_score < 0) {
                    best = "Leaders: " + names + " at " + Integer.toString(best_score);
                }
                TextView tv = (TextView) myView.findViewById(R.id.leader);
                tv.setText(best);
            }
        }
        return myView;
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                FragmentManager fragmentManager2 = getFragmentManager();
                fragmentManager2.beginTransaction()
                        .replace(R.id.content_frame, new first_fragment())
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
    public Integer getPlayerScore(ArrayList<Integer> scores, ArrayList<Integer> pars){
        Integer netscore = 0;
        for(int i = 0; i < scores.size(); i++){
            netscore = netscore + (scores.get(i) - pars.get(i));
        }
        return netscore;
    }
}
