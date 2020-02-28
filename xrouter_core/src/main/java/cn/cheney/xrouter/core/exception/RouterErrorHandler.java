package cn.cheney.xrouter.core.exception;

public interface RouterErrorHandler {

    void onError(String url, String errorMsg);

}
