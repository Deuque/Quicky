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
import android.graphics.PixelFormat;
import android.hardware.input.InputManager;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    private Activity mActivity;
    private WindowManager mWindowManager;
    private View mFloatView;
    private WindowManager.LayoutParams mFloatViewLayoutParams;
    private boolean mFloatViewTouchConsumedByMove = false;
    private int mFloatViewLastX;
    private int mFloatViewLastY;
    private int mFloatViewFirstX;
    private int mFloatViewFirstY;
    private boolean isShown = false;
    private ImageView img,cancel;
    private LinearLayout viewPager;
    private LinearLayout views,empty;
    private AutoCompleteTextView text;
    private ImageButton search,speak;
    private String dictionary;
    private TextView result;
    private ArrayList<String> words;
    private RelativeLayout dl,nl;
    private ImageButton add;
    private AutoCompleteTextView nottext;
    private DatabaseClass dbclass;
    List<notemodel> notearray;
    RecyclerView rv;
    notes_adapter ma;
    ArrayAdapter<String> adapter;
    Handler h = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            if(words.size()>0){
                String[] str = new String[words.size()];
                adapter = new ArrayAdapter<String>(mActivity.getApplicationContext(), android.R.layout.simple_list_item_1, words.toArray(str));
                text.setAdapter(adapter);
                text.setDropDownBackgroundResource(R.color.aux1);
                text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        empty.setVisibility(View.GONE);
                        String name = text.getText().toString();
                        result.setText(formatresult(process2(name.trim())));
                    }
                });

                h.removeCallbacks(r);
            }else {
                h.postDelayed(r, 1500);
            }
        }
    };
    JSONObject job = null;
    private QuickAction quickAction;
    private QuickAction quickIntent;
    ActionItem copyItem = new ActionItem(1, "copy");
    ActionItem pasteItem = new ActionItem(2, "paste");
    WordSpeak wordSpeak;


    @SuppressLint("InflateParams")
    public FloatViewManager(final Activity activity) {
        mActivity = activity;
        wordSpeak = new WordSpeak(mActivity);
        dbclass = new DatabaseClass(mActivity);
        notearray = new ArrayList<>();
        words = new ArrayList<>();
        Jsonthread jt = new Jsonthread();
        jt.start();
        LayoutInflater inflater = LayoutInflater.from(activity);
        mFloatView = inflater.inflate(R.layout.onscreen, null);

        empty =mFloatView.findViewById(R.id.empty);
        dl = mFloatView.findViewById(R.id.diclayout);
        nl = mFloatView.findViewById(R.id.notlayout);
        img = mFloatView.findViewById(R.id.icon);
        cancel = mFloatView.findViewById(R.id.cancel);
        views = mFloatView.findViewById(R.id.views);
        text = mFloatView.findViewById(R.id.text);
        result = mFloatView.findViewById(R.id.result);
        result.setMovementMethod(new ScrollingMovementMethod());
        search=mFloatView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                empty.setVisibility(View.GONE);
                if(!dictionary.isEmpty()&&!text.getText().toString().isEmpty()){

                    result.setText(formatresult(process2(text.getText().toString().trim())));
                }else{
                    Toast.makeText(activity, "Enter a word", Toast.LENGTH_SHORT).show();
                }
            }
        });
        speak = mFloatView.findViewById(R.id.speak);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordSpeak.speak(text.getText().toString());
            }
        });
        nottext = mFloatView.findViewById(R.id.notetext);
        add = mFloatView.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert(nottext.getText().toString().trim());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissFloatView();
            }
        });
        text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                moveUp();
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(text,InputMethodManager.SHOW_IMPLICIT);
                text.requestFocus();
                moveUp();
                return false;
            }
        });
        text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu pm = new PopupMenu(v.getContext(),v, Gravity.END);
                pm.getMenuInflater().inflate(R.menu.clips,pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        return false;
                    }
                });
                pm.show();
                return false;
            }
        });
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cancel.setVisibility(View.VISIBLE);
                return false;
            }
        });

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
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWindowManager != null) {
                        mWindowManager.removeViewImmediate(mFloatView);
                    }
                }
            });
        }
    }
    public void showFloatView() {
        if (!mIsFloatViewShowing) {
            mIsFloatViewShowing = true;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mActivity.isFinishing()) {
                        mWindowManager = (WindowManager) mActivity.getSystemService(WINDOW_SERVICE);
                        if (mWindowManager != null) {
                            mWindowManager.addView(mFloatView, mFloatViewLayoutParams);
                        }
                    }
                }
            });
        }
    }
    public void moveUp(){
        WindowManager.LayoutParams prm = mFloatViewLayoutParams;

                prm.y += -20;
                mFloatViewTouchConsumedByMove = true;
                if (mWindowManager != null) {
                    mWindowManager.updateViewLayout(mFloatView, prm);
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
                            ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(CLIPBOARD_SERVICE);
                            ClipData cdata = clipboard.getPrimaryClip();
                            ClipData.Item citem = cdata.getItemAt(0);
                            act.setText(citem.getText().toString());
                            act.setSelection(act.getText().toString().length());
                            return true;
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
                        if(cancel.getVisibility()==View.VISIBLE){
                            cancel.setVisibility(View.GONE);
                        }else {
                            showOrHide();
                        }
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
    private String process2(final String a){
        try {
            if(job==null){
               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       process2(a);
                   }
               },3000);
                return "";

            }

            return job.getString(a.toLowerCase());

        } catch (JSONException e) {
            e.printStackTrace();
        }
       return "";
    }
    public class Jsonthread extends Thread{

        public void run(){
            Looper.prepare();
            try {
                readFile();
                job = new JSONObject(dictionary.substring(1,dictionary.length()));
                getWords(job);
                h.post(r);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private String readFile() {
        InputStream is = null;
        try {
            is = mActivity.getAssets().open("dictionary2.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ( (line = reader.readLine()) != null) {
                // do something with the line here
                sb.append(line);
            }

            if (reader != null) {
                reader.close();
            }


            dictionary = sb.toString();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    private void getWords(JSONObject job2){
        JSONArray jarrkeys = job2.names();
        for(int i=0;i<jarrkeys.length();i++){

            try {
                words.add(jarrkeys.getString (i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
            notearray.add(nm);
        }
        ma.notifyDataSetChanged();
    }
    private void deletenote(int id){
        dbclass.deleteData(id);
        populate();
    }
    public String getDate(){
        SimpleDateFormat tf = new SimpleDateFormat("MM/yy HH:mma");
        String date = tf.format( Calendar.getInstance().getTime());
        return String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH))+"/"+date;
    }
    private String formatresult(String text){
        int c = 2;
        while(c<11){
            text = text.replace(Integer.toString(c)+".", "\n"+Integer.toString(c)+".");
            c+=1;
        }
        return text.isEmpty()?"No Result":text;
    }


}