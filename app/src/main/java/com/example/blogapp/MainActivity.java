package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //this code will pause the app for 1.5 secs and then any thing in run method will run
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences userPref = getSharedPreferences(GlobalVar.FILE_USER, 0);
                boolean isLoggedIn = userPref.getBoolean(GlobalVar.VAR_IS_LOGGED_IN, false);

                if (isLoggedIn){
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                } else {
                    isFirstTime();
                }

            }
        },1500);

    }

    private void isFirstTime() {
        //ini buat ngecek pertama kali buka apps atau engga
        SharedPreferences preferences = getSharedPreferences(GlobalVar.FILE_ONBOARD, MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(GlobalVar.VAR_ISFIRST_TIME, true);
        //defaultnya true
        if (isFirstTime){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(GlobalVar.VAR_ISFIRST_TIME, false);
            editor.apply();

            startActivity(new Intent(getApplicationContext(), OnBoardActivity.class));
            finish();
        } else {
            startActivity(new Intent(getApplicationContext(), AuthActivity.class));
            finish();
        }

    }
}
