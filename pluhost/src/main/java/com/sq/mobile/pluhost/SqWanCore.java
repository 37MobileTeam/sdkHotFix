package com.sq.mobile.pluhost;


import android.content.Context;

/**
 * SDK对外提供的接口
 */
public class SqWanCore extends BasePluSqWanCore {

    private volatile static SqWanCore instance;

    public static SqWanCore getInstance(Context context) {
        if (instance == null) {
            synchronized (SqWanCore.class) {
                if (instance == null) {
                    instance = new SqWanCore(context);
                }
            }
        }
        return instance;
    }

    public SqWanCore(Context context) {
        super(context);
    }


}
