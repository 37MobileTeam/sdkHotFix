package com.sq.mobile.commonpluhostandsqsdk;

import android.content.Intent;
import android.content.res.Resources;



public class BasePluginInterface {

    public void startActivity(Intent intent) {

    }

    public void startActivityForResult(Intent intent, int requestCode) {

    }

    public Resources getResources() {
        return null;
    }

    public ClassLoader getClassLoader() {
        return null;
    }

    public boolean isSupportPlugin() {
        return false;
    }

}
