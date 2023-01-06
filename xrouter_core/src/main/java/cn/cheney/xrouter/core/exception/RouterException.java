package cn.cheney.xrouter.core.exception;

public class RouterException extends RuntimeException {

    public RouterException(int code) {
        this.code = code;
    }

    public RouterException(int code,String message) {
        super(message);
        this.code = code;
    }

    public static final int NOT_INIT = -100;
    public static final int NOT_FOUND = -1000;
    public static final int INVOKE_ERROR = -1002;
    public static final int UN_KNOWN = -1001;
    public static final int UN_SUPPORT_EXTRA_TYPE = -1001;


    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
