package cn.cheney.xrouter.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import cn.cheney.xrouter.annotation.XMethod;
import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.compiler.XRouterProcessor;
import cn.cheney.xrouter.compiler.contant.XTypeMirror;
import cn.cheney.xrouter.compiler.util.Logger;
import cn.cheney.xrouter.constant.GenerateFileConstant;
import cn.cheney.xrouter.constant.RouteType;

public class ModuleClassGenerator {

    private String generatorClassName;

    private String module;

    private XRouterProcessor.Holder holder;

    private MethodSpec.Builder loadMethodBuilder;


    public ModuleClassGenerator(String module, XRouterProcessor.Holder holder) {
        this.module = module;
        this.generatorClassName = GenerateFileConstant.MODULE_FILE_PREFIX + module;
        this.holder = holder;
        loadMethodBuilder = loadMethodBuilder();
    }

    /**
     * 最后生产java文件
     */
    public void generateJavaFile() {
        TypeMirror baseModuleType = holder.elementUtils
                .getTypeElement(XTypeMirror.BASEMODULE).asType();

        TypeSpec typeSpec = TypeSpec.classBuilder(generatorClassName)
                .superclass(TypeName.get(baseModuleType))
                .addJavadoc(GenerateFileConstant.WARNING_TIPS)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(loadMethodBuilder.build())
                .addMethod(getNameMethodBuilder.addStatement("return $S", module)
                        .build())
                .build();

        JavaFile javaFile = JavaFile.builder("cn.cheney.xrouter", typeSpec)
                .build();
        try {
            javaFile.writeTo(holder.filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * public void load(Map<String, Invokable> routeMap) {}
     */
    private MethodSpec.Builder loadMethodBuilder() {
        return MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addAnnotation(Override.class)
                .addParameter(ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(holder.elementUtils
                                .getTypeElement(XTypeMirror.INVOKABLE))),
                        "routeMap");
    }

    /**
     * public String getName() {return "home";}
     */
    private MethodSpec.Builder getNameMethodBuilder = MethodSpec.methodBuilder("getName")
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class)
            .addAnnotation(Override.class);


    /**
     * routeMap.put("/test2",new MethodInvoke() or new ActivityInvoke());
     */
    public void generateSeg(TypeElement classType, String path) {
        TypeMirror typeActivity = holder.elementUtils
                .getTypeElement(XTypeMirror.ACTIVITY).asType();

        TypeMirror typeMethod = holder.elementUtils
                .getTypeElement(XTypeMirror.I_METHOD).asType();

        TypeMirror typeActivityInvoke = holder.elementUtils
                .getTypeElement(XTypeMirror.ACTIVITY_INVOKE).asType();

        if (holder.types.isSubtype(classType.asType(), typeActivity)) {
            String segBuilder = "$L.put($S,new $T(" + "$T.ACTIVITY,$T.class,$S,$S" + "))";
            loadMethodBuilder.addStatement(segBuilder, "routeMap", path,
                    typeActivityInvoke, RouteType.class, classType, module, path);
        } else if (holder.types.isSubtype(classType.asType(), typeMethod)) {
            for (Element element : classType.getEnclosedElements()) {
                if (element instanceof ExecutableElement) {
                    ExecutableElement executableElement = (ExecutableElement) element;
                    addMethodInvoke(holder, classType, executableElement);
                }
            }
        }
    }


    private void addMethodInvoke(XRouterProcessor.Holder holder,
                                 TypeElement classType,
                                 ExecutableElement methodElement) {
        XMethod xMethod = methodElement.getAnnotation(XMethod.class);
        if (null == xMethod) {
            return;
        }
        Set<Modifier> modifiers = methodElement.getModifiers();
        if (null == modifiers) {
            Logger.e(String.format("[%s] [%s]  Must public static !!",
                    classType.getQualifiedName(),
                    methodElement.getSimpleName()));
            return;
        }
        if (!modifiers.contains(Modifier.PUBLIC) || !modifiers.contains(Modifier.STATIC)) {
            Logger.e(String.format("[%s] [%s]  Must public static !!",
                    classType.getQualifiedName(),
                    methodElement.getSimpleName()));
            return;
        }
        TypeMirror returnType = methodElement.getReturnType();
        boolean isReturnVoid = TypeKind.VOID.equals(returnType.getKind());

        List<? extends VariableElement> parameters = methodElement.getParameters();
        List<Object> paramsSegList = new ArrayList<>();
        paramsSegList.add(classType);
        paramsSegList.add(methodElement.getSimpleName().toString());

        StringBuilder paramSeg = new StringBuilder();
        if (isReturnVoid) {
            paramSeg.append("$T.$L(");
        } else {
            paramSeg.append("return $T.$L(");
        }
        boolean hasCallback = false;
        if (null != parameters && !parameters.isEmpty()) {
            for (VariableElement variableElement : parameters) {
                javax.lang.model.type.TypeMirror methodParamType = variableElement.asType();
                String key;
                XParam xParam = variableElement.getAnnotation(XParam.class);
                if (variableElement.asType().toString().equals(XTypeMirror.CALLBACK)) {
                    if (hasCallback) {
                        Logger.e(String.format("[%s] [%s] only one RouteCallback !!",
                                classType.getQualifiedName(),
                                methodElement.getSimpleName()));
                        return;
                    }
                    key = GenerateFileConstant.CALLBACK_KEY;
                    hasCallback = true;
                } else {
                    if (null != xParam && !xParam.name().isEmpty()) {
                        key = xParam.name();
                    } else {
                        key = variableElement.getSimpleName().toString();
                    }
                    if (key.equals(GenerateFileConstant.CALLBACK_KEY)) {
                        Logger.e(String.format("[%s] [%s] have illegal key ROUTE_CALLBACK",
                                classType.getQualifiedName(),
                                methodElement.getSimpleName()));
                    }
                }
                if (parameters.indexOf(variableElement) == parameters.size() - 1) {
                    paramSeg.append("($T)params.get($S)");
                } else {
                    paramSeg.append("($T)params.get($S),");
                }
                paramsSegList.add(methodParamType);
                paramsSegList.add(key);
            }
        }
        paramSeg.append(")");
        //返回类型的泛型className
        ClassName returnClassName;
        if (!isReturnVoid) {
            returnClassName = ClassName.bestGuess(returnType.toString());
        } else {
            returnClassName = ClassName.bestGuess("java.lang.Void");
        }
        ParameterizedTypeName methodInvokableType =
                ParameterizedTypeName.get(ClassName.get("cn.cheney.xrouter.core.invok", "MethodInvokable"), returnClassName);
        /*
         *   new MethodInvokable(RouteType.METHOD,YourClazz.class,module,path) {
         *       @Override
         *       public Object invoke(Map<String, Object> params) {
         *         return YourClass.YourMethod(params.get(yourKey));
         *       }
         *     }
         */
        MethodSpec.Builder invokeBuilder = MethodSpec.methodBuilder("invoke")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(Object.class)), "params")
                .addStatement(paramSeg.toString(), paramsSegList.toArray())
                .returns(returnClassName);

        if (isReturnVoid) {
            invokeBuilder.addStatement("return null");
        }
        String methodInvokeClassStr = "$T.METHOD,$T.class,$S,$S,$L";
        TypeSpec methodInvoke = TypeSpec.anonymousClassBuilder(methodInvokeClassStr,
                RouteType.class, classType, module, xMethod.name(), hasCallback)
                .addSuperinterface(methodInvokableType)
                .addMethod(invokeBuilder.build())
                .build();
        loadMethodBuilder.addStatement("$L.put($S,$L)", "routeMap", xMethod.name(), methodInvoke);
    }

}
