package cn.cheney.xrouter.core;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.call.MethodCall;
import cn.cheney.xrouter.core.call.PageCall;
import cn.cheney.xrouter.core.callback.EmptyActivityLifecycleCallBack;
import cn.cheney.xrouter.core.exception.RouterErrorHandler;
import cn.cheney.xrouter.core.exception.RouterException;
import cn.cheney.xrouter.core.interceptor.BuildInvokeInterceptor;
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

    private static final String ROUTER_LOAD_CLASS = "cn.cheney.xrouter.XRouterModule_Loader";

    public static String sScheme = DEFAULT_SCHEME;

    private static WeakReference<Activity> sTopActivityRf;

    private volatile static XRouter instance = null;

    private static boolean hasInit;

    private final static Map<String, Invokable<?>> sRouterMap = new HashMap<>();

    private final SyringeManager mSyringeManager;

    private final List<RouterInterceptor> mInterceptorList;

    private final ParamParser mParamParser = new DefaultParser();

    private RouterErrorHandler mErrorHandler;

    private XRouter() {
        mInterceptorList = new ArrayList<>();
        mSyringeManager = new SyringeManager();
    }

    public static XRouter getInstance() {
        if (!hasInit) {
            throw new RouterException("XRouter::Init::Invoke init(context) first!");
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
        hasInit = true;
    }

    private static void loadRouters() {
        try {
            Class<?> loadClass = Class.forName(ROUTER_LOAD_CLASS);
            Method loadInto = loadClass.getMethod("loadInto", Map.class);
            loadInto.invoke(null, sRouterMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void addInterceptor(RouterInterceptor interceptor) {
        if (!mInterceptorList.contains(interceptor)) {
            mInterceptorList.add(interceptor);
        }
    }

    public void setErrorHandler(RouterErrorHandler errorHandler) {
        this.mErrorHandler = errorHandler;
    }

    public Object proceed(BaseCall<?> call) {
        addInterceptor(new BuildInvokeInterceptor());
        RealChain realChain = new RealChain(call, mInterceptorList);
        return realChain.proceed(call);
    }

    public void onError(String url, String errorMsg) {
        if (null != mErrorHandler) {
            mErrorHandler.onError(url, errorMsg);
        }
    }

    public Activity getTopActivity() {
        return sTopActivityRf.get();
    }


    public static Map<String, Invokable<?>> getRouterMap() {
        return sRouterMap;
    }

    public Object parse(BaseCall<?> call, String paramName, String paramValue) {
        Invokable<?> invokable = sRouterMap.get(call.getModule() + "/" + call.getPath());
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

    private static String getUriSite(Uri uri) {
        return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
    }

}