package com.sq.plugin.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;

import com.sq.plugin.core.loader.ApkClassLoader;
import com.sq.plugin.core.resources.MixResources;
import com.sq.plugin.core.tool.ApkTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 占位式插件
 */
public class PluginManager {

    private final String TAG = "PluginManager";

    private static PluginManager sInstance;

    private Context mContext;

    private String mPluginPath;

    private String mPluginPkgName = "";

    private String defaultPluginApkName = "plugin0.apk";

    private PluginManager(Context context) {
        mContext = context;
    }

    public static PluginManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PluginManager(context);
        }
        return sInstance;
    }

    public Plugin loadPlugin() {
        Log.i("37手游安卓团队", "--loadPlugin--");
        mPluginPath = copyAssetPlugin(defaultPluginApkName);
        return loadPlugin(mPluginPath);

    }


    //加载默认plugin
    private Plugin loadPlugin(String pluginPath) {
        Log.i("37手游安卓团队", "pluginPath : " + pluginPath);
        Plugin plugin = new Plugin();
        plugin.setPluginPath(pluginPath);
        File file = mContext.getDir("plugin-opti", Context.MODE_PRIVATE);
        String[] interfaces = new String[]{"com.sqwan.msdk.api", "com.sqwan.msdk.api.tool", "com.sq.standard"};
        //获取ClassLoader
        ApkClassLoader pluginClassLoader = new ApkClassLoader(pluginPath, file.getAbsolutePath(), null, mContext.getClassLoader(), interfaces);
        plugin.setClassLoader(pluginClassLoader);
        try {
            Resources pluginResources = buildPluginResources();
            Resources resources = new MixResources(mContext.getResources(), pluginResources, mPluginPkgName);
            plugin.setResources(resources);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plugin;
    }

    private Resources buildPluginResources() {
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA
                    | PackageManager.GET_SERVICES
                    | PackageManager.GET_PROVIDERS
                    | PackageManager.GET_SIGNATURES);
            String hostPublicSourceDir = packageInfo.applicationInfo.publicSourceDir;
            String hostSourceDir = packageInfo.applicationInfo.sourceDir;
            String pkgName = packageInfo.packageName;
            packageInfo.applicationInfo.publicSourceDir = mPluginPath;
            packageInfo.applicationInfo.sourceDir = mPluginPath;
            PackageInfo pluginInfo = mContext.getPackageManager().getPackageArchiveInfo(mPluginPath, PackageManager.GET_ACTIVITIES);
            mPluginPkgName = pluginInfo.packageName;
            Resources pluginResources = mContext.getPackageManager().getResourcesForApplication(packageInfo.applicationInfo);
            packageInfo.applicationInfo.publicSourceDir = hostPublicSourceDir;
            packageInfo.applicationInfo.sourceDir = hostSourceDir;
            packageInfo.packageName = pkgName;
            return pluginResources;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String copyAssetPlugin(String assetName) {
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open(assetName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File pluginDirFile = mContext.getDir("plugin", Context.MODE_PRIVATE);
        if (!pluginDirFile.exists()) {
            pluginDirFile.mkdirs();
        }
        File resultFile = new File(pluginDirFile, assetName);
        writeInputStream(resultFile.getAbsolutePath(), inputStream);
        return resultFile.getAbsolutePath();
    }

    private void writeInputStream(String storagePath, InputStream inputStream) {
        File file = new File(storagePath);
        try {
            if (!file.exists()) {
                // 1.建立通道对象
                FileOutputStream fos = new FileOutputStream(file);
                // 2.定义存储空间
                byte[] buffer = new byte[inputStream.available()];
                // 3.开始读文件
                int lenght = 0;
                while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                    // 将Buffer中的数据写到outputStream对象中
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();// 刷新缓冲区
                // 4.关闭流
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}