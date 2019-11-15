package xyz.glorin.sniperexample;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import xyz.glorin.sniper.lib.annotations.SniperMethodTrack;
import xyz.glorin.sniper.lib.internal.MethodEventManager;
import xyz.glorin.sniper.lib.internal.MethodObserver;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private MethodObserver mMethodObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMethodObserver = new MethodObserver() {
            @Override
            public void onMethodEnter(String tag, String methodName) {
                Log.d(TAG, "onMethodEnter: " + methodName);
            }

            @Override
            public void onMethodExit(String tag, String methodName) {
                Log.d(TAG, "onMethodExit: " + methodName);
            }
        };
        MethodEventManager.getInstance().registerMethodObserver("test", mMethodObserver);

        testSniper();
    }

    @SniperMethodTrack(tag = "test")
    public void testSniper() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMethodObserver != null) {
            MethodEventManager.getInstance().unregisterMethodObserver(mMethodObserver);
            mMethodObserver = null;
        }
    }
}
