package xyz.glorin.sniperexample;

import xyz.glorin.sniper.lib.internal.MethodEventManager;

public class ForASM {
    ForASM() {
        MethodEventManager.getInstance().notifyMethodEnter("tag", "method");
    }
}
