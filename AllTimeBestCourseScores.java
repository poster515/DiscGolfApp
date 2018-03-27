package com.scoresheet.discgolf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe Post on 5/28/2017.
 */

public class AllTimeBestCourseScores extends Fragment{
    private static final int REQ_CODE = 1;

    View myView;
    public AllTimeBestCourseScores(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        myView = inflater.inflate(R.layout.best_course_scores, container, false);

        final DatabaseHelper DBH = new DatabaseHelper(getActivity());
        final DatabaseHelperScoringTable DBHscoringtable = new DatabaseHelperScoringTable(getActivity());

        boolean no_current_courses;

        final List<String> AllDGCoursesNames = DBH.getAllCoursesNames();

        if(AllDGCoursesNames != null){
            no_current_courses = false;
        } else { no_current_courses = true; }

        if(!no_current_courses) {

            //implement a ListView adapter
            ListView mListView = (ListView) myView.findViewById(R.id.allcoursesforbestscores);

            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, AllDGCoursesNames);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final Bundle args = new Bundle();
                    args.putInt("course_id", position);

                    FragmentManager fragmentManager2 = getFragmentManager();
                    AllTimeBestScoreNames atbcs = new AllTimeBestScoreNames();
                    atbcs.setArguments(args);

                    fragmentManager2.beginTransaction()
                            .replace(R.id.content_frame, atbcs)
                            .addToBackStack(null)
                            .commit();

                    }
            });
        } else {
            Toast.makeText(getActivity(), "Start and save new round to view best scores.", Toast.LENGTH_SHORT).show();
        }

        return myView;
    }
}
