package cn.cheney.xrouter.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

    private final String generatorClassName;

    private final String module;

    private final XRouterProcessor.Holder holder;

    private final MethodSpec.Builder loadMethodBuilder;


    public ModuleClassGenerator(String module, XRouterProcessor.Holder holder) {
        this.module = module;
        this.generatorClassName = GenerateFileConstant.MODULE_FILE_PREFIX +
                UUID.randomUUID().toString().replace("-", "");
        this.holder = holder;
        loadMethodBuilder = loadMethodBuilder();
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

    /**
     * public void load(Map<String, Invokable> routeMap) {}
     */
    private MethodSpec.Builder loadMethodBuilder() {
        ParameterizedTypeName methodInvokableType = ParameterizedTypeName.
                get(XTypeMirror.CLASSNAME_INVOKABLE, TypeVariableName.get("?"));
        return MethodSpec.methodBuilder("load")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(String.class),
                        methodInvokableType),
                        "routeMap");
    }


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
                addMethodInvoke(classType, executableElement);
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
        paramsInfoSegList.add(module + "/" + path);
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


    private void addMethodInvoke(TypeElement classType, ExecutableElement methodElement) {
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
        //new MethodInvokable()所有参数的值
        List<Object> allInfoSegList = new ArrayList<>();
        //返回类型的泛型className
        TypeName returnClassName;
        if (!isReturnVoid) {
            returnClassName = TypeName.get(returnType);
        } else {
            returnClassName = ClassName.bestGuess("java.lang.Void");
        }
        MethodParamCodeBean methodParamCodeBean = generateParamCode(classType, methodElement, parameters);
        if (null == methodParamCodeBean) {
            return;
        }
        //Override invoke
        MethodSpec.Builder invokeBuilder = MethodSpec.methodBuilder("invoke")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(
                        ClassName.get(Map.class),
                        ClassName.get(String.class),
                        ClassName.get(Object.class)), "params")
                .addStatement(methodParamCodeBean.invokeImplDesc.toString(),
                        methodParamCodeBean.invokeImplValueList.toArray())
                .returns(returnClassName);
        if (isReturnVoid) {
            invokeBuilder.addStatement("return null");
        }
        //new MethodInvokable(RouteType.METHOD,YourClazz.class,module,path)
        String methodInvokeClassStr = "$T.METHOD,$T.class,$S,$S,$L";

        if (!methodParamCodeBean.paramsInfoDesc.toString().isEmpty()) {
            methodInvokeClassStr += "," + methodParamCodeBean.paramsInfoDesc.toString();
        }
        Logger.d(" " + methodInvokeClassStr);
        allInfoSegList.add(RouteType.class);
        allInfoSegList.add(classType);
        allInfoSegList.add(module);
        allInfoSegList.add(xMethod.name());
        allInfoSegList.add(methodParamCodeBean.isASync);
        allInfoSegList.addAll(methodParamCodeBean.paramsInfoValueList);
        //MethodInvokable<R>的类型
        ParameterizedTypeName methodInvokableType = ParameterizedTypeName.get(XTypeMirror.CLASSNAME_METHOD_INVOKABLE
                , returnClassName);
        TypeSpec methodInvoke = TypeSpec.anonymousClassBuilder(methodInvokeClassStr, allInfoSegList.toArray())
                .addSuperinterface(methodInvokableType)
                .addMethod(invokeBuilder.build())
                .build();
        loadMethodBuilder.addStatement("$L.put($S,$L)", "routeMap", module + "/" + xMethod.name(), methodInvoke);
    }


    /**
     * @param classType     类
     * @param methodElement 方法
     * @param parameters    方法参数
     * @return new ParamInfo("key1", Clazz),new ParamInfo("key2", Clazz)...
     * YourClass.YourMethod(params.get ( yourKey))
     */
    private MethodParamCodeBean generateParamCode(TypeElement classType,
                                                  ExecutableElement methodElement,
                                                  List<? extends VariableElement> parameters) {
        boolean isReturnVoid = TypeKind.VOID.equals(methodElement.getReturnType().getKind());
        TypeMirror paramInfoType = holder.elementUtils
                .getTypeElement(XTypeMirror.PARAM_INFO).asType();

        MethodParamCodeBean paramCodeBean = new MethodParamCodeBean();
        paramCodeBean.invokeImplValueList.add(classType);
        paramCodeBean.invokeImplValueList.add(methodElement.getSimpleName().toString());

        if (isReturnVoid) {
            paramCodeBean.invokeImplDesc.append("$T.$L(");
        } else {
            paramCodeBean.invokeImplDesc.append("return $T.$L(");
        }
        List<String> alreadyExistList = new ArrayList<>();
        if (null != parameters && !parameters.isEmpty()) {
            for (VariableElement variableElement : parameters) {
                boolean isGenerics = false;
                javax.lang.model.type.TypeMirror paramType = variableElement.asType();
                if (paramType instanceof DeclaredType) {
                    DeclaredType methodParamDeclaredType = (DeclaredType) paramType;
                    if (!methodParamDeclaredType.getTypeArguments().isEmpty()) {
                        isGenerics = true;
                        Logger.d("泛型 " + paramType.toString());
                    }
                }
                XParam xParam = variableElement.getAnnotation(XParam.class);
                String key = getParamName(xParam, variableElement.getSimpleName().toString());
                if (alreadyExistList.contains(key)) {
                    Logger.e(String.format("[%s] [%s] have repeat key %s", classType.getSimpleName().toString(),
                            methodElement.getSimpleName().toString(), key));
                    return null;
                }
                if (variableElement.asType().toString().equals(XTypeMirror.CONTEXT)) {
                    key = XParam.Context;
                }
                alreadyExistList.add(key);
                if (parameters.indexOf(variableElement) == parameters.size() - 1) {
                    if (isGenerics) {
                        paramCodeBean.paramsInfoDesc.append("new $T($S,new $T(){}.getType())");
                    } else {
                        paramCodeBean.paramsInfoDesc.append("new $T($S,$T.class)");
                    }
                    paramCodeBean.invokeImplDesc.append("($T)params.get($S)");
                } else {
                    if (isGenerics) {
                        paramCodeBean.paramsInfoDesc.append("new $T($S,new $T(){}.getType()),");
                    } else {
                        paramCodeBean.paramsInfoDesc.append("new $T($S,$T.class),");
                    }
                    paramCodeBean.invokeImplDesc.append("($T)params.get($S),");
                }

                paramCodeBean.paramsInfoValueList.add(paramInfoType);
                paramCodeBean.paramsInfoValueList.add(getParamName(xParam,
                        variableElement.getSimpleName().toString()));
                if (isGenerics) {
                    ParameterizedTypeName methodInvokableType = ParameterizedTypeName.get(
                            XTypeMirror.CLASSNAME_TYPE_REFERENCE, TypeName.get(paramType));
                    paramCodeBean.paramsInfoValueList.add(methodInvokableType);
                } else {
                    paramCodeBean.paramsInfoValueList.add(paramType);
                }

                paramCodeBean.invokeImplValueList.add(paramType);
                paramCodeBean.invokeImplValueList.add(key);
            }
        }
        paramCodeBean.invokeImplDesc.append(")");
        paramCodeBean.isASync = alreadyExistList.contains(XParam.RequestId);
        return paramCodeBean;
    }


    static class MethodParamCodeBean {
        //new ParamInfo("key1", Clazz),new ParamInfo("key2", Clazz)...
        StringBuilder paramsInfoDesc = new StringBuilder();
        List<Object> paramsInfoValueList = new ArrayList<>();
        //YourClass.YourMethod(params.get (yourKey))
        StringBuilder invokeImplDesc = new StringBuilder();
        List<Object> invokeImplValueList = new ArrayList<>();
        boolean isASync;
    }


}
