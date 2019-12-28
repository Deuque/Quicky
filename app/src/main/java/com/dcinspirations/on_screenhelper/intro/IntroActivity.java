package com.dcinspirations.on_screenhelper.intro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dcinspirations.on_screenhelper.BaseApp;
import com.dcinspirations.on_screenhelper.DatabaseClass;
import com.dcinspirations.on_screenhelper.FloatViewManager;
import com.dcinspirations.on_screenhelper.R;
import com.dcinspirations.on_screenhelper.Sp;
import com.github.islamkhsh.CardSliderViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class IntroActivity extends AppCompatActivity {
    private static final int req1 = 93;

    ArrayList<String> words;
    DatabaseClass db;
    JSONObject job;
    ArrayList<IntroModel> items;
    int imgids[] = {R.mipmap.s5,R.mipmap.sp4,R.mipmap.o2,R.mipmap.e2};
    String desarray[] = {"Quickly look up words or take short notes while reading or browsing.","Listen to word pronunciations quickly.",
                            "Experience multitasking with the floating window, show or hide with icon tap.",
                            "Safe and easy to use."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        items = new ArrayList<>();
        words = new ArrayList<>();
        db = new DatabaseClass(this);
        setItems();

        TextView launch = findViewById(R.id.launch);
        launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkPermission();
            }
        });


    }

    private void setItems(){
        for(int i=0;i<imgids.length;i++){
            IntroModel im = new IntroModel(imgids[i],desarray[i]);
            items.add(im);
        }
        CardSliderViewPager cardSliderViewPager = (CardSliderViewPager) findViewById(R.id.viewPager);
        cardSliderViewPager.setAdapter(new IntroAdapter(items));
    }

    private void checkPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.canDrawOverlays(this)){
                new Sp(IntroActivity.this).setStatus(true);
                new FloatViewManager(BaseApp.ctx).showFloatView();
                finish();
            }else{
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, req1);
            }
        }else{
            new Sp(IntroActivity.this).setStatus(true);
            new FloatViewManager(BaseApp.ctx).showFloatView();
            finish();
        }
    }


}
