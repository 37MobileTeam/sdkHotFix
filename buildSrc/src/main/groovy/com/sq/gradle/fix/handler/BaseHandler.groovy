package com.sq.gradle.fix.handler

import javassist.ClassPool
import javassist.CtClass

abstract class BaseHandler {

    abstract boolean handle(ClassPool pool, CtClass ctClass);

}
