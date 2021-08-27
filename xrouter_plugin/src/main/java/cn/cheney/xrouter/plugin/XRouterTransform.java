package cn.cheney.xrouter.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.tools.r8.com.google.common.collect.ImmutableSet;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import cn.cheney.xrouter.constant.GenerateFileConstant;

class XRouterTransform extends Transform {
    @Override
    public String getName() {
        return "XRouter";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        XLogger.debug("transform ===>");
        List<String> allModuleClassList = new ArrayList<>();
        List<String> allInterceptorClassList = new ArrayList<>();
        for (TransformInput input : transformInvocation.getInputs()) {
            //jar的classfile
            Collection<JarInput> jarInputs = input.getJarInputs();
            if (null != jarInputs) {
                for (JarInput jarInput : jarInputs) {

                    File src = jarInput.getFile();
                    File dest = transformInvocation.getOutputProvider().getContentLocation(jarInput.getName(),
                            jarInput.getContentTypes(),
                            jarInput.getScopes(),
                            Format.JAR);

                    allModuleClassList.addAll(CodeScanner
                            .scanJar(GenerateFileConstant.MODULE_CLASS_PREFIX, src));

                    allInterceptorClassList.addAll(CodeScanner
                            .scanJar(GenerateFileConstant.INTERCEPTOR_CLASS_PREFIX, src));

                    FileUtils.copyFile(src, dest);
                }
            }
            //app的classfile
            Collection<DirectoryInput> dirInputs = input.getDirectoryInputs();
            if (null != dirInputs) {
                for (DirectoryInput dirInput : dirInputs) {
                    File src = dirInput.getFile();
                    File dest = transformInvocation.getOutputProvider().getContentLocation(dirInput.getName(),
                            dirInput.getContentTypes(),
                            dirInput.getScopes(),
                            Format.DIRECTORY);

                    allModuleClassList.addAll(CodeScanner.
                            scanDir(GenerateFileConstant.MODULE_CLASS_PREFIX, src));

                    allInterceptorClassList.addAll(CodeScanner
                            .scanDir(GenerateFileConstant.INTERCEPTOR_CLASS_PREFIX, src));

                    FileUtils.copyDirectory(src, dest);
                }
            }
        }
        File dest = transformInvocation.getOutputProvider().getContentLocation("XRouter",
                TransformManager.CONTENT_CLASS,
                ImmutableSet.of(QualifiedContent.Scope.PROJECT),
                Format.DIRECTORY);
        for (String moduleStr : allModuleClassList) {
            XLogger.debug("scan router module ===>" + moduleStr);
        }
        for (String interceptor : allInterceptorClassList) {
            XLogger.debug("scan router interceptor ===>" + interceptor);
        }
        CodeGenerator.generateRouterLoaderClass(dest.getAbsolutePath(), allModuleClassList);
        CodeGenerator.generateInterceptorLoaderClass(dest.getAbsolutePath(), allInterceptorClassList);

    }
}
