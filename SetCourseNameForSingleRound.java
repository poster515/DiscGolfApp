package com.scoresheet.discgolf;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Joe Post on 5/25/2017.
 */

public class SetCourseNameForSingleRound extends Fragment implements View.OnClickListener{
    private static final int REQ_CODE = 1;
    View myView;
    public SetCourseNameForSingleRound(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        myView = inflater.inflate(R.layout.course_selection, container, false);

        final Button add_hole = (Button) myView.findViewById(R.id.add_new_course_to_table_list);
        add_hole.setOnClickListener(this);

        DatabaseHelper DBH = new DatabaseHelper(getActivity());
        boolean no_current_courses;

        final List<String> AllDGCourses = DBH.getAllCoursesNames();

        if(AllDGCourses != null){
            no_current_courses = false;
        } else { no_current_courses = true; }

        if(!no_current_courses) {

            //implement a ListView adapter
            ListView mListView = (ListView) myView.findViewById(R.id.course_name_listview);

            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, AllDGCourses);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition     = position;

                    //need to mark all not_done scoringtable entries with the _id of the table selected
                    DatabaseHelperScoringTable DBH = new DatabaseHelperScoringTable(getActivity());
                    DBH.done(itemPosition);

                    FragmentManager fragmentManager2 = getFragmentManager();
                    fragmentManager2.beginTransaction()
                            .replace(R.id.content_frame, new first_fragment())
                            .addToBackStack(null)
                            .commit();
                }

            });
        }

        return myView;
    }
    public void onClick(View v) {
//        TableLayout table = (TableLayout) myView.findViewById(course_name_list);
        if (v.getId() == R.id.add_new_course_to_table_list) {
            AddCoursetoCourseListTablePopup actcltp = new AddCoursetoCourseListTablePopup();
            actcltp.setTargetFragment(SetCourseNameForSingleRound.this, REQ_CODE);
            actcltp.show(getFragmentManager(), "course_add");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == -1){
            String name = data.getStringArrayListExtra("name_array").get(0);

            DatabaseHelper DBH = new DatabaseHelper(getActivity());
            DBH.addNewCourseName(name);

            //now refresh the fragment so the new name will appear
            FragmentManager fragmentManager2 = getFragmentManager();
            fragmentManager2.beginTransaction()
                    .replace(R.id.content_frame, new SetCourseNameForSingleRound())
                    .addToBackStack(null)
                    .commit();
            //Toast.makeText(getActivity(), "You hit the Yes button.", Toast.LENGTH_SHORT).show();
        }
    }
}
