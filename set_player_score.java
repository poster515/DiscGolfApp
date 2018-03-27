package com.scoresheet.discgolf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.scoresheet.discgolf.R.id.player_name;
import static com.scoresheet.discgolf.R.id.score_picker;

/**
 * Created by Joe Post on 5/17/2017.
 */

public class set_player_score extends Fragment implements View.OnClickListener{
    View myView;
    NumberPicker sp;
    ArrayList<String> PlayerNames = new ArrayList<String>();
    ArrayList<Integer> hole_par_currentplayer = new ArrayList<>();
    ArrayList<Integer> hole_list = new ArrayList<>();
    TextView playername;
    private boolean edit_hole = false;
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

    public set_player_score(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.popup_menu, container, false);

        //set button functionality
        final Button back = (Button)  myView.findViewById(R.id.player_score_back);
        back.setOnClickListener(this);
        final Button next = (Button) myView.findViewById(R.id.player_score_next);
        next.setOnClickListener(this);

        //initialize numberpicker
        sp = (NumberPicker) myView.findViewById(score_picker);
        playername = (TextView) myView.findViewById(player_name);

        String nums[] = new String[25];
        for(int i = 0; i < nums.length; i++){
            nums[i] = Integer.toString(i + 1);
        }
        sp.setMinValue(1);
        sp.setMaxValue(25);
        sp.setWrapSelectorWheel(false);
        sp.setDisplayedValues(nums);
        sp.setValue(1);

//        if(hole_par_currentplayer.get(3) == 1){
//            next.setText("Done");
//        } else { next.setText("Next"); }

        Bundle args = this.getArguments();
        PlayerNames = args.getStringArrayList("pn");
        hole_par_currentplayer = args.getIntegerArrayList("hp");
        hole_list = args.getIntegerArrayList("hole_list");
        edit_hole = args.getBoolean("edit_hole");
        playername.setText(PlayerNames.get(PlayerNames.size() - hole_par_currentplayer.get(3)));

        //Toast.makeText(getActivity(), Integer.toString(hole_par_currentplayer.get(2)), Toast.LENGTH_SHORT).show();
        //String name = args.getStringArrayList("pn").get(0);

        return myView;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player_score_back:

                FragmentManager fragmentManager = getFragmentManager();
                Bundle args = new Bundle();
                args.putStringArrayList("pn", PlayerNames);
                args.putIntegerArrayList("hole_list", hole_list);
                args.putBoolean("edit_hole", edit_hole);
//                if(hole_par_currentplayer.size() > 4) {
//                    hole_par_currentplayer.remove(PlayerNames.size() - hole_par_currentplayer.get(3) + 4);
//                }
                if(hole_par_currentplayer.get(3) == PlayerNames.size()) {
                    add_hole ah = new add_hole();
                    args.putIntegerArrayList("hp", hole_par_currentplayer);
                    ah.setArguments(args);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, ah)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Bundle args2 =  new Bundle();
                    args2.putStringArrayList("pn", PlayerNames);
                    hole_par_currentplayer.set(3, hole_par_currentplayer.get(3) + 1);
                    args2.putIntegerArrayList("hp", hole_par_currentplayer);
                    args2.putIntegerArrayList("hole_list", hole_list);
                    args2.putBoolean("edit_hole", edit_hole);
                    set_player_score sps1 = new set_player_score();
                    sps1.setArguments(args2);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, sps1)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case R.id.player_score_next:
                FragmentManager fragmentManager1 = getFragmentManager();
                Bundle args1 = new Bundle();

                //add current player score to array list
                int score = sp.getValue();

                //add playernames to the array list
                args1.putStringArrayList("pn", PlayerNames);

                //get player index for current score entry
                int index = 4 + (PlayerNames.size() - hole_par_currentplayer.get(3));
                hole_par_currentplayer.add(index, score);
                //next decrease the number of current player by one for next fragment
                Integer next_player = hole_par_currentplayer.get(3) - 1;

                if (next_player != 0) {

                    //add to array list at the same location
                    hole_par_currentplayer.set(3, next_player);

                    //add to bundle for next fragment
                    args1.putIntegerArrayList("hp", hole_par_currentplayer);
                    args1.putBoolean("edit_hole", edit_hole);
                    args1.putIntegerArrayList("hole_list", hole_list);
                    set_player_score sps = new set_player_score();

                    //add bundle to fragment
                    sps.setArguments(args1);
                    fragmentManager1.beginTransaction()
                            .replace(R.id.content_frame, sps)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Bundle args3 = new Bundle();
                    args3.putStringArrayList("pn", PlayerNames);
                    //going back to the second_fragment now, pass PlayerNames back again
                    second_fragment sf = new second_fragment();

                    args3.putIntegerArrayList("hp", hole_par_currentplayer);
                    args3.putBoolean("edit_hole", edit_hole);
                    args3.putIntegerArrayList("hole_list", hole_list);
                    sf.setArguments(args3);
                    DatabaseHelperScoringTable scoringtableDBH = new DatabaseHelperScoringTable(getActivity());

                    //created an instance of the database, now write to it with the above values:
//                    edit_hole = this.getArguments().getBoolean("edit_hole");
                    if(!edit_hole) {
                        try {
                            scoringtableDBH.addRowtoSingleScoringTable(args3);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    } else {
                        scoringtableDBH.editRowtoSingleScoringTable(args3);
                    }
                    FragmentManager fragmentManager2 = getFragmentManager();
                    fragmentManager2.beginTransaction()
                            .replace(R.id.content_frame, sf)
//                            .addToBackStack(null)
                            .commit();

//                    Toast.makeText(getActivity(), "DONE WITH PLAYERS", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
