package com.dcinspirations.on_screenhelper;

import android.content.Context;
import android.content.SharedPreferences;

public class Sp {
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor spe;
    private Boolean isLoggedIn = false;

    public Sp(Context ctx) {
        sharedPreferences = ctx.getSharedPreferences("dee", 0);
        spe = sharedPreferences.edit();
    }

    public static void setStatus(boolean state){
        spe.putBoolean("continue",state);
        spe.commit();
    }
    public static boolean getStatus(){
        boolean li = sharedPreferences.getBoolean("continue",false);
        return li;
    }

}
