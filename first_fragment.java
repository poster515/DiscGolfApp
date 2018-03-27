package com.scoresheet.discgolf;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe Post on 4/5/2017.
 */

public class first_fragment extends Fragment{
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.first_layout, container, false);

        DatabaseHelperScoringTable DBHscoringtable = new DatabaseHelperScoringTable(getActivity());

        final Bundle AllDGCourses = DBHscoringtable.getAllDoneCourses();
        final List<String> course_dates = AllDGCourses.getStringArrayList("date_list");
        final ArrayList<Integer> course_ids = AllDGCourses.getIntegerArrayList("course_id_list");

        //now, pass the course_ids array to the Course Manager: DatabaseHelper
        DatabaseHelper DBH = new DatabaseHelper(getActivity());

        //now get all course names, as strings, from the databasehelper
        final List<String> course_names = DBH.getAllDoneCoursesNames(course_ids);

        final List<String> final_names = new ArrayList<>();

        for(int i = 0; i < course_dates.size(); i++){
            final_names.add(i, course_names.get(i) + ", " + course_dates.get(i));
        }
        ListView mListView = (ListView) myView.findViewById(R.id.list_of_all_courses);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, final_names);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // ListView Clicked item index
//                int itemPosition     = position;
                // ListView Clicked item value
                String  itemValue    = (String) final_names.get(position);

                //construct bundle for the next fragment to display that course information
                Bundle args = new Bundle();
                args.putInt("unique_course_id", position);
                args.putString("unique_course_title", itemValue);
                //now, send the user to a fragment similar to second_fragment to view that course

//need to change the below to take you to a new fragment displaying the exact course stats, similar to second_fragment
                FragmentManager fragmentManager2 = getFragmentManager();
                singleCourseDisplayPage scdp = new singleCourseDisplayPage();
                scdp.setArguments(args);

                fragmentManager2.beginTransaction()
                        .replace(R.id.content_frame, scdp)
                        .addToBackStack(null)
                        .commit();
            }

        });
        return myView;
    }
}
