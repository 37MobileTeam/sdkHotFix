package com.sq.mobile.pluhost;

import android.content.Context;
import android.util.Log;

import com.sq.mobile.commonpluhostandsqsdk.BasePluginInterface;
import com.sq.mobile.commonpluhostandsqsdk.SQSdkInterface;
import com.sq.plugin.core.Plugin;
import com.sq.plugin.core.PluginManager;

import java.lang.reflect.Method;

public class BasePluSqWanCore extends BasePluginInterface implements SQSdkInterface {

    /**
     * 以前的SDK，经过处理后的名字
     * （1）没有热修之前，我们的SDK对客户的是《SqWanCore》
     * （2）因为插件化，以前对外的SDK被弄成了插件，提供给宿主使用，而宿主统一对外
     * 那么，就需要把以前的《SqWanCore》换个名字叫《SQwanCoreImpl》提供给宿主使用，
     * 这样宿主那边就好用《SqWanCore》来给客户提供不变的接口，实现兼容
     */
    private final String SDK_CLASS = "com.sq.mobile.sqsdk.SqWanCoreImpl";

    private final String SDK_GET_METHOD = "getInstance";

    /**
     * SDK 对外接口
     */
    private SQSdkInterface mSdk;


    /**
     * 插件
     */
    public Plugin mPlugin;

    /**
     * 上下文
     */
    public Context mContext;


    public BasePluSqWanCore(Context context) {
        mContext = context;


        try {
            //加载插件
            mPlugin = PluginManager.getInstance(mContext).loadPlugin();
            //从插件里面获得接口（SQSdkInterface）的具体实现
            Class sdkClass = mPlugin.mClassLoader.loadClass(SDK_CLASS);
            Method sdkGetMethod = sdkClass.getMethod(SDK_GET_METHOD);
            mSdk = (SQSdkInterface) sdkGetMethod.invoke(null);
        } catch (Exception e) {
            //ClassNotFoundException, NoSuchMethodException,
            //InvocationTargetException, IllegalAccessException
            e.printStackTrace();
            Log.e("37手游安卓团队", e.toString());
        }
    }


    @Override
    public void init() {
        if (mSdk != null) {
            mSdk.init();
        }
    }

    @Override
    public void login() {
        if (mSdk != null) {
            mSdk.login();
        }
    }

    @Override
    public void pay() {
        if (mSdk != null) {
            mSdk.pay();
        }
    }

}
