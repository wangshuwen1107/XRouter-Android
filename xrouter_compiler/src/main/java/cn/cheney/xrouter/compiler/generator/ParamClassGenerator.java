package cn.cheney.xrouter.compiler.generator;


import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import cn.cheney.xrouter.annotation.XParam;
import cn.cheney.xrouter.compiler.XRouterProcessor;
import cn.cheney.xrouter.compiler.contant.TypeKind;
import cn.cheney.xrouter.core.constant.GenerateFileConstant;
import cn.cheney.xrouter.core.syringe.Syringe;

public class ParamClassGenerator {

    private TypeElement activityElement;
    private String className;
    private String fileName;


    public ParamClassGenerator(TypeElement activityElement) {
        this.activityElement = activityElement;
        this.className = activityElement.getSimpleName().toString();
        this.fileName = GenerateFileConstant.PARAM_FILE_PREFIX + className;
        methodBuilder.addStatement("$T activity =($T)target",
                this.activityElement.asType(),
                this.activityElement.asType());
    }

    /**
     * 最后生产java文件
     */
    public void generateJavaFile(XRouterProcessor.Holder holder) {
        Name qualifiedName = holder.elementUtils.getPackageOf(activityElement).getQualifiedName();
        TypeSpec typeSpec = TypeSpec.classBuilder(fileName)
                .addSuperinterface(Syringe.class)
                .addJavadoc(GenerateFileConstant.WARNING_TIPS)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        JavaFile javaFile = JavaFile.builder(qualifiedName.toString(), typeSpec)
                .build();
        try {
            javaFile.writeTo(holder.filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * public void inject(Object target){
     * Activity activity = (Activity) target;
     * }
     */
    private MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("inject")
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.VOID)
            .addAnnotation(Override.class)
            .addParameter(Object.class, "target");

    /**
     * activity.key = activity.getIntent().getStringExtra()
     */
    public void generateSeg(XRouterProcessor.Holder holder, VariableElement variableElement, XParam param) {
        String key = param.name().isEmpty() ? variableElement.getSimpleName().toString() : param.name();
        String getExtraStr = getExtraByType(holder.typeUtils.typeExchange(variableElement));
        if (getExtraStr.isEmpty()) {
            return;
        }
        methodBuilder.addStatement("activity.$L =($L)activity.getIntent()." + getExtraStr,
                variableElement.getSimpleName(),
                variableElement.asType(),
                key);
    }


    private String getExtraByType(int type) {
        switch (TypeKind.values()[type]) {
            case BYTE:
                return "getByteExtra($S)";
            case BOOLEAN:
                return "getBooleanExtra($S)";
            case SHORT:
                return "getShortExtra($S)";
            case INT:
                return "getIntExtra($S)";
            case LONG:
                return "getLongExtra($S)";
            case FLOAT:
                return "getFloatExtra($S)";
            case DOUBLE:
                return "getDoubleExtra($S)";
            case CHAR:
                return "getCharExtra($S)";
            case STRING:
                return "getStringExtra($S)";
            case SERIALIZABLE:
                return "getSerializableExtra($S)";
            case PARCELABLE:
                return "getParcelableExtra($S)";
        }
        return "";
    }
}
