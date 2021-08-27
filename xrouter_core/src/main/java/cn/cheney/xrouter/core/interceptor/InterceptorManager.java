package cn.cheney.xrouter.core.interceptor;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterceptorManager {

    private final Map<String, List<InterceptorDesc>> interceptorDescMap;

    private final Map<Class<?>, RouterInterceptor> interceptorCacheMap;

    private final Object mLock = new Object();

    public InterceptorManager() {
        this.interceptorDescMap = new HashMap<>();
        this.interceptorCacheMap = new HashMap<>();
    }

    public Map<String, List<InterceptorDesc>> getInterceptorDescMap() {
        return interceptorDescMap;
    }

    public List<RouterInterceptor> getInterceptorList(String module, String path) {
        List<InterceptorDesc> allDescList = new ArrayList<>();
        if (!TextUtils.isEmpty(module)) {
            List<InterceptorDesc> moduleInterceptorList = interceptorDescMap.get(module);
            if (null != moduleInterceptorList) {
                allDescList.addAll(moduleInterceptorList);
            }
            if (!TextUtils.isEmpty(path)) {
                List<InterceptorDesc> pathInterceptorList = interceptorDescMap.get(module + "/" + path);
                if (null != pathInterceptorList) {
                    allDescList.addAll(pathInterceptorList);
                }
            }
        }
        if (allDescList.isEmpty()) {
            return new ArrayList<>();
        }
        //去重
        List<InterceptorDesc> filterDescList = new ArrayList<>();
        for (InterceptorDesc interceptorDesc : allDescList) {
            boolean has = false;
            for (InterceptorDesc filterDesc : filterDescList) {
                if (filterDesc.getClazz() == interceptorDesc.getClazz()) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                filterDescList.add(interceptorDesc);
            }
        }
        //排序
        Collections.sort(filterDescList, (o1, o2) -> o2.getPriority() - o1.getPriority());
        List<RouterInterceptor> interceptorList = new ArrayList<>();
        synchronized (mLock) {
            for (InterceptorDesc interceptorDesc : filterDescList) {
                try {
                    Class<?> clazz = interceptorDesc.getClazz();
                    RouterInterceptor interceptor = interceptorCacheMap.get(clazz);
                    if (null == interceptor) {
                        Object object = clazz.newInstance();
                        if (object instanceof RouterInterceptor) {
                            interceptor = (RouterInterceptor) object;
                            interceptorList.add(interceptor);
                        }
                        interceptorCacheMap.put(clazz, interceptor);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return interceptorList;
    }


}
