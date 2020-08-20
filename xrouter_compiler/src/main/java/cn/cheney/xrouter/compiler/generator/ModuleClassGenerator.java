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
import javax.lang.model.type.DeclaredType;
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
                .getTypeElement(XTypeMirror.BASE_MODULE).asType();

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
        if (holder.types.isSubtype(classType.asType(), typeActivity)) {
            addActivityInvoke(classType, path);
        }
        for (Element element : classType.getEnclosedElements()) {
            if (element instanceof ExecutableElement) {
                ExecutableElement executableElement = (ExecutableElement) element;
                addMethodInvoke(holder, classType, executableElement);
            }
        }
    }


    private String getParamName(XParam xParam, String originalName) {
        if (null != xParam && !xParam.name().isEmpty()) {
            return xParam.name();
        }
        return originalName;
    }


    private void addActivityInvoke(TypeElement activityType, String path) {
        TypeMirror typeActivityInvoke = holder.elementUtils
                .getTypeElement(XTypeMirror.ACTIVITY_INVOKE).asType();

        TypeMirror paramInfoType = holder.elementUtils
                .getTypeElement(XTypeMirror.PARAM_INFO).asType();

        StringBuilder paramsInfoSeg = new StringBuilder();
        List<Object> paramsInfoSegList = new ArrayList<>();

        /**
         *  routeMap.put("page",new ActivityInvoke(RouteType.ACTIVITY,HomeActivity.class,"home","page"));
         */
        paramsInfoSeg.append("$L.put($S,new $T($T.ACTIVITY,$T.class,$S,$S");

        paramsInfoSegList.add("routeMap");
        paramsInfoSegList.add(path);
        paramsInfoSegList.add(typeActivityInvoke);
        paramsInfoSegList.add(RouteType.class);
        paramsInfoSegList.add(activityType);
        paramsInfoSegList.add(module);
        paramsInfoSegList.add(path);


        List<VariableElement> xParamElementList = new ArrayList<>();
        for (Element element : activityType.getEnclosedElements()) {
            if (element instanceof VariableElement) {
                VariableElement variableElement = (VariableElement) element;
                XParam xParam = variableElement.getAnnotation(XParam.class);
                if (null != xParam) {
                    xParamElementList.add(variableElement);
                }
            }
        }
        if (!xParamElementList.isEmpty()) {
            paramsInfoSeg.append(",");
        }
        for (VariableElement variableElement : xParamElementList) {
            XParam xParam = variableElement.getAnnotation(XParam.class);
            if (xParamElementList.indexOf(variableElement) == xParamElementList.size() - 1) {
                paramsInfoSeg.append("new $T($S,$T.class)");
            } else {
                paramsInfoSeg.append("new $T($S,$T.class),");
            }
            TypeMirror paramType = variableElement.asType();
            //擦除泛型[因为Map<String>.class 不允许]
            if (paramType instanceof DeclaredType) {
                DeclaredType paramDeclaredType = (DeclaredType) paramType;
                if (!paramDeclaredType.getTypeArguments().isEmpty()) {
                    paramType = holder.types.erasure(paramType);
                    Logger.d("erasure " + paramType.toString());
                }
            }
            paramsInfoSegList.add(paramInfoType);
            paramsInfoSegList.add(getParamName(xParam, variableElement.getSimpleName().toString()));
            paramsInfoSegList.add(paramType);
        }
        paramsInfoSeg.append("))");

        loadMethodBuilder.addStatement(paramsInfoSeg.toString(), paramsInfoSegList.toArray());
    }


    private void addMethodInvoke(XRouterProcessor.Holder holder,
                                 TypeElement classType,
                                 ExecutableElement methodElement) {
        TypeMirror paramInfoType = holder.elementUtils
                .getTypeElement(XTypeMirror.PARAM_INFO).asType();

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

        StringBuilder paramsInfoSeg = new StringBuilder();
        List<Object> allInfoSegList = new ArrayList<>();
        List<Object> paramsInfoSegList = new ArrayList<>();
        boolean hasRequestId = false;
        if (null != parameters && !parameters.isEmpty()) {
            for (VariableElement variableElement : parameters) {
                javax.lang.model.type.TypeMirror methodParamType = variableElement.asType();
                //擦除泛型[因为Map<String>.class 不允许]
                if (methodParamType instanceof DeclaredType) {
                    DeclaredType methodParamDeclaredType = (DeclaredType) methodParamType;
                    if (!methodParamDeclaredType.getTypeArguments().isEmpty()) {
                        methodParamType = holder.types.erasure(methodParamType);
                        Logger.d("erasure " + methodParamType.toString());
                    }
                }
                XParam xParam = variableElement.getAnnotation(XParam.class);
                String key = getParamName(xParam, variableElement.getSimpleName().toString());
                if (key.equals(XParam.RequestId)) {
                    if (hasRequestId) {
                        Logger.e(String.format("[%s] [%s] have illegal key requestId",
                                classType.getQualifiedName(),
                                methodElement.getSimpleName()));
                        return;
                    }
                    hasRequestId = true;
                }
                if (parameters.indexOf(variableElement) == parameters.size() - 1) {
                    paramSeg.append("($T)params.get($S)");
                    paramsInfoSeg.append("new $T($S,$T.class)");
                } else {
                    paramSeg.append("($T)params.get($S),");
                    paramsInfoSeg.append("new $T($S,$T.class),");
                }
                paramsSegList.add(methodParamType);
                paramsSegList.add(key);

                paramsInfoSegList.add(paramInfoType);
                paramsInfoSegList.add(getParamName(xParam, variableElement.getSimpleName().toString()));
                paramsInfoSegList.add(methodParamType);

            }
        }
        paramSeg.append(")");
        //返回类型的泛型className
        TypeName returnClassName;
        if (!isReturnVoid) {
            returnClassName = TypeName.get(returnType);
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
        if (!paramsInfoSeg.toString().isEmpty()) {
            methodInvokeClassStr += "," + paramsInfoSeg.toString();
        }

        allInfoSegList.add(RouteType.class);
        allInfoSegList.add(classType);
        allInfoSegList.add(module);
        allInfoSegList.add(xMethod.name());
        allInfoSegList.add(hasRequestId);
        allInfoSegList.addAll(paramsInfoSegList);

        TypeSpec methodInvoke = TypeSpec.anonymousClassBuilder(methodInvokeClassStr, allInfoSegList.toArray())
                .addSuperinterface(methodInvokableType)
                .addMethod(invokeBuilder.build())
                .build();
        loadMethodBuilder.addStatement("$L.put($S,$L)", "routeMap", xMethod.name(), methodInvoke);
    }

}
