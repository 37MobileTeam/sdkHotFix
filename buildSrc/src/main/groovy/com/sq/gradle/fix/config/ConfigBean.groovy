package com.sq.gradle.fix.config

/**
 *
 * @author zhuxiaoxin @date 2020-07-23
 */
class ConfigBean {

    //名称映射，打插件时替换类名
    Map<String, String> nameMap

    //过滤掉class，不打到插件中
    Set<String> deleteClassList

    //过滤掉本地jar包，不打到插件中
    Set<String> filterLocalJar

    //activity过滤器
    Set<String> activityFilter

}
