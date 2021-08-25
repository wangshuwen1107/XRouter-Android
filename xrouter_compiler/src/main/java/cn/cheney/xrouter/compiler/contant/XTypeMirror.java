package cn.cheney.xrouter.compiler.contant;

import com.squareup.javapoet.ClassName;

public class XTypeMirror {
    //java
    private static final String LANG = "java.lang";
    public static final String BYTE = LANG + ".Byte";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBLE = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String CHAR = LANG + ".Character";
    public static final String STRING = LANG + ".String";
    public static final String SERIALIZABLE = "java.io.Serializable";
    public static final String LIST = "java.util.List";
    public static final String MAP = "java.util.Map";
    //android
    public static final String PARCELABLE = "android.os.Parcelable";
    public static final String ACTIVITY = "android.app.Activity";
    public static final String CONTEXT = "android.content.Context";
    //XRouter
    public static final String CALLBACK = "cn.cheney.xrouter.core.RouteCallback";
    public static final String BASE_MODULE = "cn.cheney.xrouter.core.module.BaseModule";
    public static final String INVOKABLE = "cn.cheney.xrouter.core.invok.Invokable";
    public static final String ACTIVITY_INVOKE = "cn.cheney.xrouter.core.invok.ActivityInvoke";
    public static final String SYRINGE = "cn.cheney.xrouter.core.syringe.Syringe";
    public static final String PARAM_INFO = "cn.cheney.xrouter.core.invok.ParamInfo";

    //ClassName
    public static final ClassName CLASSNAME_TYPE_REFERENCE = ClassName.get("com.alibaba.fastjson",
            "TypeReference");
    public static final ClassName CLASSNAME_METHOD_INVOKABLE =  ClassName.get("cn.cheney.xrouter.core.invok",
            "MethodInvokable");
    public static final ClassName CLASSNAME_INVOKABLE =  ClassName.get("cn.cheney.xrouter.core.invok",
            "Invokable");

}
