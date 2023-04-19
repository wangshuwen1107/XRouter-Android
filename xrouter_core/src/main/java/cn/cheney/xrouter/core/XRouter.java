package cn.cheney.xrouter.core;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cheney.xrouter.constant.GenerateFileConstant;
import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.call.MethodCall;
import cn.cheney.xrouter.core.call.PageCall;
import cn.cheney.xrouter.core.callback.EmptyActivityLifecycleCallBack;
import cn.cheney.xrouter.core.callback.RouteCallback;
import cn.cheney.xrouter.core.exception.RouterException;
import cn.cheney.xrouter.core.interceptor.BuildInvokeInterceptor;
import cn.cheney.xrouter.core.interceptor.InterceptorManager;
import cn.cheney.xrouter.core.interceptor.RealChain;
import cn.cheney.xrouter.core.interceptor.RouterInterceptor;
import cn.cheney.xrouter.core.invok.Invokable;
import cn.cheney.xrouter.core.invok.ParamInfo;
import cn.cheney.xrouter.core.parser.DefaultParser;
import cn.cheney.xrouter.core.parser.ParamParser;
import cn.cheney.xrouter.core.syringe.Syringe;
import cn.cheney.xrouter.core.syringe.SyringeManager;
import cn.cheney.xrouter.core.util.Logger;


public class XRouter {

    private static final String DEFAULT_SCHEME = "xrouter";

    public static String sScheme = DEFAULT_SCHEME;

    private static WeakReference<Activity> sTopActivityRf;

    private volatile static XRouter instance = null;

    private static boolean sHasInit;

    private final Map<String, Invokable<?>> mRouterMap = new HashMap<>();

    private final SyringeManager mSyringeManager;

    private final InterceptorManager mInterceptorManager;

    private final ParamParser mParamParser = new DefaultParser();


    private XRouter() {
        mSyringeManager = new SyringeManager();
        mInterceptorManager = new InterceptorManager();
    }

    public static XRouter getInstance() {
        if (!sHasInit) {
            throw new RouterException(RouterException.NOT_INIT);
        }
        if (instance == null) {
            synchronized (XRouter.class) {
                if (instance == null) {
                    instance = new XRouter();
                }
            }
        }
        return instance;

    }

    public static void init(Application context, String scheme) {
        if (sHasInit) {
            return;
        }
        sHasInit = true;
        context.registerActivityLifecycleCallbacks(new EmptyActivityLifecycleCallBack() {
            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                super.onActivityResumed(activity);
                sTopActivityRf = new WeakReference<>(activity);
            }
        });
        if (!TextUtils.isEmpty(scheme)) {
            sScheme = scheme;
        }
        loadRouters();
        loadInterceptor();
    }

    private static void loadRouters() {
        long startTime = System.currentTimeMillis();
        try {
            Class<?> loadClass = Class.forName(GenerateFileConstant.ALL_ROUTER_LOAD_CLASS);
            Method loadInto = loadClass.getMethod("loadInto", Map.class);
            loadInto.invoke(null, getInstance().mRouterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.d("loadRouters size=" + getInstance().mRouterMap.entrySet().size()
                + " time=" + (System.currentTimeMillis() - startTime));
    }


    private static void loadInterceptor() {
        long startTime = System.currentTimeMillis();
        try {
            Class<?> loadClass = Class.forName(GenerateFileConstant.ALL_INTERCEPTOR_LOAD_CLASS);
            Method loadInto = loadClass.getMethod("loadInto", Map.class);
            loadInto.invoke(null, getInstance().mInterceptorManager.getInterceptorDescMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.d("load Interceptor time=" + (System.currentTimeMillis() - startTime));
    }

    public void inject(Activity activity) {
        if (null == activity) {
            Logger.e("inject activity not null");
            return;
        }
        Syringe syringe = mSyringeManager.getSyringe(activity);
        if (null == syringe) {
            Logger.e("inject activity syringe not found");
            return;
        }
        syringe.inject(activity);
    }

    public static PageCall page(String uriStr) {
        return new PageCall(uriStr);
    }


    public static <R> MethodCall<R> method(String uriStr) {
        return new MethodCall<>(uriStr);
    }


    public void invokeCallback(String requestId, Map<String, Object> resultMap) {
        RequestManager.getInstance().invokeCallback(requestId, resultMap);
    }

    public Object proceed(BaseCall<?> call, RouteCallback callback) {
        List<RouterInterceptor> interceptorList =
                mInterceptorManager.getInterceptorList(call.getModule(), call.getPath());
        interceptorList.add(new BuildInvokeInterceptor());
        RealChain realChain = new RealChain(call, interceptorList);
        Object value = null;
        try {
            value = realChain.proceed();
        } catch (RouterException e) {
            if (e.getCode() == RouterException.NOT_FOUND) {
                if (null != callback) {
                    callback.notFound();
                }
            } else if (e.getCode() == RouterException.HAS_INTERCEPT) {
                if (null != callback) {
                    callback.hasIntercept();
                }
            }
            if (null != callback) {
                callback.onError(e.getCode(), e.getMessage());
            }
        }
        return value;
    }

    public Activity getTopActivity() {
        return sTopActivityRf.get();
    }

    public Map<String, Invokable<?>> getRouterMap() {
        return mRouterMap;
    }

    public Object parse(BaseCall<?> call, String paramName, String paramValue) {
        Invokable<?> invokable = mRouterMap.get(call.getModule() + "/" + call.getPath());
        if (null == invokable || TextUtils.isEmpty(paramName)) {
            return paramValue;
        }
        List<ParamInfo> params = invokable.getParams();
        for (ParamInfo param : params) {
            if (paramName.equals(param.getName())) {
                Object parseObject = null;
                try {
                    parseObject = mParamParser.parse(paramName, paramValue, param.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null == parseObject ? paramValue : parseObject;
            }
        }
        return paramValue;
    }


}