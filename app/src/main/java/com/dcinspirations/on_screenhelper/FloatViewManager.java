package com.dcinspirations.on_screenhelper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.input.InputManager;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dcinspirations.on_screenhelper.ui.main.SectionsPagerAdapter;
import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import me.piruin.quickaction.ActionItem;
import me.piruin.quickaction.QuickAction;
import me.piruin.quickaction.QuickIntentAction;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.WINDOW_SERVICE;


/**
 * Created by aminography on 11/1/2018.
 */
public class FloatViewManager {

    private static boolean mIsFloatViewShowing;

    private Context mActivity;
    private WindowManager mWindowManager;
    private View mFloatView;
    private WindowManager.LayoutParams mFloatViewLayoutParams;
    private boolean mFloatViewTouchConsumedByMove = false;
    private int mFloatViewLastX;
    private int mFloatViewLastY;
    private int mFloatViewFirstX;
    private int mFloatViewFirstY;
    private boolean isShown = false;
    private ImageView img,cancel,ct,ct2;
    private LinearLayout load;
    private LinearLayout views,empty;
    private AutoCompleteTextView text;
    private ImageButton search,speak;
    private TextView result;
    private ArrayList<String> words;
    private RelativeLayout dl,nl,rl2;
    private ImageButton add;
    private AutoCompleteTextView nottext;
    private DatabaseClass dbclass;
    List<notemodel> notearray;
    RecyclerView rv;
    notes_adapter ma;
    ArrayAdapter<String> adapter;
    WordSpeak wordSpeak;
    DbHelper dbHelper;
    Handler handler=new Handler();
    Handler handler2 =new Handler();
    String res="";
    Runnable runnable =  new Runnable() {
        @Override
        public void run() {

            if(!res.isEmpty()){
                result.setText(res);
                res="";
                load.setVisibility(View.GONE);
                result.setVisibility(View.VISIBLE);
                handler.removeCallbacks(runnable);
            }else{
                handler.postDelayed(runnable,500);
            }
        }
    };


    @SuppressLint("InflateParams")
    public FloatViewManager(final Context activity) {
        mActivity = activity;
        wordSpeak = new WordSpeak(mActivity);
        dbclass = new DatabaseClass(mActivity);
        notearray = new ArrayList<>();
        words = new ArrayList<>();


        LayoutInflater inflater = LayoutInflater.from(activity);
        mFloatView = inflater.inflate(R.layout.onscreen, null);

        dbHelper = new DbHelper(mActivity,1,"garbage.db");
        try{
            dbHelper.openDatabase();

        }catch (Exception e){ Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();}
        try {
            dbHelper.createDatabase();
        }
        catch (Exception e){
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        load = mFloatView.findViewById(R.id.loading);
        rl2 = mFloatView.findViewById(R.id.rl2);

        empty =mFloatView.findViewById(R.id.empty);
        ct = mFloatView.findViewById(R.id.ct);
        ct2 = mFloatView.findViewById(R.id.ct2);
        dl = mFloatView.findViewById(R.id.diclayout);
        nl = mFloatView.findViewById(R.id.notlayout);
        img = mFloatView.findViewById(R.id.icon);
        cancel = mFloatView.findViewById(R.id.cancel);
        views = mFloatView.findViewById(R.id.views);
        text = mFloatView.findViewById(R.id.text);
        text.setDropDownBackgroundResource(R.color.aux1);
        result = mFloatView.findViewById(R.id.result);
        result.setMovementMethod(new ScrollingMovementMethod());
        search=mFloatView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
        ct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("");
            }
        });
        ct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nottext.setText("");
            }
        });
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 1){
                    ct.setVisibility(View.VISIBLE);
                    words.clear();
                    words.addAll(dbHelper.GetAllWords(s.toString()));

                    text.setAdapter(new ArrayAdapter<String>(mActivity,
                            R.layout.autocomplete,words));

                }else{
                        ct.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               performSearch();
            }
        });

        speak = mFloatView.findViewById(R.id.speak);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordSpeak.speak(text.getText().toString());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissFloatView();
            }
        });
        nottext = mFloatView.findViewById(R.id.notetext);
        nottext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 1){
                    ct2.setVisibility(View.GONE);
                }else {
                    ct2.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        add = mFloatView.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert(nottext.getText().toString().trim());
            }
        });
//        text.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                moveUp();
//                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(text,InputMethodManager.SHOW_IMPLICIT);
//                text.requestFocus();
//                moveUp();
//                return false;
//            }
//        });

        text.setOnLongClickListener(onLongClickListener);
        nottext.setOnLongClickListener(onLongClickListener);
        img.setOnTouchListener(mFloatViewOnTouchListener2);
        mFloatView.setOnTouchListener(mFloatViewOnTouchListener);

        mFloatViewLayoutParams = new WindowManager.LayoutParams();
        mFloatViewLayoutParams.format = PixelFormat.TRANSLUCENT;
        mFloatViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mFloatViewLayoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;



        mFloatViewLayoutParams.type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_TOAST;


        mFloatViewLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatViewLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatViewLayoutParams.gravity = Gravity.CENTER;
        adjust();
        setupTablayout();

        ma = new notes_adapter(mActivity,notearray);
        rv =  mFloatView.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(mActivity);
        llm.setOrientation(RecyclerView.VERTICAL);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(ma);
        populate();

    }

    public void performSearch(){
        if(!text.getText().toString().isEmpty()) {
            empty.setVisibility(View.GONE);
            result.setVisibility(View.GONE);
            load.setVisibility(View.VISIBLE);
            JsonThread jt = new JsonThread();
            jt.start();

            handler.post(runnable);

        }
    }
    public void showOrHide(){
        if(isShown){
            mFloatViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            views.setVisibility(View.GONE);
            isShown = false;
        }else{
            mFloatViewLayoutParams.flags = mFloatViewLayoutParams.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mFloatViewLayoutParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            mFloatViewLayoutParams.dimAmount = 0.6f;
            views.setVisibility(View.VISIBLE);
            isShown = true;

        }
        moveLeft();
    }
    public void setupTablayout(){
        final TabLayout tabs = mFloatView.findViewById(R.id.tabs);
        TabLayout.Tab tab1 = tabs.newTab();
        tab1.setText("Dictionary");
        TabLayout.Tab tab2 = tabs.newTab();
        tab2.setText("Notes");
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab==tabs.getTabAt(0)) {
                    dl.setVisibility(View.VISIBLE);
                    nl.setVisibility(View.GONE);
                }else{
                    dl.setVisibility(View.GONE);
                    nl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabs.addTab(tab1,0,true);
        tabs.addTab(tab2,1);



    }
    public void dismissFloatView() {
        if (mIsFloatViewShowing) {
            mIsFloatViewShowing = false;
                    if (mWindowManager != null) {
                        mWindowManager.removeViewImmediate(mFloatView);
                    }
                }

    }
    public void showFloatView() {
        if (!mIsFloatViewShowing) {
            mIsFloatViewShowing = true;
                        mWindowManager = (WindowManager) mActivity.getSystemService(WINDOW_SERVICE);
                        if (mWindowManager != null) {
                            mWindowManager.addView(mFloatView, mFloatViewLayoutParams);
                        }

                }

    }
    public void moveLeft(){
        WindowManager.LayoutParams prm = mFloatViewLayoutParams;

        prm.x += -20;
        mFloatViewTouchConsumedByMove = true;
        if (mWindowManager != null) {
            mWindowManager.updateViewLayout(mFloatView, prm);
        }


    }
    public void adjust(){
        WindowManager.LayoutParams prm = mFloatViewLayoutParams;

        prm.x += 550;
        prm.y-=480;
        mFloatViewTouchConsumedByMove = true;
        if (mWindowManager != null) {
            mWindowManager.updateViewLayout(mFloatView, prm);
        }


    }

//    private void bubbleDescriptions(){
//        BubbleShowCaseBuilder firstcase = new BubbleShowCaseBuilder((Activity) mActivity)//Activity instance
//                .description("Click to show or hide this view as you multitask!") //More detailed description
//                .arrowPosition(BubbleShowCase.ArrowPosition.RIGHT) //You can force the position of the arrow to change the location of the bubble.
//                .backgroundColor(Color.GREEN) //Bubble background color
//                .textColor(Color.BLACK) //Bubble Text color
//                .titleTextSize(17) //Title text size in SP (default value 16sp)
//                .descriptionTextSize(15)
//                .showOnce("1")
//                .targetView(img);
//    }
    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            final AutoCompleteTextView act = (AutoCompleteTextView) v;
            act.selectAll();
            PopupMenu pm = new PopupMenu(v.getContext(),v, Gravity.END);
            if(act.getText().toString().isEmpty()){
                pm.getMenuInflater().inflate(R.menu.clips2,pm.getMenu());
            }else{
                pm.getMenuInflater().inflate(R.menu.clips,pm.getMenu());
            }

            pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.paste:
                            try {
                                ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(CLIPBOARD_SERVICE);
                                ClipData cdata = clipboard.getPrimaryClip();
                                ClipData.Item citem = cdata.getItemAt(0);
                                act.setText(citem.getText().toString());
                                act.setSelection(act.getText().toString().length());
                                return true;
                            }catch (Exception e){}
                        case R.id.copy:
                            ClipboardManager clipboard2 = (ClipboardManager) mActivity.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("text", act.getText());
                            clipboard2.setPrimaryClip(clip);
                    }
                    return false;
                }
            });
            pm.show();
            return false;
        }
    };



    @SuppressWarnings("FieldCanBeLocal")
    private View.OnTouchListener mFloatViewOnTouchListener = new View.OnTouchListener() {

        @SuppressLint("ClickableViewAccessibility")
        @TargetApi(Build.VERSION_CODES.FROYO)
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            WindowManager.LayoutParams prm = mFloatViewLayoutParams;
            int totalDeltaX = mFloatViewLastX - mFloatViewFirstX;
            int totalDeltaY = mFloatViewLastY - mFloatViewFirstY;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mFloatViewLastX = (int) event.getRawX();
                    mFloatViewLastY = (int) event.getRawY();
                    mFloatViewFirstX = mFloatViewLastX;
                    mFloatViewFirstY = mFloatViewLastY;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) event.getRawX() - mFloatViewLastX;
                    int deltaY = (int) event.getRawY() - mFloatViewLastY;
                    mFloatViewLastX = (int) event.getRawX();
                    mFloatViewLastY = (int) event.getRawY();
                    if (Math.abs(totalDeltaX) >= 5 || Math.abs(totalDeltaY) >= 5) {
                        if (event.getPointerCount() == 1) {
                            prm.x += deltaX;
                            prm.y += deltaY;
                            mFloatViewTouchConsumedByMove = true;
                            if (mWindowManager != null) {
                                mWindowManager.updateViewLayout(mFloatView, prm);
                            }
                        } else {
                            mFloatViewTouchConsumedByMove = false;
                        }
                    } else {
                        mFloatViewTouchConsumedByMove = false;
                    }
//                    moveUp();
                    break;
                default:
                    break;
            }
            return mFloatViewTouchConsumedByMove;
        }
    };
    private View.OnTouchListener mFloatViewOnTouchListener2 = new View.OnTouchListener() {

        private static final int MAX_CLICK_DURATION = 200;
        private long startclick;
        @SuppressLint("ClickableViewAccessibility")
        @TargetApi(Build.VERSION_CODES.FROYO)
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            WindowManager.LayoutParams prm = mFloatViewLayoutParams;
            int totalDeltaX = mFloatViewLastX - mFloatViewFirstX;
            int totalDeltaY = mFloatViewLastY - mFloatViewFirstY;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startclick = Calendar.getInstance().getTimeInMillis();

                    mFloatViewLastX = (int) event.getRawX();
                    mFloatViewLastY = (int) event.getRawY();
                    mFloatViewFirstX = mFloatViewLastX;
                    mFloatViewFirstY = mFloatViewLastY;
                    break;
                case MotionEvent.ACTION_UP:
                    long clickdur = Calendar.getInstance().getTimeInMillis() - startclick;
                    if(clickdur<MAX_CLICK_DURATION){
                            showOrHide();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) event.getRawX() - mFloatViewLastX;
                    int deltaY = (int) event.getRawY() - mFloatViewLastY;
                    mFloatViewLastX = (int) event.getRawX();
                    mFloatViewLastY = (int) event.getRawY();
                    if (Math.abs(totalDeltaX) >= 5 || Math.abs(totalDeltaY) >= 5) {
                        if (event.getPointerCount() == 1) {
                            prm.x += deltaX;
                            prm.y += deltaY;
                            mFloatViewTouchConsumedByMove = true;
                            if (mWindowManager != null) {
                                mWindowManager.updateViewLayout(mFloatView, prm);
                            }
                        } else {
                            mFloatViewTouchConsumedByMove = false;
                        }
                    } else {
                        mFloatViewTouchConsumedByMove = false;
                    }
//                    moveUp();
                    break;
                default:
                    break;
            }
            return mFloatViewTouchConsumedByMove;
        }
    };
    class JsonThread extends Thread{
        @Override
        public void run() {
            res = formatresult(BaseApp.getMeaning(text.getText().toString().trim()));
        }
    }


    //Note functions
    private void insert(String text){
        if(text.isEmpty()){
            return;
        }
       dbclass.insertIntoNotes(text,getDate());
        nottext.setText("");
        populate();
    }
    private void populate(){
        notearray.clear();
        Cursor cs = dbclass.getNotesData();
        while (cs.moveToNext()){
            notemodel nm = new notemodel(cs.getInt(0),cs.getString(1),cs.getString(2));
            notearray.add(0,nm);
        }
        ma.notifyDataSetChanged();
    }
    public String getDate(){
        SimpleDateFormat tf = new SimpleDateFormat("MM/yy HH:mma");
        String date = tf.format( Calendar.getInstance().getTime());
        return String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))+"/"+date;
    }
    private String formatresult(String text){
        int c = 2;
        while(c<20){
            text = text.replace(Integer.toString(c)+".", "\n"+Integer.toString(c)+".");
            c+=1;
        }
        return text.isEmpty()?"No Result":text;
    }


}