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

import cn.cheney.xrouter.plugin.utils.GenerateCodeUtils;
import cn.cheney.xrouter.plugin.utils.ScannerUtils;
import cn.cheney.xrouter.plugin.utils.XLogger;

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
        List<String> allModuleClassList = new ArrayList<>();
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

                    List<String> moduleClassInJarList = ScannerUtils.scanJar(src);
                    allModuleClassList.addAll(moduleClassInJarList);
                    FileUtils.copyFile(src, dest);
                }
            }
            //文件夹的classfile
            Collection<DirectoryInput> dirInputs = input.getDirectoryInputs();
            if (null != dirInputs) {
                for (DirectoryInput dirInput : dirInputs) {
                    File src = dirInput.getFile();
                    File dest = transformInvocation.getOutputProvider().getContentLocation(dirInput.getName(),
                            dirInput.getContentTypes(),
                            dirInput.getScopes(),
                            Format.DIRECTORY);
                    List<String> moduleClassInDirList = ScannerUtils.scanDir(src);
                    if (null != moduleClassInDirList) {
                        allModuleClassList.addAll(moduleClassInDirList);
                    }
                    FileUtils.copyDirectory(src, dest);
                }
            }
        }
        File dest = transformInvocation.getOutputProvider().getContentLocation("XRouter",
                TransformManager.CONTENT_CLASS,
                ImmutableSet.of(QualifiedContent.Scope.PROJECT),
                Format.DIRECTORY);
        for (String moduleStr : allModuleClassList) {
            XLogger.debug("scan===>" + moduleStr);
        }
        GenerateCodeUtils.generateClass(dest.getAbsolutePath(), allModuleClassList);

    }
}
