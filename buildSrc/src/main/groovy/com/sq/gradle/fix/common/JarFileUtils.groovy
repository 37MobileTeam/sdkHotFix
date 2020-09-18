package com.sq.gradle.fix.common

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * @author zhuxiaoxin
 * Jar包内修改class工具类
 */
class JarFileUtils {

    /**
     *
     * @param zipFilePath jar包路径
     * @param fileName 要处理的文件名称
     * @param content 要处理的文件内容
     */
    static void replaceFile(String zipFilePath, String fileName, byte[] content) {
        Map<String, byte[]> map = deCompress(zipFilePath)
        map.put(fileName, content)
        compress(map, zipFilePath)
    }

    static void replaceFileAndDeleteOldFile(String zipFilePath, String newFileName, byte[] content, List<String> oldFileName) {
        Map<String, byte[]> map = deCompress(zipFilePath)
        map.put(newFileName, content)
        oldFileName.each {
            map.remove(it)
        }
        compress(map, zipFilePath)
    }

    static void deleteEntry(String zipFilePath, List<String> deleteEntryList) {
        Map<String, byte[]> map = deCompress(zipFilePath)
        deleteEntryList.each {
            map.remove(it)
        }
        compress(map, zipFilePath)
    }

    private static Map<String, byte[]> deCompress(String zipFilePath) {
        Map<String, byte[]> map = new HashMap<>()
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))
            ZipEntry zipEntry = null
            while ((zipEntry = zis.getNextEntry()) != null) {
                String entryName = zipEntry.getName()
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
                int i = 0
                while ((i = zis.read()) != -1) {
                    outputStream.write(i)
                }
                map.put(entryName, outputStream.toByteArray())
                outputStream.flush()
                outputStream.close()
            }
            zis.closeEntry()
            zis.close()
        } catch (Exception e) {
            e.printStackTrace()
        }
        return map
    }

    private static Set<String> deCopressName(String zipFilepath) {
        Set<String> set = new HashSet<>()
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilepath))
            ZipEntry zipEntry = null
            while ((zipEntry = zis.getNextEntry()) != null) {
                String entryName = zipEntry.getName()
                set.add(entryName)
            }
            zis.closeEntry()
            zis.close()
        } catch (Exception e) {
            e.printStackTrace()
        }
        return set
    }

    private static void compress(Map<String, byte[]> map, String targetFileName) {
        try {
            File file = new File(targetFileName)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            FileOutputStream fileOutput = new FileOutputStream(file)
            ZipOutputStream zos = new ZipOutputStream(fileOutput)
            for (String fileName : map.keySet()) {
                byte[] fileContent = map.get(fileName)
                if (fileContent == null) {
                    continue
                }
                zos.putNextEntry(new ZipEntry(fileName))
                zos.write(fileContent, 0, fileContent.length)
                zos.closeEntry()
            }
            zos.flush()
            zos.close()
            fileOutput.flush()
            fileOutput.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

}
