package xyz.glorin.sniper.lib.internal;

/**
 * Method enter and exit observer
 */
public interface MethodObserver {
    /**
     * Called when methods enter
     * @param tag
     * @param methodName
     */
    void onMethodEnter(String tag, String methodName);

    /**
     * Called when emthods exit
     * @param tag
     * @param methodName
     */
    void onMethodExit(String tag, String methodName);
}
