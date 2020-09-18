package com.sq.plugin.core;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sq.mobile.plugin_standard.IActivityInterface;
import com.sq.plugin.core.loader.ApkClassLoader;


public class ProxyPluginActivity extends Activity {

    private final String TAG = getClass().getSimpleName();

    @Override
    public ApkClassLoader getClassLoader() {
        return null;
    }

    @Override
    public Resources getResources() {
        return null;
    }

    private IActivityInterface pluginActivity;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        if (intent != null && !TextUtils.isEmpty(intent.getStringExtra("activity"))) {
            try {
                pluginActivity = getClassLoader().getInterface(IActivityInterface.class, intent.getStringExtra("activity"));
                pluginActivity.insertAppContext(this);
                pluginActivity.onCreate(new Bundle());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "intent 中没带插件activity信息");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (pluginActivity != null){
            pluginActivity.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pluginActivity != null){
            pluginActivity.onResume();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (!TextUtils.isEmpty(intent.getStringExtra("activity"))) {
            intent.setClass(this, ProxyPluginActivity.class);
        }
        super.startActivity(intent);
    }
}
