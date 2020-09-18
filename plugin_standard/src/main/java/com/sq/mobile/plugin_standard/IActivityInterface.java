package com.sq.mobile.plugin_standard;

import android.content.Context;

public interface IActivityInterface {

    void insertAppContext(android.app.Activity activity);

    void onCreate(android.os.Bundle bundle);

    <T extends android.view.View> T findViewById(int i);

    void onResume();

    void onStart();

    void onStop();

    void onDestroy();

    void startActivity(android.content.Intent intent);

    void setContentView(int i);

    android.content.Intent getIntent();

    android.view.Window getWindow();

    Context getContext();

    void finish();

    boolean isFinishing();

    void startActivityForResult(android.content.Intent intent, int i);

    void onActivityResult(int i, int i1, android.content.Intent intent);

    boolean onKeyDown(int i, android.view.KeyEvent keyEvent);

    String getPackageName();

    Context getApplicationContext();

}
