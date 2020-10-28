package cn.cheney.xrouter.core.invok;

import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Type;
import java.util.Map;

public class ParamInfo {

    private String name;
    private Type type;

    public ParamInfo(String name, Type type) {
        this.name = name;
        this.type = type;
        this.type = new TypeReference<Map<String, Object>>(){}.getType();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
