package com.anwesome.ui.lockerpatterdemo;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anwesome.ui.lockerpattern.LockerPattern;

public class MainActivity extends AppCompatActivity {
    private Button b1,b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = (Button)findViewById(R.id.b1);
        b2 = (Button)findViewById(R.id.b2);
        b1.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Click1",Toast.LENGTH_SHORT).show();
            }
        });
        b2.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Click2",Toast.LENGTH_SHORT).show();
            }
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LockerPattern lockerPattern = new LockerPattern(this);
        lockerPattern.lock();
    }
}
