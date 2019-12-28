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
        db.execSQL("create table dictionary  (id INTEGER PRIMARY KEY AUTOINCREMENT,word TEXT,meaning TEXT)");
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
    public boolean insertIntoDic(String word,String meaning){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("word", word);
        cv.put("meaning", meaning);
        long result = db.insert("dictionary",null,cv);
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
    public Cursor getDicWords(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select word from dictionary", null );
    }
    public Cursor getMeaning(String word){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select meaning from dictionary where word=?", new String[]{word} );
    }
    public Cursor getDicData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("Select * from dictionary", null );
    }
    public int deleteData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("notes","id=?",new String[]{String.valueOf(id)});
    }



}
