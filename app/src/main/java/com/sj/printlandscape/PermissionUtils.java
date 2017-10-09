package com.sj.printlandscape;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by SiddharthJain on 7/17/2017.
 */

public class PermissionUtils {

    private static final int REQUEST_CODE_STORAGE = 101;

    public static boolean hasStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void askStoragePermission(AppCompatActivity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
    }

    public static boolean storagePermissionGranted(int requestCode, int[] grantResults) {
        return requestCode == REQUEST_CODE_STORAGE && isPermissionGranted(grantResults);
    }

    private static boolean isPermissionGranted(int[] grantResults) {
        return grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
