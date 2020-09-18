package com.sq.gradle.fix.manifest

/**
 * manifest解析帮助类，解析application和各类组件
 */
class ManifestHelper {

    String mManifestPath

    String mApplicationName

    ArrayList<String> mActivities

    ArrayList<String> mProviders

    ArrayList<String> mServices

    ArrayList<String> mReceivers

    void init(String path){
        mManifestPath = path
        mActivities = new ArrayList<>()
        mProviders = new ArrayList<>()
        mServices = new ArrayList<>()
        mReceivers = new ArrayList<>()

        println "=====manifestPath=====$mManifestPath"
        def androidManifest = new XmlSlurper().parse(mManifestPath)
        mApplicationName = androidManifest.application['@android:name']
        println "=====application name=====$mApplicationName"

        println "=====activity name====="
        androidManifest.application[0]."activity".each{
            String activityName = it."@android:name"
            println "$activityName"
            mActivities.add(activityName)
        }

        println "=====receiver name====="
        androidManifest.application[0]."receiver".each{
            String receiverName = it."@android:name"
            println "$receiverName"
            mReceivers.add(receiverName)
        }

        println "=====provider name====="
        androidManifest.application[0]."provider".each{
            String providerName = it."@android:name"
            println "$providerName"
            mProviders.add(providerName)
        }
    }

}
