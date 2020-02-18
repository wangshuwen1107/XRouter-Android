package cn.cheney.xrouter.compiler.util;

import java.util.Set;

import javax.lang.model.element.Modifier;

public class CheckUtil {

    public static String checkModifiers(Set<Modifier> modifiers) {
        if (null == modifiers || modifiers.isEmpty()) {
            return "package";
        }
        for (Modifier modifier : modifiers) {
            if (!modifier.toString().equals("public")) {
                return modifier.toString();
            }
        }
        return null;
    }

}

