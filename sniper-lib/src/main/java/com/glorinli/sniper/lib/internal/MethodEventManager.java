package com.glorinli.sniper.lib.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodEventManager {
    private static volatile MethodEventManager sInstance;
    private Map<String, List<MethodObserver>> mObserverMap = new HashMap<>();

    private MethodEventManager() {}

    public static MethodEventManager getInstance() {
        if (sInstance == null) {
            synchronized (MethodEventManager.class) {
                if (sInstance == null) {
                    sInstance = new MethodEventManager();
                }
            }
        }

        return sInstance;
    }

    /**
     * Register an observer to with a tag
     * @param tag
     * @param observer
     */
    public void registerMethodObserver(String tag, MethodObserver observer) {
        if (observer == null) {
            return;
        }

        List<MethodObserver> observers = mObserverMap.get(tag);
        if (observers == null) {
            observers = new ArrayList<>();
            mObserverMap.put(tag, observers);
        }

        observers.add(observer);
    }

    /**
     * Notify observers that the method is entering
     * @param tag
     * @param methodName
     */
    public void notifyMethodEnter(String tag, String methodName) {
        List<MethodObserver> observers = mObserverMap.get(tag);

        if (observers == null) {
            return;
        }

        for (MethodObserver observer : observers) {
            observer.onMethodEnter(tag, methodName);
        }
    }

    /**
     * Notify observers that the method is exiting
     * @param tag
     * @param methodName
     */
    public void notifyMethodExit(String tag, String methodName) {
        List<MethodObserver> observers = mObserverMap.get(tag);

        if (observers == null) {
            return;
        }

        for (MethodObserver observer : observers) {
            observer.onMethodExit(tag, methodName);
        }
    }
}
