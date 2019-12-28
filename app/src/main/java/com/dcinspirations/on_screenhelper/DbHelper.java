package com.dcinspirations.on_screenhelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emrekose.copyexternaldb.CopyDatabase;

import java.util.ArrayList;

public class DbHelper extends CopyDatabase {

    String Table_Name = "Dictionary";
    String Column_Name = "English";
    Context mcontext;


    public DbHelper(Context context, int version, String databaseName) {
        super(context, version, databaseName);
        mcontext = context;

    }

    public ArrayList<String> GetAllWords(String query){

        ArrayList<String> arrayList = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(
                Table_Name,
                new String[] {Column_Name},
                Column_Name + " LIKE ?",
                new String[] {query + "%"},
                null,null,null);

        int index = cursor.getColumnIndex(Column_Name);

        while(cursor.moveToNext()){
            arrayList.add(cursor.getString(index));

        }

        return arrayList;

    }

    public String GetMean(String word){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        String mean = null;

        Cursor cursor = sqLiteDatabase.rawQuery("Select * from " + Table_Name + " where " + Column_Name + "=  '"+word+"'",null);

        while(cursor.moveToNext()){
            mean = cursor.getString(cursor.getColumnIndex("Hindi"));

        }
        return mean;
    }
}
