package com.sq.gradle.fix.handler

import com.sq.gradle.fix.common.LogUtils
import javassist.ClassPool
import javassist.CtClass

/**
 * 处理Application代理
 * 父类替换为BaseApplication,目的是去掉super的逻辑,并且改名为SQApplicationImpl，改名是为了保持给cp使用的依旧是SQApplication
 */
class ApplicationHandler extends BaseHandler {

    @Override
    boolean handle(ClassPool pool, CtClass ctClass) {
        CtClass applicationAdapter = pool.get("com.sq.plugin.standard.BaseApplication")
        LogUtils.w("处理application: $ctClass.name")
        ctClass.setSuperclass(applicationAdapter)
        ctClass.replaceClassName("android.app.Application", "com.sq.plugin.standard.BaseApplication")
        return true
    }
}
