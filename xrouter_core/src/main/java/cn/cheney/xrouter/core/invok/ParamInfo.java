package cn.cheney.xrouter.core.invok;

import java.lang.reflect.Type;

public class ParamInfo {

    private String name;
    private Type type;

    public ParamInfo(String name, Type type) {
        this.name = name;
        this.type = type;
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
