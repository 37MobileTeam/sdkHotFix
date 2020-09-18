package com.sq.mobile.sdkhotfix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sq.mobile.pluhost.SqWanCore;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SqWanCore.getInstance(this).login();
        SqWanCore.getInstance(this).pay();
    }
}
