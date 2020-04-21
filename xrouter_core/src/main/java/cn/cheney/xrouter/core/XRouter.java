package cn.cheney.xrouter.core;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.call.MethodCall;
import cn.cheney.xrouter.core.call.PageCall;
import cn.cheney.xrouter.core.exception.RouterErrorHandler;
import cn.cheney.xrouter.core.exception.RouterException;
import cn.cheney.xrouter.core.interceptor.BuildInvokeInterceptor;
import cn.cheney.xrouter.core.interceptor.RealChain;
import cn.cheney.xrouter.core.interceptor.RouterInterceptor;
import cn.cheney.xrouter.core.invok.Invokable;
import cn.cheney.xrouter.core.invok.ParamInfo;
import cn.cheney.xrouter.core.module.RouteModuleManager;
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

    private static boolean hasInit;

    private RouteModuleManager mRouteModules;

    private SyringeManager mSyringeManager;

    private List<RouterInterceptor> mInterceptorList;

    private ParamParser paramParser = new DefaultParser();

    private XRouter() {
        mRouteModules = new RouteModuleManager();
        mInterceptorList = new ArrayList<>();
        mSyringeManager = new SyringeManager();
    }

    public static XRouter getInstance() {
        if (!hasInit) {
            throw new RouterException("XRouter::Init::Invoke init(context) first!");
        } else {
            if (instance == null) {
                synchronized (XRouter.class) {
                    if (instance == null) {
                        instance = new XRouter();
                    }
                }
            }
            return instance;
        }
    }

    public static void init(Application context, String scheme) {
        context.registerActivityLifecycleCallbacks(new EmptyActivityLifecycleCallBack() {
            @Override
            public void onActivityResumed(Activity activity) {
                super.onActivityResumed(activity);
                sTopActivityRf = new WeakReference<>(activity);
            }
        });
        if (!TextUtils.isEmpty(scheme)) {
            sScheme = scheme;
        }
        hasInit = true;
    }

    public void inject(Activity activity) {
        if (null == activity) {
            Logger.e("inject activity not null");
            return;
        }
        Syringe syringe = mSyringeManager.getSyringe(activity);
        if (null == syringe) {
            return;
        }
        syringe.inject(activity);
    }

    public static PageCall page(@NonNull String uriStr) {
        return new PageCall(uriStr);
    }


    public static <R> MethodCall<R> method(@NonNull String uriStr) {
        return new MethodCall<>(uriStr);
    }

    public boolean build(BaseCall call) {
        mInterceptorList.add(new BuildInvokeInterceptor());
        RealChain realChain = new RealChain(call, mInterceptorList);
        Invokable invokable = realChain.proceed(call);
        if (null == invokable) {
            return false;
        }
        call.setInvokable(invokable);
        return true;
    }

    public void addInterceptor(RouterInterceptor interceptor) {
        if (!mInterceptorList.contains(interceptor)) {
            mInterceptorList.add(interceptor);
        }
    }

    private RouterErrorHandler mErrorHandler;

    public void setErrorHandler(RouterErrorHandler errorHandler) {
        this.mErrorHandler = errorHandler;
    }

    public void onError(String url, String errorMsg) {
        if (null != mErrorHandler) {
            mErrorHandler.onError(url, errorMsg);
        }
    }

    public Activity getTopActivity() {
        return sTopActivityRf.get();
    }

    public RouteModuleManager getRouteModules() {
        return mRouteModules;
    }

    private static String getUriSite(Uri uri) {
        return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
    }

    public Object parse(BaseCall call, String paramName, String paramValue) {
        Invokable invokable = XRouter.getInstance().getRouteModules().getRouteMeta(call.getModule(),
                call.getPath());
        if (null == invokable
                || TextUtils.isEmpty(paramName)) {
            return paramValue;
        }
        List<ParamInfo> params = invokable.getParams();
        for (ParamInfo param : params) {
            if (paramName.equals(param.getName())) {
                Object parseObject = null;
                try {
                    parseObject = paramParser.parse(paramName, paramValue, param.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null == parseObject ? paramValue : parseObject;
            }
        }
        return paramValue;
    }
}