# Sniper
A method tracker for Androd.

## Integration
1. In your top level build.gradle, or module build.gradle, introduce sniper to class path.
``` groovy
buildscript {
    repositories {
        ...
        jcenter()
    }
    dependencies {
        classpath "xyz.glorin:sniper:0.0.4"
    }
}
```
2. In module that you want to use sniper, apply the plugin:
``` groovy
apply plugin: 'xyz.glorin.sniper'
```
3. Register method observer where you want:
``` java
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
```
The method `testSniper()` is annotated by `@SniperMethodTrack`, so it will be tracked.
