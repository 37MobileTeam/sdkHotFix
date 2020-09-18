package com.sq.gradle.fix.core

import com.sq.gradle.fix.common.ClassFileTransform
import com.sq.gradle.fix.common.CtClassBean
import com.sq.gradle.fix.common.LogUtils
import com.sq.gradle.fix.config.ConfigBean
import com.sq.gradle.fix.handler.ActivityHandler
import com.sq.gradle.fix.handler.ApplicationHandler
import com.sq.gradle.fix.handler.ReceiverHandler
import com.sq.gradle.fix.manifest.ManifestHelper
import javassist.ClassPool
import javassist.CtClass

class PluginHandler {

    private static PluginHandler sInstance

    private final static ClassPool pool = ClassPool.getDefault()

    private ManifestHelper manifestHelper

    private ClassFileTransform mClassFileTransform

    private ActivityHandler mActivityHandler

    private ApplicationHandler mAppHandler

    private ReceiverHandler mReceiverHandler

    //名称替换映射
    Map<String, String> nameMap

    //不处理的第三方activity过滤
    Set<String> mActivityFilter

    //需要删除的class列表，规避虚拟机加载规则
    Set<String> mDeleteClassList

    //第三方不打入插件的jar包过滤
    Set<String> mFilterLocalJar

    private PluginHandler() {
        mClassFileTransform = new ClassFileTransform(pool)
        manifestHelper = new ManifestHelper()
        mActivityHandler = new ActivityHandler()
        mReceiverHandler = new ReceiverHandler()
        mAppHandler = new ApplicationHandler()
    }

    void init(String manifestPath, ConfigBean configBean) {
        manifestHelper.init(manifestPath)

        nameMap = configBean.nameMap
        if (nameMap != null && nameMap.size() > 0) {
            println "=====PluginHandler=====${nameMap.size()}"
        } else {
            println "=====PluginHandler=====nameMap is null..."
        }
        mActivityFilter = configBean.activityFilter
        mFilterLocalJar = configBean.filterLocalJar
        mDeleteClassList = configBean.deleteClassList
    }

    void appendClassPath(String path) {
        pool.appendClassPath(path)
        mClassFileTransform.addFilePath(path)
    }

    void init() {
        mClassFileTransform = new ClassFileTransform(pool)
    }

    static PluginHandler getInstance() {
        if (sInstance == null) {
            sInstance = new PluginHandler()
        }
        return sInstance
    }

    @Override
    int hashCode() {
        return super.hashCode()
    }

    void handleInputFile() {
        Map<String, CtClassBean> map = mClassFileTransform.toCtClass()
        for (String key : map.keySet()) {
            CtClassBean ctClassBean = map.get(key)
            CtClass ctClass = ctClassBean.ctClass
            if (!ctClass.name.contains("\$")) {
                handleClass(ctClassBean)
            }
        }
        for (String classKey : map.keySet()) {
            CtClassBean deleteClassBean = map.get(classKey)
            if (deleteFileFilter(deleteClassBean)) {
                deleteClassBean.deleteFile()
            }
        }
    }

    /**
     * 返回true代表需要删除
     * @param classBean
     * @return
     */
    boolean deleteFileFilter(CtClassBean classBean) {
        String className = classBean.ctClass.name
        //xxx.*
        for (String scheme : mDeleteClassList) {
            if (scheme.endsWith("*")) {
                String newScheme = scheme.replace("*", "")
                if (className.startsWith(newScheme)) {
                    return true
                }
            } else if (scheme.equals(className)) {
                return true
            }
        }
        return false
    }

    /**
     * 类名修改
     * 类是否引用了替换名称的类, 是的话替换
     * 类是否是替换名称的本类，是的话替换名称时还需要把匿名内部类的名称一起换了，加到map中
     * 内部类处理
     * @param ctClassBean
     * @param nameMap
     */
    void handleClass(CtClassBean ctClassBean) {
        //处理自身包名替换
        CtClass ctClass = ctClassBean.ctClass
        if (ctClass.isFrozen()) {
            ctClass.defrost()
        }
        //处理组件
        handleCompenent(ctClass)
        //替换包名类名
        Map<String, String> nestNameMap = new HashMap<>()
        for (String key : nameMap.keySet()) {
            if (ctClass.getRefClasses() != null && ctClass.getRefClasses().contains(key)) {
                //如果替换的名称是本类，还需处理本类的匿名内部类
                if (ctClass.name.equals(key)) {
                    CtClass[] renameNestClasses = ctClass.getNestedClasses()
                    if (renameNestClasses != null) {
                        for (int i = 0; i < renameNestClasses.size(); i++) {
                            String nestName = renameNestClasses[i].name
                            String newNestName = nestName.replace(key, nameMap.get(key))
                            nestNameMap.put(nestName, newNestName)
                            //这里是为了pool中能有换名了的内部类
                            renameNestClasses[i].replaceClassName(nestName, newNestName)
                            //换掉类中内部类名称索引
                            ctClass.replaceClassName(nestName, newNestName)
                        }
                    }
                }
                ctClass.replaceClassName(key, nameMap.get(key))
            }
        }
        //TODO 内部类足够多的情况下性能不太好,待优化
        nameMap.putAll(nestNameMap)
        //处理内部类
        CtClass[] ctClasses = ctClass.getNestedClasses()
        for (int i = 0; i < ctClasses.size(); i++) {
            try {
                CtClassBean ctNestedClassBean = new CtClassBean(ctClasses[i], ctClassBean.path, ctClassBean.type)
                handleClass(ctNestedClassBean)
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
        //如果修改过则需要保存
        if (ctClass.isModified()) {
            ctClassBean.saveFile()
        } else {
            ctClass.detach()
        }
    }

    /**
     * 处理组件
     * @param ctClass
     */
    void handleCompenent(CtClass ctClass) {

        if (manifestHelper.mActivities.contains(ctClass.name) && !mActivityFilter.contains(ctClass.name)) {
            mActivityHandler.handle(pool, ctClass)
        }

        if (manifestHelper.mApplicationName.equals(ctClass.name)) {
            mAppHandler.handle(pool, ctClass)
        }

        if (manifestHelper.mReceivers.contains(ctClass.name)) {
            mReceiverHandler.handle(pool, ctClass)
        }

    }

    boolean fileLocalJar(String path) {
        for (String jarName : mFilterLocalJar) {
            if (path.endsWith(jarName)) {
                return true
            } else if (path.endsWith("classes.jar") && path.contains(jarName)) {
                return true
            }
        }
        return false
    }


}
