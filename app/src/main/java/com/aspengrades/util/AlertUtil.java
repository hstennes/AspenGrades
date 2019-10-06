package com.aspengrades.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.aspengrades.data.StudentList;
import com.aspengrades.main.R;

import java.util.ArrayList;

public class AlertUtil {

    /**
     * Shows an alert dialog indicating that the aspen session has expired
     * @param activity The activity showing the dialog
     */
    public static void showSessionExpiredAlert(final Activity activity, final SessionExpiredCallback callback){
        if(activity.isFinishing()) return;
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.alert_title_session_expired))
                .setMessage(activity.getString(R.string.alert_text_session_expired))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onPositiveButton();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onNegativeButton();
                    }
                })
                .show();
    }

    /**
     * Shows an alert dialog that allows the user (who must be a parent) to select a student
     * @param activity The activity showing the dialog
     * @param students A HashMap mapping student names to student OIDs
     * @param studentOid The student OID of the student currently being shown
     */
    public static void showStudentSelector(Activity activity, final StudentSelectorCallback callback,
                                           final StudentList students, String studentOid){
        if(activity.isFinishing()) return;

        String[] names = students.getNames().toArray(new String[1]);
        final ArrayList<String> ids = students.getIds();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Student");
        builder.setSingleChoiceItems(names, ids.indexOf(studentOid), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                callback.onStudentSelected(ids.get(item));
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * An interface for using showStudentSelector
     */
    public interface StudentSelectorCallback {
        void onStudentSelected(String studentOid);
    }

    /**
     * An interface for using showSessionExpiredAlert
     */
    public interface SessionExpiredCallback {
        void onPositiveButton();
        void onNegativeButton();
    }
}