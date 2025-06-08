package com.example.myalarm.util;

import static android.content.Context.WINDOW_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    public static boolean grantResultCheck(int[] grantResults) {
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

    public static void jumpToPermissionGrant(Activity activity, String action) {
        Intent intent = new Intent(action);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static ActivityResultLauncher getActivityResultLauncher(ComponentActivity componentActivity) {
        return componentActivity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() != RESULT_OK) {
//                            Log.i("terry", "registerForActivityResult");
//                            jumpToPermissionGrant(componentActivity, Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//                        }
                    }
                });
    }

    public static View overlayPermissionCheck(Context context, ActivityResultLauncher mDrawOverAppsLauncher, Integer resource) {
        View view = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                Log.i("terry", "overlayPermissionCheck");
                mDrawOverAppsLauncher.launch(intent);
            } else {
                if (resource != null) {
                    Log.i("terry", "overlayPermissionCheck2");
                    view = showFloatingWindow(context, resource);
                }
            }
        }
        return view;
    }

    public static View showFloatingWindow(Context context, int resource) {
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        // 设置悬浮窗的位置
        params.gravity = Gravity.TOP | Gravity.START; // 例如，设置在屏幕左上角
        params.x = 100; // x坐标
        params.y = 100; // y坐标

        // 创建浮动窗口视图
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View floatingView = inflater.inflate(resource, null);

        // 获取WindowManager
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

        try {
            // 添加悬浮窗到WindowManager
            windowManager.addView(floatingView, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return floatingView;
    }

    public static void removeFloatingWindow(Context context, View floatingView) {
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        if (floatingView != null && windowManager != null) {
            windowManager.removeView(floatingView);
        }
    }
}