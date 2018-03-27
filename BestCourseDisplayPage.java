package com.scoresheet.discgolf;

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
 * Created by Joe Post on 5/28/2017.
 */

public class BestCourseDisplayPage extends Fragment implements View.OnClickListener{
    public BestCourseDisplayPage(){
    }
    View myView;
    ArrayList<Integer> net_scores = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DatabaseHelperScoringTable scoringtableDBH = new DatabaseHelperScoringTable(getActivity());
        myView = inflater.inflate(R.layout.unique_course_info, container, false);

        //find all buttons and set onClickListeners for each
        final Button back_button = (Button) myView.findViewById(R.id.back_button);
        back_button.setOnClickListener(this);

        Bundle args = this.getArguments();

        TextView title = (TextView) myView.findViewById(R.id.unique_course_name);
        DatabaseHelper DBH = new DatabaseHelper(getActivity());
        title.setText("Course: " + DBH.getCourseName(args.getInt("course_id")));

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
        String player_to_display_best_course = args.getString("name");

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
        int best_player_score = 0;

        for(int i = 0; i < player_names.size(); i++){
            ArrayList<Integer> player_scores
                    = scoringtableDBH.getPlayerScoresUniqueCourse(unique_course_id, player_names.get(i));
            TableRow row_for_player_score = new TableRow(getActivity());
            row_for_player_score.setOrientation(TableRow.HORIZONTAL);

            net_scores.add(i, getPlayerScore(player_scores, pars));
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
            if(player_names.get(i).equalsIgnoreCase(player_to_display_best_course)){
                best_player_score = getPlayerScore(player_scores, pars);
            }

            for(int j = 0; j < player_scores.size(); j++){
                TextView tv1 = new TextView(getActivity());
                tv1.setText(player_scores.get(j).toString());
                tv1.setWidth(50);
                tv1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
                row_for_player_score.addView(tv1);
            }
            score_table.addView(row_for_player_score, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        }
//
//        Integer best_score = net_scores.get(0);
//        String best_name = player_names.get(0);
//        for (int j = 1; j < net_scores.size(); j++) {
//            if (best_score < net_scores.get(j)) {
//                best_score = best_score;
//            } else {
//                best_score = net_scores.get(j);
//                best_name = player_names.get(j);
//            }
//        }
        String best = null;
//        if(best_score >= 0) {
//            best = "Leader: " + best_name + " at " + "+" + Integer.toString(best_score);
//        } else if (best_score < 0){
//            best = "Leader: " + best_name + " at " + Integer.toString(best_score);
//        }
        if(best_player_score >= 0) {
            best = player_to_display_best_course + "'s Best Score: +" + Integer.toString(best_player_score);
        } else if (best_player_score < 0){
            best = player_to_display_best_course + "'s Best Score: " + Integer.toString(best_player_score);
        }
        TextView tv = (TextView) myView.findViewById(R.id.leader);
        tv.setText(best);
        return myView;
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                FragmentManager fragmentManager2 = getFragmentManager();
                AllTimeBestScoreNames atbsn = new AllTimeBestScoreNames();
                Bundle args = new Bundle();
                args.putInt("course_id", this.getArguments().getInt("course_id"));
                atbsn.setArguments(args);
                fragmentManager2.beginTransaction()
                        .replace(R.id.content_frame, atbsn)
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
