package com.sq.gradle.fix.common

import javassist.CtClass

class CtClassBean {

    static final int TYPE_JAR_FILE = 0

    static final int TYPE_CLASS_FILE = 1

    /**
     * CtClass类
     */
    CtClass ctClass

    /**
     * 路径
     */
    String path

    /**
     * 类型，是jar里解析的file，还是文件夹中的
     */
    int type

    /**
     * CtClass初始名称
     */
    String originalName

    /**
     * 内部类
     */
    Set<String> nestClassNames

    /**
     *
     * @param ctClass
     * @param path
     * @param type
     */
    CtClassBean(CtClass ctClass, String path, int type) {
        this.ctClass = ctClass
        this.originalName = ctClass.name
        this.path = path
        this.type = type
        CtClass[] nestClasses = this.ctClass.nestedClasses
        if (nestClasses != null && nestClasses.size() > 0) {
            nestClassNames = new HashSet<>()
            nestClasses.each {
                nestClassNames.add(it.name)
            }
        }
    }

    /*
     * 保存CtClass文件
     */
    void saveFile(){
        if (type == TYPE_CLASS_FILE) {
            if (ctClass != null) {
                try {
                    if (!originalName.equals(ctClass.name)) {
                        //删除原先类
                        String originalFilePath = path + File.separator + (originalName.replace(".", File.separator)) + ".class"
                        File originalFile = new File(originalFilePath)
                        if (originalFile.exists()) {
                            originalFile.delete()
                        }
                        if (nestClassNames != null) {
                            nestClassNames.each {
                                String nestClassOriginalFilePath = path + File.separator + (it.replace(".", File.separator)) + ".class"
                                File nestClassOriginalFile = new File(nestClassOriginalFilePath)
                                if (nestClassOriginalFile.exists()) {
                                    nestClassOriginalFile.delete()
                                }
                            }
                        }

                    }
                    ctClass.writeFile(path)
                    ctClass.detach()
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }
        } else {
            //是jar里面的
            if (ctClass != null) {
                if (!originalName.equals(ctClass.name)) {
                    ArrayList<String> originalEntryNameList = new ArrayList<>()
                    String originalEntryName = originalName.replace(".", File.separator) + ".class"
                    originalEntryNameList.add(originalEntryName)
                    String entryName = ctClass.name.replace(".", File.separator) + ".class"
                    if (nestClassNames != null) {
                        nestClassNames.each {
                            String nestClassOriginalEntryName = it.replace(".", File.separator) + ".class"
                            originalEntryNameList.add(nestClassOriginalEntryName)
                        }
                    }
                    JarFileUtils.replaceFileAndDeleteOldFile(path, entryName, ctClass.toBytecode(), originalEntryNameList)
                } else {
                    String entryName = ctClass.name.replace(".", File.separator) + ".class"
                    JarFileUtils.replaceFile(path, entryName, ctClass.toBytecode())
                }
                ctClass.detach()
            }
        }
    }

    void deleteFile() {
        if (type == TYPE_CLASS_FILE) {
            if (ctClass != null) {
                String classFilePath = path + File.separator + (ctClass.name.replace(".", File.separator)) + ".class"
                File classFile = new File(classFilePath)
                if (classFile.exists()) {
                    classFile.delete()
                }
                if (nestClassNames != null) {
                    nestClassNames.each {
                        String nestClassFilePath = path + File.separator + (it.replace(".", File.separator)) + ".class"
                        File nestClassFile = new File(nestClassFilePath)
                        if (nestClassFile.exists()) {
                            nestClassFile.delete()
                        }
                    }
                }
            }
        } else {
            if (ctClass != null) {
                ArrayList<String> entryNameList = new ArrayList<>()
                String entryName = ctClass.name.replace(".", File.separator) + ".class"
                entryNameList.add(entryName)
                if (nestClassNames != null) {
                    nestClassNames.each {
                        String nestClassEntryName = it.replace(".", File.separator) + ".class"
                        entryNameList.add(nestClassEntryName)
                    }
                }
                JarFileUtils.deleteEntry(path, entryNameList)
            }
        }
    }
}
