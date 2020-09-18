package com.sq.gradle.fix

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.sq.gradle.fix.common.LogUtils
import com.sq.gradle.fix.core.PluginHandler
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * 插件变换使用的Transform
 */
class FixTransform extends Transform {

    Project project

    public FixTransform(Project project){
        this.project = project
    }

    /**
     * transform的名称
     * transformClassesWithMyClassTransformForDebug 运行时的名字
     * transformClassesWith + getName() + For + Debug或Release
     *
     * @return String
     */
    @Override
    String getName() {
        return "FixTransform"
    }

    /**
     * 需要处理的数据类型，有两种枚举类型
     * CLASSES和RESOURCES，CLASSES代表处理的java的class文件，RESOURCES代表要处理java的资源
     *
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 指Transform要操作内容的范围，官方文档Scope有7种类型：
     * EXTERNAL_LIBRARIES        只有外部库
     * PROJECT                   只有项目内容
     * PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * PROVIDED_ONLY             只提供本地或远程依赖项
     * SUB_PROJECTS              只有子项目。
     * SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * TESTED_CODE               由当前变量(包括依赖项)测试的代码
     *
     * Returns the scope(s) of the Transform. This indicates which scopes the transform consumes.
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 指明当前Transform是否支持增量编译
     * If it does, then the TransformInput may contain a list of changed/removed/added files, unless
     * something else triggers a non incremental run.
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * Transform中的核心方法
     * transformInvocation.getInputs() 中是传过来的输入流，其中有两种格式，一种是jar包格式一种是目录格式。
     * transformInvocation.getOutputProvider() 获取到输出目录，最后将修改的文件复制到输出目录，这一步必须做不然编译会报错
     *
     * @param transformInvocation
     * @throws TransformException
     * @throws InterruptedException
     * @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        Context context = transformInvocation.getContext()
        Collection inputs = transformInvocation.getInputs()
        Collection referencedInputs = transformInvocation.getReferencedInputs()
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider()
        boolean isIncremental = transformInvocation.isIncremental()
        outputProvider.deleteAll()
        inputs.each {
            TransformInput input ->
                PluginHandler.getInstance().init()
                input.directoryInputs.each {
                    DirectoryInput directoryInput ->
                        PluginHandler.getInstance().appendClassPath(directoryInput.file.absolutePath)
                }

                //提前全部加到pool中
                input.jarInputs.each {
                    JarInput jarInput ->
                        PluginHandler.getInstance().appendClassPath(jarInput.file.absolutePath)
                }

                //统一处理
                PluginHandler.getInstance().handleInputFile()

                //目录
                input.directoryInputs.each {
                    DirectoryInput directoryInput ->
                        def dest = outputProvider.getContentLocation(directoryInput.name,
                                directoryInput.contentTypes, directoryInput.scopes,
                                Format.DIRECTORY)
                        FileUtils.copyDirectory(directoryInput.file, dest)
                }

                //jar包
                input.jarInputs.each {
                    JarInput jarInput ->
                        def jarName = jarInput.name
                        def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                        if (jarName.endsWith(".jar")) {
                            jarName = jarName.substring(0, jarName.length() - 4)
                        }
                        LogUtils.w(jarInput.file.absolutePath)
                        if (!PluginHandler.getInstance().fileLocalJar(jarInput.file.absolutePath)) {
                            def dest = outputProvider.getContentLocation(jarName + md5Name,
                                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
                            FileUtils.copyFile(jarInput.file, dest)
                        }
                }
        }
    }
}
