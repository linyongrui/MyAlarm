package com.example.myalarm.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static boolean permissionCheck(Activity activity, String[] permissions, int requestCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionGranted = PackageManager.PERMISSION_GRANTED;
            for (String permission : permissions) {
                permissionGranted = ContextCompat.checkSelfPermission(activity.getApplicationContext(), permission);
                if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            }
            if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
                return false;
            }
        }
        return true;
    }

    public static boolean prantResultCheck(int[] grantResults) {
        boolean isGrant;
        if (grantResults != null) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            isGrant = true;
        } else {
            isGrant = false;
        }
        return isGrant;
    }
}