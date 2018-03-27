package com.scoresheet.discgolf;

import android.content.Intent;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Joe Post on 4/5/2017.
 */

public class second_fragment extends Fragment implements View.OnClickListener{
    View myView;

    private static final int REQ_CODE = 1;
    private boolean course_created;
    private Bundle args = new Bundle();
    private boolean edit_hole = false;
    //create instance of the scoring table database
    ArrayList<String> PlayerNames = new ArrayList<String>();
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
        myView = inflater.inflate(R.layout.second_layout, container, false);
        course_created = true;
        edit_hole = true;

        //find all buttons and set onClickListeners for each
        final Button add_discgolfcourse = (Button) myView.findViewById(R.id.add_discgolfcourse);
        add_discgolfcourse.setOnClickListener(this);

        final Button edit_hole = (Button) myView.findViewById(R.id.edit_hole);
        edit_hole.setOnClickListener(this);

        final Button done_with_course = (Button) myView.findViewById(R.id.done_with_course);
        done_with_course.setOnClickListener(this);

        final Button delete = (Button) myView.findViewById(R.id.delete);
        delete.setOnClickListener(this);

        //now, read from that database to obtain all current database values
        //first, create array of SingleRoundScoringTable

        List<SingleRoundScoringTable> SRSTarray = new ArrayList<SingleRoundScoringTable>();

        //send list of player names to new ArrayList


        //TODO: set this function to grab all entries marked as not done
        //SRSTarray = scoringtableDBH.getAllHoles(getActivity());

        TableLayout score_table = (TableLayout) myView.findViewById(R.id.score_table);
        TableLayout name_table = (TableLayout) myView.findViewById(R.id.name_table);

        //initialize hole and par textviews
        TextView hole_tv = new TextView(getActivity());
        hole_tv.setText("Hole");
        hole_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
        TableRow tr_hole = new TableRow(getActivity());
        tr_hole.addView(hole_tv);
        name_table.addView(tr_hole, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        TextView par_tv = new TextView(getActivity());
        par_tv.setText("Par");
        par_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
        TableRow tr_par = new TableRow(getActivity());
        tr_par.addView(par_tv);
        name_table.addView(tr_par, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        //check for no entries, and tell the user if they need to add players to the current game
        boolean no_current_table = scoringtableDBH.noCurrentCourse();

        try {
            PlayerNames = this.getArguments().getStringArrayList("pn");
        } catch (Exception e) {
            PlayerNames = null;
        }

        if(!no_current_table) {
            //now let's handle all the scores for that single player.
            ArrayList<Integer> hole_list = new ArrayList<>();
            ArrayList<Integer> par_list = new ArrayList<>();
            //
            if(PlayerNames == null || PlayerNames.size() == 0){
                PlayerNames = scoringtableDBH.getAllCurrentNames();
            }
            hole_list = scoringtableDBH.getSingleHoleRow(PlayerNames.get(0));
            par_list = scoringtableDBH.getSingleParRow(PlayerNames.get(0));

            TableRow row_for_hole_hums = new TableRow(getActivity());
            row_for_hole_hums.setOrientation(TableRow.HORIZONTAL);

            for(int i = 0; i < hole_list.size(); i++){
                TextView tv = new TextView(getActivity());
                tv.setText(hole_list.get(i).toString());
                tv.setWidth(100);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
                row_for_hole_hums.addView(tv);
            }

            score_table.addView(row_for_hole_hums, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

            TableRow row_for_par_nums = new TableRow(getActivity());
            row_for_par_nums.setOrientation(TableRow.HORIZONTAL);

            for(int i = 0; i < par_list.size(); i++){
                TextView tv = new TextView(getActivity());
                tv.setText(par_list.get(i).toString());
                tv.setWidth(50);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
                row_for_par_nums.addView(tv);

            }
            //now add the populated row to the table
            score_table.addView(row_for_par_nums, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));


            for (int i = 0; i < PlayerNames.size(); i++) {


                ArrayList<Integer> score_list = new ArrayList<>();
                score_list = scoringtableDBH.getSinglePlayerRow(PlayerNames.get(i), getActivity());

                net_scores.add(i, getPlayerScore(score_list, par_list));

                TextView tv = new TextView(getActivity());
                if(score_table.getChildCount() > 0) {
                    if (net_scores.get(i) >= 0) {
                        tv.setText(PlayerNames.get(i) + " (+" + net_scores.get(i) + ")");
                    } else {
                        tv.setText(PlayerNames.get(i) + " (" + net_scores.get(i) + ")");
                    }
                } else {
                    tv.setText(PlayerNames.get(i));
                }
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
                TableRow tr = new TableRow(getActivity());
                tr.addView(tv);

                //now add to name_table
                name_table.addView(tr, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

                TableRow row_for_player_score = new TableRow(getActivity());
                row_for_player_score.setOrientation(TableRow.HORIZONTAL);
                for(int j = 0; j < score_list.size(); j++){
                    TextView tv1 = new TextView(getActivity());
                    tv1.setText(score_list.get(j).toString());
                    tv1.setWidth(50);
                    tv1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
                    row_for_player_score.addView(tv1);
                }

                score_table.addView(row_for_player_score, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

            }

            boolean tie = false;
            int current_index = 0;
            if(score_table.getChildCount() > 0) {
                Integer best_score = net_scores.get(0);
                String best_name;
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

                for (int j = 0; j < net_scores.size(); j++) {
                    if(net_scores.get(j) == net_scores.get(current_best_score_index) &&
                            j != current_best_score_index) {
                        tie = true;
                        best_score_index.add(loop, j);
                        loop++;
                    }
                }

                String best = null;
                if(!tie) {
                    best_name = PlayerNames.get(current_best_score_index);
                    best_score = net_scores.get(current_best_score_index);
                    if (best_score >= 0) {
                        best = "Leader: " + best_name + " at " + "+" + Integer.toString(best_score);
                    } else if (best_score < 0) {
                        best = "Leader: " + best_name + " at " + Integer.toString(best_score);
                    }
                    TextView tv = (TextView) myView.findViewById(R.id.leader);
                    tv.setText(best);
                } else {
                    String names = PlayerNames.get(current_index);
                    for(int i = 0; i < best_score_index.size(); i++){
                        names = names +  ", " + PlayerNames.get(best_score_index.get(i));
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
        } else if (PlayerNames != null) {
            for (int i = 0; i < PlayerNames.size(); i++) {
                TextView tv = new TextView(getActivity());
                tv.setText(PlayerNames.get(i));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
                TableRow tr = new TableRow(getActivity());
                tr.addView(tv);

                //now add to name_table
                name_table.addView(tr, new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            }
        } else{
            Toast.makeText(getActivity(), "Please enter player names to begin course.", Toast.LENGTH_SHORT).show();
        }
        return myView;
    }
    public void onClick(View v) {
        DatabaseHelperScoringTable scoringtableDBH = new DatabaseHelperScoringTable(getActivity());
        FragmentManager fragmentManager = getFragmentManager();
        switch (v.getId()) {
            case R.id.add_discgolfcourse:

                if(PlayerNames != null) {
                    //send playernames string array to add_hole fragment
                    add_hole new_add_hole = new add_hole();
//                    DatabaseHelperScoringTable scoringtableDBH = new DatabaseHelperScoringTable(getActivity());
                    ArrayList<Integer> hole_list = scoringtableDBH.getSingleHoleRow(PlayerNames.get(0));
                    args.putIntegerArrayList("hole_list", hole_list);
                    args.putStringArrayList("pn", PlayerNames);
                    args.putBoolean("edit_hole", false);
                    new_add_hole.setArguments(args);

                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, new_add_hole)
                            .addToBackStack(null)
                            .commit();
                } else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, new third_fragment())
                            .addToBackStack(null)
                            .commit();
                }
                break;

            case R.id.edit_hole:
                Bundle args1 = new Bundle();
                args1.putStringArrayList("pn", PlayerNames);
                ArrayList<Integer> hole_list = scoringtableDBH.getSingleHoleRow(PlayerNames.get(0));
                boolean edit_hole = true;
                args1.putBoolean("edit_hole", edit_hole);
                args1.putIntegerArrayList("hole_list", hole_list);
                add_hole add_hole = new add_hole();
                add_hole.setArguments(args1);

                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, add_hole)
                        .addToBackStack(null)
                        .commit();

                break;
            case R.id.done_with_course:
                    //the next two proceeding lines will work, but the user should be expected
                    //to be able to verify they intend to mark the course as 'done'
                TableLayout score_table = (TableLayout) myView.findViewById(R.id.score_table);
                if(score_table.getChildCount() == 0){
                    Toast.makeText(getActivity(), "Please enter course information.", Toast.LENGTH_LONG).show();
                    break;
                } else {
                    CourseDoneDialogConfirmation dialog = new CourseDoneDialogConfirmation();
                    dialog.setTargetFragment(second_fragment.this, REQ_CODE);
                    dialog.show(getFragmentManager(), "dialog");
                    break;
                }
            case R.id.delete:
                CourseDeleteConfirmationDialogBox dialog = new CourseDeleteConfirmationDialogBox();
                dialog.setTargetFragment(second_fragment.this, REQ_CODE);
                dialog.show(getFragmentManager(), "dialog");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == -1){
            done();

            //Toast.makeText(getActivity(), "You hit the Yes button.", Toast.LENGTH_SHORT).show();
        } else if (resultCode == 2){
            DatabaseHelperScoringTable dbh = new DatabaseHelperScoringTable(getActivity());
            dbh.deleteCurrentTable();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new second_fragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void done(){
        try {
            FragmentManager fragmentManager2 = getFragmentManager();

            //now that the user has confirmed we're done, go to the next fragment to select the
            //name of the course.
            fragmentManager2.beginTransaction()
                    .replace(R.id.content_frame, new SetCourseNameForSingleRound())
                    .addToBackStack(null)
                    .commit();
            Toast.makeText(getActivity(), "You hit the Yes button.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Error: ", e.toString());
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
