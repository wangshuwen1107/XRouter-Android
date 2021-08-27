package cn.cheney.xrouter.core.interceptor;

public class InterceptorDesc {

    private final int priority;

    private final Class<?> clazz;

    public InterceptorDesc(int priority, Class<?> clazz) {
        this.priority = priority;
        this.clazz = clazz;
    }

    public int getPriority() {
        return priority;
    }

    public Class<?> getClazz() {
        return clazz;
    }


}
