package com.dcinspirations.on_screenhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseClass extends SQLiteOpenHelper {

    public DatabaseClass(Context context) {
        super(context, "OnScreenNotes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table notes  (id INTEGER PRIMARY KEY AUTOINCREMENT,texts TEXT,time TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS  notes");
        onCreate(db);
    }
    public boolean insertIntoNotes(String text,String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("texts", text);
        cv.put("time", time);
        long result = db.insert("notes",null,cv);
        if(result==-1){
            return true;
        }else{
            return false;
        }
   }
    public Cursor getDur(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select duration from medications where id=?", new String[]{String.valueOf(id)} );
    }
    public Cursor getNotesData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from notes", null );
    }
    public int deleteData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("notes","id=?",new String[]{String.valueOf(id)});
    }



}
