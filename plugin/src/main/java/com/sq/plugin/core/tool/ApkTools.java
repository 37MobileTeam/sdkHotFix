package com.sq.plugin.core.tool;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

public class ApkTools {

    public static boolean isApkValid(String path) {
        File apkFile = new File(path);
        if (!apkFile.exists()) {
            return false;
        }
        if (!isValidZipFile(path)) {
            return false;
        }
        return true;
    }

    private static boolean isValidZipFile(String file) {
        try {
            ZipFile zf = new ZipFile(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
