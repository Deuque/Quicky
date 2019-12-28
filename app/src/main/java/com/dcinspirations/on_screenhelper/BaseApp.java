package com.dcinspirations.on_screenhelper;

import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class BaseApp extends Application {
    public static Context ctx;
    static String dictionary;
    static String[] words;
    static JsonObject job;
    private static JsonReader reader1;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();
        readFile();
    }

    private static String readFile() {
        InputStream is = null;
        try {
            is = ctx.getAssets().open("dictionary2.txt");
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

    public static String getMeaning(String word){
        JsonReader reader1;
        try {
            reader1 = new JsonReader(new StringReader(dictionary.substring(1)));
            reader1.beginObject();

            while (reader1.hasNext()) {

                String name = reader1.nextName();

                if (name.equalsIgnoreCase(word)) {

                    return reader1.nextString();

                } else {
                    reader1.skipValue(); //avoid some unhandle events
                }
            }

            reader1.endObject();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No result";
    }


}
