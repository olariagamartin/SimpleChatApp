package com.themarto.mychatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.themarto.mychatapp.loginActivity.LoginActivity;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME = 1000;
    private FirebaseUser user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        user = FirebaseAuth.getInstance().getCurrentUser();

        setFullScreenActivity();

        setSplashScreenFinish();

    }

    private void setFullScreenActivity () {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setSplashScreenFinish () {
        new Handler().postDelayed(() -> {
            if (user != null) {
                goToMainActivity();
                Log.w("LoginTag","Firebase User: " + user.toString());
            } else {
                goToLoginActivity();
            }
        }, SPLASH_TIME);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SplashScreen.this, ChatListActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}