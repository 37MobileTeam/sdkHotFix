package com.sq.gradle.fix.handler

import javassist.ClassPool
import javassist.CtClass

/**
 * 处理Activity代理
 */
class ActivityHandler extends BaseHandler{

    @Override
    boolean handle(ClassPool pool, CtClass ctClass) {
        try {
            CtClass baseActivity = pool.get("com.sq.plugin.standard.BaseActivity")
            ctClass.setSuperclass(baseActivity)
            ctClass.replaceClassName("com.sq.plugin.standard.RealBaseActivity", "com.sq.plugin.standard.BaseActivity")
        } catch(Exception e) {
            return false
        }
        return true
    }

}
