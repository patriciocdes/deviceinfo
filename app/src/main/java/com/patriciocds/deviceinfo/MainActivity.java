package com.patriciocds.deviceinfo;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String DEVICE_INFO_DESC = "DEVICE_INFO";
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 1001;

    private WindowManager windowManager;
    private ActivityManager activityManager;

    private View screenBrightnessOverlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        windowManager = (WindowManager)  getSystemService(Context.WINDOW_SERVICE);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<String> installedApplications = getInstalledApplications();
        Collections.sort(installedApplications);

        AppListAdapter appListAdapter = new AppListAdapter(installedApplications);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(appListAdapter);

        printLog("onCreate called");
        printMemoryInfo();

        //Configurando o brilho da tela para 80%
        configScreenBrightness(0.8f);
    }

    @Override
    protected void onStart() {
        super.onStart();
        printLog("onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        printLog("onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        printLog("onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        printLog("onStop called");
    }

    @Override
    protected void onDestroy() {
        printMemoryInfo();

        super.onDestroy();

        if (screenBrightnessOverlayView != null) {
            windowManager.removeView(screenBrightnessOverlayView);
            screenBrightnessOverlayView = null;
        }

        activityManager = null;
        windowManager = null;

        printLog("onDestroy called");
    }

    private void printMemoryInfo() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(memoryInfo);

        long total = memoryInfo.totalMem / (1024 * 1024);
        long avail = memoryInfo.availMem / (1024 * 1024);

        printLog("Total memory: " + total + " MB | Available memory: " + avail + " MB");
    }

    @SuppressLint("QueryPermissionsNeeded")
    private List<String> getInstalledApplications() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        List<String> result = new ArrayList<>();

        for (ApplicationInfo appInfo : apps) {
            String appName = pm.getApplicationLabel(appInfo).toString();
            result.add(appName);
        }

        return result;
    }

    private void configScreenBrightness(float value) {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));

            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
        } else {
            setScreenBrightness(value);
        }
    }

    private void setScreenBrightness(float value) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.screenBrightness = value;

        screenBrightnessOverlayView = new View(this);
        screenBrightnessOverlayView.setBackgroundColor(Color.TRANSPARENT);

        windowManager.addView(screenBrightnessOverlayView, params);
    }

    private void printLog(String message, boolean isError) {
        if (isError) {
            Log.e(DEVICE_INFO_DESC, message);
        } else {
            Log.d(DEVICE_INFO_DESC, message);
        }
    }

    private void printLog(String message) {
        printLog(message, false);
    }
}