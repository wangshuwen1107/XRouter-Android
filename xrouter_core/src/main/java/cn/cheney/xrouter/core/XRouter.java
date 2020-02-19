package cn.cheney.xrouter.core;

import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.cheney.xrouter.core.constant.RouteType;
import cn.cheney.xrouter.core.entity.XRouteMeta;
import cn.cheney.xrouter.core.exception.RouterException;
import cn.cheney.xrouter.core.invok.ActivityInvoke;
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

    public static RouteIntent.Builder build(String uriStr) {
        return RouteIntent.newBuilder(uriStr);
    }

    void start(RouteIntent routeIntent, int requestCode) {
        XRouteMeta routeMeta = mRouteModules.getRouteMeta(routeIntent.getModule(),
                routeIntent.getPath());
        if (null == routeMeta) {
            return;
        }
        RouteType routeType = routeMeta.getType();
        if (null == routeType) {
            return;
        }
        switch (routeType) {
            case ACTIVITY:
                ActivityInvoke activityInvoke = ActivityInvoke.newBuilder()
                        .className(routeMeta.getClassName())
                        .requestCode(requestCode)
                        .action(routeIntent.getAction())
                        .anim(routeIntent.getEnterAnim(), routeIntent.getExitAnim())
                        .build();
                if (null == activityInvoke) {
                    Logger.w("ActivityInvoke build Failed checkYou class");
                    return;
                }
                activityInvoke.invoke(sTopActivityRf.get(), routeIntent.getParamsMap());
                break;
            case METHOD:

                break;
            default:
                Logger.w("not support route type=" + routeType.name());
        }
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