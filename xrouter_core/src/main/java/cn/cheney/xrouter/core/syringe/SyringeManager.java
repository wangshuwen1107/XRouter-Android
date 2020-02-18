package cn.cheney.xrouter.core.syringe;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import cn.cheney.xrouter.core.constant.GenerateFileConstant;

public class SyringeManager {

    private Map<String, Syringe> syringeMap;

    public SyringeManager() {
        this.syringeMap = new HashMap<>();
    }

    public Syringe getSyringe(String className) {
        if (TextUtils.isEmpty(className)) {
            return null;
        }
        Syringe syringe = syringeMap.get(className);
        if (null == syringe) {
            try {
                syringe = (Syringe) Class.forName(GenerateFileConstant.PARAM_CLASS_PREFIX +
                        className).newInstance();
                if (null != syringe) {
                    syringeMap.put(className, syringe);
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return syringe;
    }


}
