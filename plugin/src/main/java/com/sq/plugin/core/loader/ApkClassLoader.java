package com.sq.plugin.core.loader;

import android.os.Build;

import dalvik.system.DexClassLoader;

public class ApkClassLoader extends DexClassLoader {

    private ClassLoader mGrandParent;
    private final String[] mInterfacePackageNames;

    public ApkClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent, String[] interfacePackageNames) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
        ClassLoader grand = parent;
//        mGrandParent = grand.getParent();
        mGrandParent = grand;
        this.mInterfacePackageNames = interfacePackageNames;
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        String packageName;
        int dot = className.lastIndexOf('.');
        if (dot != -1) {
            packageName = className.substring(0, dot);
        } else {
            packageName = "";
        }

        boolean isInterface = false;
        for (String interfacePackageName : mInterfacePackageNames) {
            if (packageName.equals(interfacePackageName)) {
                isInterface = true;
                break;
            }
        }

        if (isInterface) {
            return super.loadClass(className, resolve);
        } else {

            Class<?> clazz = findLoadedClass(className);
            if (clazz == null) {
                ClassNotFoundException suppressed = null;
                try {
                    clazz = findClass(className);
                } catch (ClassNotFoundException e) {
                    suppressed = e;
                }

                if (clazz == null) {
                    try {
                        clazz = mGrandParent.loadClass(className);
                    } catch (ClassNotFoundException e) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            e.addSuppressed(suppressed);
                        }
                        throw e;
                    }
                }
            }

            return clazz;
        }
    }

    /**
     * 从apk中读取接口的实现
     *
     * @param clazz     接口类
     * @param className 实现类的类名
     * @param <T>       接口类型
     * @return 所需接口
     * @throws Exception
     */
    public <T> T getInterface(Class<T> clazz, String className) throws Exception {
        try {
            Class<?> interfaceImplementClass = loadClass(className);
            Object interfaceImplement = interfaceImplementClass.newInstance();
            return clazz.cast(interfaceImplement);
        } catch (ClassNotFoundException | InstantiationException
                | ClassCastException | IllegalAccessException e) {
            throw new Exception(e);
        }
    }

}
