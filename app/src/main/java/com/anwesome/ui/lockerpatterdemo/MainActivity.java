package com.anwesome.ui.lockerpatterdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.anwesome.ui.lockerpattern.LockerPattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LockerPattern lockerPattern = new LockerPattern(this);
        lockerPattern.lock();
    }
}
