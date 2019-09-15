package com.aspengrades.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import com.aspengrades.data.LoginManager;
import com.aspengrades.main.ClassesActivity;
import com.aspengrades.main.R;

public class AlertUtil {

    public static void showSessionExpiredAlert(final Activity activity){
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.alert_title_session_expired))
                .setMessage(activity.getString(R.string.alert_text_session_expired))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startClassesActivity(activity);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .show();
    }

    private static void startClassesActivity(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(
                activity.getString(R.string.credentials_file_key),
                Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(activity.getString(R.string.saved_username_key), "");
        String password = sharedPreferences.getString(activity.getString(R.string.saved_password_key), "");
        String name = sharedPreferences.getString(activity.getString(R.string.saved_name_key), LoginManager.DEFAULT_NAME);
        boolean isParentAccount = sharedPreferences.getBoolean(activity.getString(R.string.saved_is_parent_key), false);
        Intent intent = new Intent(activity, ClassesActivity.class);
        intent.putExtra(activity.getString(R.string.saved_username_key), username);
        intent.putExtra(activity.getString(R.string.saved_password_key), password);
        intent.putExtra(activity.getString(R.string.saved_name_key), name);
        intent.putExtra(activity.getString(R.string.saved_is_parent_key), isParentAccount);
        activity.startActivity(intent);
    }
}
