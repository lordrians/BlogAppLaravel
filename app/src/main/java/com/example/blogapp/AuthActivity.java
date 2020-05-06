 package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.blogapp.fragments.SignInFragment;

 public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_auth_activity, new SignInFragment()).commit();
    }
}
