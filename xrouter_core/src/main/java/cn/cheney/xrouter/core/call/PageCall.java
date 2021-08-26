package cn.cheney.xrouter.core.call;

import android.content.Context;

import cn.cheney.xrouter.core.XRouter;

public class PageCall extends BaseCall<Integer> {

    private String action;
    private int enterAnim = -1;
    private int exitAnim = -1;
    private int requestCode;

    public PageCall(String uriStr) {
        super(uriStr);
    }


    public String getAction() {
        return action;
    }

    public int getEnterAnim() {
        return enterAnim;
    }

    public int getExitAnim() {
        return exitAnim;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public PageCall action(String action) {
        this.action = action;
        return this;
    }

    public PageCall requestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public PageCall anim(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }


    public PageCall put(String key, Object val) {
        this.paramsMap.put(key, val);
        return this;
    }

    @Override
    public Integer call() {
        return call(XRouter.getInstance().getTopActivity());
    }

    public Integer call(Context context) {
        this.context = context;
        return (Integer) XRouter.getInstance().proceed(this);
    }

}
