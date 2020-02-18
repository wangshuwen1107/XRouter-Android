package cn.cheney.xrouter.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import cn.cheney.xrouter.compiler.XRouterProcessor;
import cn.cheney.xrouter.core.constant.GenerateFileConstant;
import cn.cheney.xrouter.core.constant.RouteType;
import cn.cheney.xrouter.core.entity.XRouteMeta;
import cn.cheney.xrouter.core.module.BaseModule;

public class ModuleClassGenerator {

    private String generatorClassName;


    public ModuleClassGenerator(String group) {
        this.generatorClassName = GenerateFileConstant.MODULE_FILE_PREFIX + group;
    }

    /**
     * 最后生产java文件
     *
     * @param filer out put
     */
    public void generateJavaFile(Filer filer) {
        TypeSpec typeSpec = TypeSpec.classBuilder(generatorClassName)
                .superclass(BaseModule.class)
                .addJavadoc(GenerateFileConstant.WARNING_TIPS)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();

        JavaFile javaFile = JavaFile.builder("cn.cheney.xrouter", typeSpec)
                .build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * public void load(Map<String, XRouteMeta> routeMap) {}
     */
    private MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("load")
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.VOID)
            .addAnnotation(Override.class)
            .addParameter(ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(XRouteMeta.class)),
                    "routeMap");


    /**
     * routeMap.put("path", new XRouteMeta(TestActivity1.class, "path"));
     */
    public void generateSeg(XRouterProcessor.Holder holder,
                            TypeElement classType,
                            String module,
                            String path) {
        String segBuilder = "$L.put($S,new XRouteMeta(" + "$S,$T.class,$S,$S" + "))";
        TypeMirror typeActivity = holder.elementUtils
                .getTypeElement("android.app.Activity").asType();
        if (holder.types.isSubtype(classType.asType(), typeActivity)) {
            methodBuilder.addStatement(segBuilder, "routeMap", path, RouteType.ACTIVITY,
                    classType, module, path);
        }
    }


}
