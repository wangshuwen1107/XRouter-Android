package cn.cheney.xrouter.core;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.cheney.xrouter.core.call.BaseCall;
import cn.cheney.xrouter.core.call.MethodCall;
import cn.cheney.xrouter.core.call.PageCall;
import cn.cheney.xrouter.core.constant.RouteType;
import cn.cheney.xrouter.core.exception.RouterException;
import cn.cheney.xrouter.core.invok.ActivityInvoke;
import cn.cheney.xrouter.core.invok.Invokable;
import cn.cheney.xrouter.core.invok.MethodInvokable;
import cn.cheney.xrouter.core.module.RouteModuleManager;
import cn.cheney.xrouter.core.syringe.Syringe;
import cn.cheney.xrouter.core.syringe.SyringeManager;
import cn.cheney.xrouter.core.util.Logger;


public class XRouter {

    private static final String DEFAULT_SCHEME = "xrouter";

    public static String sScheme = DEFAULT_SCHEME;

    private static WeakReference<Activity> sTopActivityRf;

    private volatile static XRouter instance = null;

    private static boolean hasInit;

    private List<RouterInterceptor> mInterceptorList;

    private RouteModuleManager mRouteModules;

    private SyringeManager mSyringeManager;


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

    public static PageCall page(String uriStr) {
        return new PageCall(uriStr);
    }


    public static <R> MethodCall<R> method(String uriStr) {
        return new MethodCall<>(uriStr);
    }


    public <C extends BaseCall> void setInvokable(C call) {
        Invokable invokable = mRouteModules.getRouteMeta(call.getModule(),
                call.getPath());
        if (null == invokable) {
            Logger.e("Can not Find Module=" + call.getModule()
                    + " path=" + call.getPath() + " Invokable");
            return;
        }
        RouteType routeType = invokable.getType();
        if (null == routeType) {
            Logger.e("Uri=" + getUriSite(call.getUri()) + " parse Failed unknown routeType");
            return;
        }
        switch (routeType) {
            case ACTIVITY:
                ActivityInvoke activityInvoke = (ActivityInvoke) invokable;
                if (!(call instanceof PageCall)) {
                    Logger.e("Uri=" + getUriSite(call.getUri())
                            + " parse -> ACTIVITY routeType but not pageCall");
                    return;
                }
                PageCall pageCall = (PageCall) call;
                pageCall.setInvokable(activityInvoke);
                return;
            case METHOD:
                MethodInvokable methodInvokable = (MethodInvokable) invokable;
                if (!(call instanceof MethodCall)) {
                    Logger.e("Uri=" + getUriSite(call.getUri())
                            + " parse -> METHOD routeType but not MethodCall");
                    return;
                }
                MethodCall methodCall = (MethodCall) call;
                methodCall.setInvokable(methodInvokable);
                return;
            default:
                Logger.w("not support route type=" + routeType.name());
        }
    }


    public Activity getTopActivity() {
        return sTopActivityRf.get();
    }

    public void addInterceptor(RouterInterceptor interceptor) {
        if (!mInterceptorList.contains(interceptor)) {
            mInterceptorList.add(interceptor);
        }
    }


    private static String getUriSite(Uri uri) {
        return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
    }


}