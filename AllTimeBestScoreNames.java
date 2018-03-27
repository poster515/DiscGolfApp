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
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Joe Post on 6/5/2017.
 */

public class AllTimeBestScoreNames extends Fragment implements View.OnClickListener {
    View myView;
    public AllTimeBestScoreNames(){

    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        myView = inflater.inflate(R.layout.best_names_for_course, container, false);

        final DatabaseHelperScoringTable DBHscoringtable = new DatabaseHelperScoringTable(getActivity());

        final Button back_button = (Button) myView.findViewById(R.id.back_button_list_of_names);
        back_button.setOnClickListener(this);

        Bundle args = this.getArguments();

        final int course_id = args.getInt("course_id");

        final List<String> AllNamesForSpecificCourse =
                DBHscoringtable.getPlayersForSpecificCourse(course_id);

        //implement a ListView adapter
        ListView mListView = (ListView) myView.findViewById(R.id.allnamesforbestscores);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, AllNamesForSpecificCourse);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Bundle args = new Bundle();
                final int unique_course_id =
                        DBHscoringtable.getBestIDForSpecificCourseAndName(
                                course_id, AllNamesForSpecificCourse.get(position));

                args.putInt("unique_course_id", unique_course_id);
                args.putInt("course_id", course_id);
                args.putString("name", AllNamesForSpecificCourse.get(position));
                //now, send the user to a fragment similar to second_fragment to view that course

                //need to change the below to take you to a new fragment displaying the exact course stats, similar to second_fragment
                FragmentManager fragmentManager2 = getFragmentManager();
                BestCourseDisplayPage bcdp = new BestCourseDisplayPage();
                bcdp.setArguments(args);

                fragmentManager2.beginTransaction()
                        .replace(R.id.content_frame, bcdp)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return myView;
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button_list_of_names:
                FragmentManager fragmentManager2 = getFragmentManager();
                fragmentManager2.beginTransaction()
                        .replace(R.id.content_frame, new AllTimeBestCourseScores())
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
}
