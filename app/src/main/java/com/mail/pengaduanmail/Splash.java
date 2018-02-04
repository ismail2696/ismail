package com.mail.pengaduanmail;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Bundle;

import com.mail.pengaduanmail.R;

public class Splash extends Activity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        new Handler().postDelayed(this, 1000);
    }

    @Override
    public void run() {
        Intent intent = new Intent(Splash.this, Login.class);
        startActivity(intent);
        finish();
    }
}
