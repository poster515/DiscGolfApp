package com.scoresheet.discgolf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by Joe Post on 5/25/2017.
 * This serves as the pop-up for adding a new disc golf course to the list of available disc golf courses.
 */


public class AddCoursetoCourseListTablePopup extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Add New Disc Golf Course");

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.add_course_to_course_list_dialog, null);
        final EditText DGcoursename = (EditText) view.findViewById(R.id.newDGcourseName);

        builder.setView(view)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        notifyToTarget(Activity.RESULT_OK, DGcoursename.getText().toString());

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                notifyToTarget(Activity.RESULT_CANCELED, "NO_NAME_ADDED");
                            }
                        });
        return builder.create();
    }

    private void notifyToTarget(int code, String name) {
        Fragment targetFragment = getTargetFragment();
        if (targetFragment != null) {
            Intent intent = new Intent();

            ArrayList<String> name_list = new ArrayList<>();
            name_list.add(0, name);

            intent.putStringArrayListExtra("name_array", name_list);

            targetFragment.onActivityResult(getTargetRequestCode(), code, intent);
        }
    }
}

