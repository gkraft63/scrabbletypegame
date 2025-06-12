package jscrabble;

public final class License {
    public static final int STATE_UNKNOWN = 999;
    public static final int STATE_OK = 111;
    public static final int STATE_EXPIRED = 403;
    public static final int STATE_INVALID = 502;
    public static final int STATE_NOT_ACTIVATED = 180;
    public static final int STATE_LOCK_VIOLATED = 640;
    
    private int state;
    private Throwable throwable;
    
    public License(int state, Throwable x) {
        this.state = state;
        this.throwable = x;
    }
    
    public int getState() {
        return state;
    }
    public Throwable getThrowable() {
        return throwable;
    }
}
