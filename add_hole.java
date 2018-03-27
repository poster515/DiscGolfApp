package com.scoresheet.discgolf;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Integer.valueOf;

/**
 * Created by Joe Post on 4/19/2017.
 */

public class add_hole extends Fragment implements View.OnClickListener{

    View myView;
    NumberPicker hole_np;
    NumberPicker par_np;
    ArrayList<String> PlayerNames = new ArrayList<String>();
    private boolean edit_hole = false;
    ArrayList<Integer> hole_list = new ArrayList<>();
    public add_hole(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.add_hole, container, false);

        //grab player names, if any have been passed (should always be the case)
        PlayerNames = this.getArguments().getStringArrayList("pn");

        //use to following segment for assigning onClickListeners
        final Button back = (Button)  myView.findViewById(R.id.add_hole_back);
        back.setOnClickListener(this);
        final Button done = (Button) myView.findViewById(R.id.add_hole_next);
        done.setOnClickListener(this);

        hole_np = (NumberPicker) myView.findViewById(R.id.hole_np);
        hole_list = this.getArguments().getIntegerArrayList("hole_list");
        edit_hole = this.getArguments().getBoolean("edit_hole");
        Integer new_hole_num;
        if(hole_list.size() == 0){
            new_hole_num = 1;
        } else if (edit_hole) {
            new_hole_num = hole_list.get(hole_list.size() - 1);
        } else{
            new_hole_num = hole_list.get(hole_list.size() - 1) + 1;
        }

        String nums1[] = new String[27];
        for(int i = 0; i < nums1.length; i++){
            nums1[i] = Integer.toString(i + 1);
        }
        hole_np.setMinValue(1);
        hole_np.setMaxValue(27);
        hole_np.setWrapSelectorWheel(false);
        hole_np.setDisplayedValues(nums1);
        hole_np.setValue(new_hole_num);

        par_np = (NumberPicker) myView.findViewById(R.id.par_np);

        String nums[] = new String[27];
        for(int i = 0; i < nums.length; i++){
            nums[i] = Integer.toString(i + 1);
        }
        par_np.setMinValue(1);
        par_np.setMaxValue(10);
        par_np.setWrapSelectorWheel(false);
        par_np.setDisplayedValues(nums);
        par_np.setValue(3);

        return myView;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.add_hole_back:

                FragmentManager fragmentManager = getFragmentManager();
                Bundle args = new Bundle();
                args.putStringArrayList("pn", PlayerNames);
                if(edit_hole){
                    args.putBoolean("edit_hole", edit_hole);
                }
                second_fragment sf = new second_fragment();
                sf.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, sf)
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.add_hole_next:

                    int hole_num = hole_np.getValue();
                    int par_num = par_np.getValue();
                    boolean valid_hole = true;
                    try {
                        edit_hole = this.getArguments().getBoolean("edit_hole");
                    } catch (NullPointerException NPE) {
                        //
                    }

                    if (edit_hole) {
                        ArrayList<Integer> hole_list = this.getArguments().getIntegerArrayList("hole_list");
                        for (int i = 0; i < hole_list.size(); i++) {
                            if (hole_num == hole_list.get(i)) {
                                valid_hole = true;
                                break;
                            } else {
                                valid_hole = false;
                            }
                        }
                    } else {
                        ArrayList<Integer> hole_list = this.getArguments().getIntegerArrayList("hole_list");
                        for (int i = 0; i < hole_list.size(); i++) {
                            if (hole_num != hole_list.get(i)) {
                                valid_hole = true;
                            } else {
                                valid_hole = false;
                                break;
                            }
                        }
                    }
                    if (!valid_hole) {
                        if (edit_hole) {
                            Toast.makeText(getActivity(), "Please select a valid hole number to edit.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Hole already exists. Please go back to edit, or add a new hole.", Toast.LENGTH_LONG).show();
                        }
                        break;
                    }

                    ArrayList<Integer> hole_par = new ArrayList<>();

                    hole_par.add(0, 0);
                    //next two entries are intuitive
                    hole_par.add(1, hole_num);
                    hole_par.add(2, par_num);

                    //next entry is a placeholder for set_player_score
                    hole_par.add(3, PlayerNames.size());

                    FragmentManager fragmentManager1 = getFragmentManager();
                    Bundle args1 = new Bundle();

                    //now add all data (player names, hole #, par) to args1 bundle
                    args1.putStringArrayList("pn", PlayerNames);
                    args1.putIntegerArrayList("hp", hole_par);
                    args1.putIntegerArrayList("hole_list", hole_list);
                    args1.putBoolean("edit_hole", edit_hole);

                    //now instantiate next fragment and add args1 bundle to it, and set fragment
                    set_player_score sps = new set_player_score();
                    sps.setArguments(args1);
                    fragmentManager1.beginTransaction()
                            .replace(R.id.content_frame, sps)
                            .addToBackStack(null)
                            .commit();

                    break;

            default:
                break;
        }
    }
}
