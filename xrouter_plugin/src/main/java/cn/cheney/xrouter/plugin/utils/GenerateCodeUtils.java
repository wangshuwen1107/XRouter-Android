package cn.cheney.xrouter.plugin.utils;

import com.android.SdkConstants;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.cheney.xrouter.plugin.consts.PluginConstant;


public class GenerateCodeUtils {


    public static void generateClass(String outDirFilePath, List<String> allModuleClassList) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5, writer) {
        };
        String className = PluginConstant.ALL_ROUTER_LOAD_CLASS.replace(".", "/");
        classVisitor.visit(50,
                Opcodes.ACC_PUBLIC,
                className,
                null,
                "java/lang/Object",
                null);
        //cn.cheney.xrouter.core.invok
        MethodVisitor methodVisitor = classVisitor.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "loadInto",
                "(Ljava/util/Map;)V",
                "(Ljava/util/Map<Ljava/lang/String;Lcn/cheney/xrouter/core/invok/Invokable<*>;>;)V",
                null);
        methodVisitor.visitCode();

        if (null != allModuleClassList && !allModuleClassList.isEmpty()) {
            for (String module : allModuleClassList) {
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC,
                        module.replace(".", "/"),
                        "load",
                        "(Ljava/util/Map;)V",
                        false);
            }
        }
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitEnd();
        classVisitor.visitEnd();
        File dest = new File(outDirFilePath, className + SdkConstants.DOT_CLASS);
        File parentFile = dest.getParentFile();
        try {
            if (parentFile != null) {
                boolean mkdirs = parentFile.mkdirs();
            }
            new FileOutputStream(dest).write(writer.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
