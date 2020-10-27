package cn.cheney.xrouter.compiler.util;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import cn.cheney.xrouter.compiler.contant.TypeKind;

import static cn.cheney.xrouter.compiler.contant.XTypeMirror.BOOLEAN;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.BYTE;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.CHAR;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.DOUBLE;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.FLOAT;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.INTEGER;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.LIST;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.LONG;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.MAP;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.PARCELABLE;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.SERIALIZABLE;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.SHORT;
import static cn.cheney.xrouter.compiler.contant.XTypeMirror.STRING;

public class TypeUtils {

    private Types types;
    private TypeMirror parcelableType;
    private TypeMirror serializableType;

    public TypeUtils(Types types, Elements elements) {
        this.types = types;
        parcelableType = elements.getTypeElement(PARCELABLE).asType();
        serializableType = elements.getTypeElement(SERIALIZABLE).asType();
    }

    /**
     * Diagnostics out the true java type
     *
     * @param element Raw type
     * @return Type class of java
     */
    public int typeExchange(Element element) {
        TypeMirror typeMirror = element.asType();
        // Primitive
        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().ordinal();
        }
        String typeStr = typeMirror.toString();
        Logger.d("页面入参 " + element.getSimpleName() + "->" + typeStr);
        if (typeStr.startsWith(LIST)) {
            typeStr = SERIALIZABLE;
        } else if (typeStr.startsWith(MAP)) {
            typeStr = SERIALIZABLE;
        }
        switch (typeStr) {
            case BYTE:
                return TypeKind.BYTE.ordinal();
            case SHORT:
                return TypeKind.SHORT.ordinal();
            case INTEGER:
                return TypeKind.INT.ordinal();
            case LONG:
                return TypeKind.LONG.ordinal();
            case FLOAT:
                return TypeKind.FLOAT.ordinal();
            case DOUBLE:
                return TypeKind.DOUBLE.ordinal();
            case BOOLEAN:
                return TypeKind.BOOLEAN.ordinal();
            case CHAR:
                return TypeKind.CHAR.ordinal();
            case STRING:
                return TypeKind.STRING.ordinal();
            case SERIALIZABLE:
                return  TypeKind.SERIALIZABLE.ordinal();
            default:
                if (types.isSubtype(typeMirror, parcelableType)) {
                    return TypeKind.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    return TypeKind.SERIALIZABLE.ordinal();
                } else {
                    return TypeKind.OBJECT.ordinal();
                }
        }
    }
}