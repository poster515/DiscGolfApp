package com.scoresheet.discgolf;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.scoresheet.discgolf.R.id.add_player;
import static com.scoresheet.discgolf.R.id.begin_scoring;

/**
 * Created by Joe Post on 4/5/2017.
 */

public class third_fragment extends Fragment implements View.OnClickListener{
    View myView;
    int num_players = 0;
    private ArrayList<String> listItems=new ArrayList<String>();
    List<String> player_names = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.third_layout, container, false);

        final Button addplayer = (Button) myView.findViewById(R.id.add_player);
        addplayer.setOnClickListener(this);

        final Button beginscoring = (Button) myView.findViewById(R.id.begin_scoring);
        beginscoring.setOnClickListener(this);

        final Button remove = (Button) myView.findViewById(R.id.remove);
        remove.setOnClickListener(this);

        //start by creating a new table instance
        TableLayout scoringtable = (TableLayout) myView.findViewById(R.id.scoringtable);

        //create new table row
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableLayout.LayoutParams( TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        //now create a new EditText
        EditText et = new EditText(getActivity());
        et.setHint("Enter Player Name Here...");

        //now add edittext to the tablerow
        tr.addView(et);

        //now add row to tablelayout
        scoringtable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        DatabaseHelperScoringTable dbh = new DatabaseHelperScoringTable(getActivity());
        boolean no_current_table = dbh.noCurrentCourse();
        if(!no_current_table) {
            Toast.makeText(getActivity(), "Caution: course already started. Players cannot be added mid-course.", Toast.LENGTH_LONG).show();
        }
        return myView;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case begin_scoring:
                DatabaseHelperScoringTable dbh = new DatabaseHelperScoringTable(getActivity());
                boolean no_current_table = dbh.noCurrentCourse();
                if(no_current_table) {
                    //create new string array
                    ArrayList<String> playernames = new ArrayList<String>();
                    //instantiate tablelayout
                    TableLayout table1 = (TableLayout) myView.findViewById(R.id.scoringtable);

                    //check for at least one player
                    if(table1.getChildCount() == 0){
                        Toast.makeText(getActivity(), "Please Add At Least One Player", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    boolean valid_name = true;
                    //now grab all views in the table
                    for (int i = 0; i <= table1.getChildCount(); i++){
                        //grab view at ith Child in table
                        View parentRow = table1.getChildAt(i);

                        if(parentRow instanceof TableRow) {
                            EditText ET = (EditText) ((TableRow) parentRow).getChildAt(0);
                            if (ET instanceof EditText) {
                                String text = ET.getText().toString();
                                if(text.equals("")){
                                    Toast.makeText(getActivity(), "Please enter a valid name.", Toast.LENGTH_SHORT).show();
                                    valid_name = false;
                                    break;
                                }
                                playernames.add(text);
                                //Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    //this is where the next step is going to be, calling the DatabaseHelperScoringTable
                    //class to help setup the next scoring table.
                    if(!valid_name){
                        break;
                    }
                    //send names to a new second_fragment fragment with via setArguments
                    second_fragment fragment = new second_fragment();
                    Bundle args = new Bundle();
                    args.putStringArrayList("pn", playernames);
                    fragment.setArguments(args);

                    //now that we've instantiated a new fragment and passed the string values, switch to
                    //the new fragment
                    FragmentManager fragmentManager2 = getFragmentManager();
                    try {
                        fragmentManager2.beginTransaction()
                                .replace(R.id.content_frame, fragment)
                                .addToBackStack(null)
                                .commit();
                    } catch (Exception e){
                        Log.e("Error: ", e.toString());
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Course already started. Please go to the 'Edit Current Course' tab and delete or resume", Toast.LENGTH_LONG).show();
                }
                break;
            case add_player:
                //start by creating a new table instance
                TableLayout scoringtable = (TableLayout) myView.findViewById(R.id.scoringtable);

                //create new table row
                TableRow tr = new TableRow(getActivity());
                tr.setLayoutParams(new TableLayout.LayoutParams( TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

                //now create a new EditText
                EditText et = new EditText(getActivity());
                et.setHint("Enter Player Name Here...");

                //now add edittext to the tablerow
                tr.addView(et);

                //now add row to tablelayout
                scoringtable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                break;
            case R.id.remove:
                TableLayout table2 = (TableLayout) myView.findViewById(R.id.scoringtable);
                if(table2.getChildCount() > 1) {
                    table2.removeViewAt(table2.getChildCount() - 1);
                } else {
                    Toast.makeText(getActivity(), "No names to remove.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
