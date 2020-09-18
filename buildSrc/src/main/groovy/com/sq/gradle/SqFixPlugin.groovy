package com.sq.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.tasks.ManifestProcessorTask
import com.sq.gradle.fix.FixTransform
import com.sq.gradle.fix.config.ConfigBean
import com.sq.gradle.fix.core.PluginHandler
import org.gradle.api.Plugin
import org.gradle.api.Project

class SqFixPlugin implements Plugin<Project> {

    @Override
    String toString() {
        return super.toString()
    }

    @Override
    void apply(Project project) {
        def android = project.extensions.android
        project.extensions.create('config', ConfigBean)
        project.plugins.all {
            if (it instanceof LibraryPlugin) {

            } else if (it instanceof AppPlugin) {
                android.applicationVariants.all { variant ->
                    variant.outputs.all { output ->
                        ManifestProcessorTask processorTask = output.processManifestProvider.getOrNull()
                        processorTask.doLast { ManifestProcessorTask task ->
                            def directory = task.getBundleManifestOutputDirectory()
                            def manifestPath = "$directory/AndroidManifest.xml"
                            PluginHandler.getInstance().init(manifestPath, project.extensions.config)
                        }
                    }
                }
                android.registerTransform(new FixTransform(project))
            }
        }
    }

}
