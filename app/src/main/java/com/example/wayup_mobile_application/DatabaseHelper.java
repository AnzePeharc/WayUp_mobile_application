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
    public static final String COL_3 = "SEQUENCE_TAGS";
    public static final String COL_4 = "SEQUENCE_COUNTERS";
    public static final String COL_5 = "NAME";
    public static final String COL_6 = "GRADE";
    public static final String COL_7 = "SETTER";
    public static final String COL_8 = "COMMENT";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,SEQUENCE TEXT, SEQUENCE_TAGS TEXT, SEQUENCE_COUNTERS TEXT, NAME TEXT,GRADE TEXT,SETTER TEXT,COMMENT TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String sequence, String sequence_tags,String sequence_counters, String name, String grade, String setter, String comment ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,sequence);
        contentValues.put(COL_3,sequence_tags);
        contentValues.put(COL_4,sequence_counters);
        contentValues.put(COL_5,name);
        contentValues.put(COL_6,grade);
        contentValues.put(COL_7,setter);
        contentValues.put(COL_8,comment);
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

    public Cursor getAllDataAsc() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" order by "+COL_6+" asc ",null);
        return res;
    }


    public Cursor getAllDataDesc() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" order by "+COL_6+" desc ",null);
        return res;
    }
    public boolean updateData(String id,String sequence, String sequence_tags,String sequence_counters,String name,String grade,String setter, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,sequence);
        contentValues.put(COL_3,sequence_tags);
        contentValues.put(COL_4,sequence_counters);
        contentValues.put(COL_5,name);
        contentValues.put(COL_6,grade);
        contentValues.put(COL_7,setter);
        contentValues.put(COL_8,comment);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }
}

