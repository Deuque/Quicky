package com.dcinspirations.on_screenhelper;

import android.content.Intent;
import android.os.Bundle;

import com.dcinspirations.on_screenhelper.intro.IntroActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.dcinspirations.on_screenhelper.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    SectionsPagerAdapter sectionsPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        finish();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(new Sp(this).getStatus()) {
            new FloatViewManager(BaseApp.ctx).showFloatView();
            Toast.makeText(this, "Quicky started", Toast.LENGTH_SHORT).show();
        }else{
            startActivity(new Intent(this, IntroActivity.class));
        }
        finish();
    }
}