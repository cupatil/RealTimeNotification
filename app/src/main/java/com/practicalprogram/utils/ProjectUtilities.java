package com.practicalprogram.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.practicalprogram.R;


public class ProjectUtilities {

    public static ProgressDialog pDialog;

    // Define variable
    static Boolean isInternetPresent = false;
    static ConnectionDetector cd;


    // This method is for checking internet connection
    public static Boolean checkInternetAvailable(Context mContext) {
        cd = new ConnectionDetector(mContext);
        isInternetPresent = cd.isConnectingToInternet();

        return isInternetPresent;
    }

    public static void showToast(Context mContext, String message) {
        Toast.makeText(mContext, "" + message, Toast.LENGTH_SHORT).show();
    }

    //This method hide keyboard
    public static void hideKeyboard(Activity mActivity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) mActivity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mActivity
                    .getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    //this method show internet diaolg
    public static void internetDialog(Context mContext) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle(mContext.getResources()
                .getString(R.string.warning));
        alertDialog.setMessage(mContext.getResources().getString(
                R.string.internet_error));
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(
                mContext.getResources().getString(R.string.btn_ok),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    public static void showAlertDialog(Context mContext, String message) {

        if (mContext != null) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setCancelable(false);
            alertDialog.setTitle(mContext.getString(R.string.app_name));
            alertDialog.setMessage(message);

            alertDialog.setPositiveButton(
                    mContext.getResources().getString(R.string.btn_ok),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        }
    }


    public static boolean checkPermission(Context mContext) {
        String[] PERMISSIONS = new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        int size = 0;
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (PermissionChecker.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(mContext, PERMISSIONS[i])) {
                size++;
            }
        }

        return size == PERMISSIONS.length;
    }

}
