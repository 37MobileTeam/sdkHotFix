package com.sq.gradle.fix.common

import javassist.ClassPool
import javassist.CtClass

class ClassFileTransform {

    Set<String> mPathSet

    ClassPool pool

    ClassFileTransform(ClassPool pool) {
        mPathSet = new HashSet<>()
        this.pool = pool
    }

    Map<String, CtClassBean> toCtClass() {
        Map<String, CtClassBean> map = new HashMap<>()
        for (String path : mPathSet) {
            File ctClassDir = new File(path)
            if (ctClassDir.isDirectory()) {
                loopClassFile(ctClassDir, ctClassDir.path, map)
            } else {
                //是单个文件，是jar包
                Set<String> jarFileNameSet = JarFileUtils.deCopressName(path)
                for (String name : jarFileNameSet) {
                    if (name.endsWith(".class")) {
                        String className = name.replace(File.separator, ".").replace(".class", "")
                        CtClass ctClass = pool.getCtClass(className)
                        CtClassBean ctClassBean = new CtClassBean(ctClass, path, CtClassBean.TYPE_JAR_FILE)
                        map.put(className, ctClassBean)
                    }
                }
            }
        }
        return map
    }

    void loopClassFile(File dirFile, String dirPath, Map<String, CtClassBean> map) {
        if (dirFile.isDirectory()) {
            dirFile.eachFileRecurse { File file ->
                loopClassFile(file, dirPath, map)
            }
        } else {
            if (dirFile.name.endsWith(".class")) {
                //这里可以排除一下R文件的
                String smaliPath = dirFile.path.replace(dirPath, "")
                if (smaliPath.startsWith(File.separator)) {
                    smaliPath = smaliPath.substring(1)
                }
                String className = smaliPath.replace(File.separator, ".").replace(".class", "")
                CtClass ctClass = pool.getCtClass(className)
                CtClassBean ctClassBean = new CtClassBean(ctClass, dirPath, CtClassBean.TYPE_CLASS_FILE)
                map.put(dirFile.path, ctClassBean)
            }
        }
    }

    void addFilePath(String path) {
        mPathSet.add(path.toString())
    }

}
