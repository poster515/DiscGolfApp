package com.scoresheet.discgolf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by Joe Post on 5/28/2017.
 */

public class CourseDeleteConfirmationDialogBox extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure want to proceed? This will delete the entire current course.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            notifyToTarget(2);

                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    notifyToTarget(Activity.RESULT_CANCELED);
                                }
                            });
            return builder.create();
        }

        private void notifyToTarget(int code) {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment != null) {
                targetFragment.onActivityResult(getTargetRequestCode(), code, null);
//            DatabaseHelperScoringTable dbh = new DatabaseHelperScoringTable(getActivity());
//            dbh.done();
            }
        }
    }

