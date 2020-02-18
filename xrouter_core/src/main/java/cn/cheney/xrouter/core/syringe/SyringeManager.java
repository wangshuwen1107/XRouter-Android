package cn.cheney.xrouter.core.syringe;

import java.util.HashMap;
import java.util.Map;

import static cn.cheney.xrouter.core.constant.GenerateFileConstant.PARAM_FILE_PREFIX;

public class SyringeManager {

    private Map<String, Syringe> syringeMap;

    public SyringeManager() {
        this.syringeMap = new HashMap<>();
    }

    public Syringe getSyringe(Object object) {
        if (null == object) {
            return null;
        }
        String className = object.getClass().getSimpleName();
        String packageName = object.getClass().getPackage().getName();
        Syringe syringe = syringeMap.get(className);
        if (null == syringe) {
            try {
                syringe = (Syringe) Class.forName(getFileName(packageName, className)).newInstance();
                if (null != syringe) {
                    syringeMap.put(className, syringe);
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return syringe;
    }


    private String getFileName(String packageName, String className) {
        return packageName + "." + PARAM_FILE_PREFIX + className;
    }

}
