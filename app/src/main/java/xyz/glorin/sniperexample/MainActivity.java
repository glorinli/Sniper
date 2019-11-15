package xyz.glorin.sniperexample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import xyz.glorin.sniper.lib.annotations.SniperMethodTrack;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testSniper();
    }

    @SniperMethodTrack(tag = "test")
    public void testSniper() {

    }
}
