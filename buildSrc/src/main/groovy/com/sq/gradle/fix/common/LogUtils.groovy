package com.sq.gradle.fix.common

//TODO 补齐日志级别
class LogUtils {

    private LogUtils(){

    }

    public static void e(String msg) {
        def styler = 'black red green yellow blue magenta cyan white'
                .split().toList().withIndex(30)
                .collectEntries { key, val -> [(key) : { "\033[${val}m${it}\033[0m" }] }
        println "Message: ${styler['red'](msg)}"
    }

    public static void w(String msg) {
        def styler = 'black red green yellow blue magenta cyan white'
                .split().toList().withIndex(30)
                .collectEntries { key, val -> [(key) : { "\033[${val}m${it}\033[0m" }] }
        println "Message: ${styler['blue'](msg)}"
    }


}
