package com.sq.mobile.sqsdk;


/**
 * SDK对外提供的接口
 */
public class SqWanCore extends BaseSqWanCore {

    private volatile static SqWanCore instance;

    public static SqWanCore getInstance() {
        if (instance == null) {
            synchronized (SqWanCore.class) {
                if (instance == null) {
                    instance = new SqWanCore();
                }
            }
        }
        return instance;
    }


}
