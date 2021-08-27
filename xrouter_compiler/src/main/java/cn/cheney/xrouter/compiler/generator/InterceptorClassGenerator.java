package cn.cheney.xrouter.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import cn.cheney.xrouter.annotation.XInterceptor;
import cn.cheney.xrouter.compiler.XRouterProcessor;
import cn.cheney.xrouter.constant.GenerateFileConstant;

import static cn.cheney.xrouter.compiler.contant.XTypeMirror.CLASSNAME_INTERCEPTOR_DESC;

public class InterceptorClassGenerator {

    private final String generatorClassName;

    private final XRouterProcessor.Holder holder;

    private final MethodSpec.Builder loadMethodBuilder;

    private boolean hasBuildVar = false;

    public InterceptorClassGenerator(XRouterProcessor.Holder holder) {
        this.generatorClassName = GenerateFileConstant.INTERCEPTOR_FILE_PREFIX +
                UUID.randomUUID().toString().replace("-", "");
        this.holder = holder;
        loadMethodBuilder = loadMethodBuilder();
    }


    public void generateSeg(XInterceptor xInterceptor, TypeElement typeElement) {
        String[] modules = xInterceptor.modules();
        String[] paths = xInterceptor.paths();
        List<String> allList = new ArrayList<>();
        allList.addAll(Arrays.asList(modules));
        allList.addAll(Arrays.asList(paths));
        if (allList.isEmpty()) {
            return;
        }
        ParameterizedTypeName listInterceptorDescType = ParameterizedTypeName.
                get(ClassName.get(List.class), CLASSNAME_INTERCEPTOR_DESC);
        if (!hasBuildVar){
            loadMethodBuilder.addStatement("$T $L", listInterceptorDescType, "interceptorList");
            hasBuildVar= true;
        }
        for (String interceptorStr : allList) {
            loadMethodBuilder.beginControlFlow("if (map.containsKey($S))", interceptorStr);
            loadMethodBuilder.addStatement("interceptorList = map.get($S)", interceptorStr);
            loadMethodBuilder.nextControlFlow("else");
            loadMethodBuilder.addStatement("interceptorList = new $T<>()", ClassName.get(ArrayList.class));
            loadMethodBuilder.addStatement("map.put($S, interceptorList)", interceptorStr);
            loadMethodBuilder.endControlFlow();
            loadMethodBuilder.addStatement("interceptorList.add(new $T($L, $T.class))",
                    CLASSNAME_INTERCEPTOR_DESC, xInterceptor.priority(), typeElement);
        }
    }

    /**
     * public void load(Map<String, Invokable> routeMap) {}
     */
    private MethodSpec.Builder loadMethodBuilder() {
        ParameterizedTypeName methodInvokableType = ParameterizedTypeName.
                get(ClassName.get(List.class), CLASSNAME_INTERCEPTOR_DESC);
        return MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(String.class),
                        methodInvokableType),
                        "map");
    }


    /**
     * 最后生产java文件
     */
    public void generateJavaFile() {
        TypeSpec typeSpec = TypeSpec.classBuilder(generatorClassName)
                .addJavadoc(GenerateFileConstant.WARNING_TIPS)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(loadMethodBuilder.build())
                .build();
        JavaFile javaFile = JavaFile.builder(GenerateFileConstant.ROUTER_PACKAGE_NAME, typeSpec)
                .build();
        try {
            javaFile.writeTo(holder.filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
