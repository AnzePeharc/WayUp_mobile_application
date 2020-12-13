package com.example.wayup_mobile_application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Problems.db";
    public static final String TABLE_NAME = "problems_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "SEQUENCE";
    public static final String COL_3 = "NAME";
    public static final String COL_4 = "GRADE";
    public static final String COL_5 = "SETTER";
    public static final String COL_6 = "COMMENT";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,SEQUENCE TEXT, NAME TEXT,GRADE TEXT,SETTER TEXT,COMMENT TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String sequence, String name,String grade,String setter, String comment ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,sequence);
        contentValues.put(COL_3,name);
        contentValues.put(COL_4,grade);
        contentValues.put(COL_5,setter);
        contentValues.put(COL_6,comment);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }

    public boolean updateData(String id,String sequence,String name,String grade,String setter, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,sequence);
        contentValues.put(COL_3,name);
        contentValues.put(COL_4,grade);
        contentValues.put(COL_5,setter);
        contentValues.put(COL_6,comment);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }
}

