package com.sq.gradle.fix.handler

import javassist.ClassPool
import javassist.CtClass

/**
 * 处理Receiver代理
 */
class ReceiverHandler extends BaseHandler{

    @Override
    boolean handle(ClassPool pool, CtClass ctClass) {
        try {
            CtClass baseBroadcastReceiver = pool.get("com.sq.plugin.standard.BaseBroadcastReceiver")
            ctClass.setSuperclass(baseBroadcastReceiver)
        } catch(Exception e) {
            return false
        }
        return true
    }

}
