package com.my51c.see51.app.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class XTsateenOpenHelper extends SQLiteOpenHelper {
    public final static String TABLE_ID = "_id";
    private final static String TABLE_NAME = "sateenable";
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "SATEENABLE.db";
    private final static String CREAT_TABLE_SATEENABLE = "create table "
            + TABLE_NAME + "(" + TABLE_ID + " integer primary key autoincrement,"
            + "satenum varchar(1),satelon char(1),mode char(1),freq char(1),"
            + "bw char(1),type char(1),modem char(1))";

    public XTsateenOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_TABLE_SATEENABLE);
    }

    @SuppressLint("NewApi")
    public Cursor select(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        //Cursor cursor=db.rawQuery("select * from person where _id=?", new String[]{id.toString()});//Cursor 游标和 ResultSet 很像
        //Cursor cursor = db.rawQuery("select * from "+TABLE_NAME, null, null);
        return cursor;
    }

    // 增加操作
    public long insert(String satenum, String satelon, String mode,
                       String freq, String bw, String type, String modem, String rssi, String recvpol, String sendpol) {
        SQLiteDatabase db = this.getWritableDatabase();
        /* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put("satenum", satenum);
        cv.put("satelon", satelon);
        cv.put("mode", mode);
        cv.put("freq", freq);
        cv.put("bw", bw);
        cv.put("type", type);
        cv.put("modem", modem);
        cv.put("rssi", rssi);
        cv.put("recvpol", recvpol);
        cv.put("sendpol", sendpol);
        long row = db.insert(TABLE_NAME, null, cv);
        return row;
    }

    // 删除操作
    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "_id" + " = ?";
        String[] whereValue = {Integer.toString(id)};
        db.delete(TABLE_NAME, where, whereValue);
    }

    // 修改操作
    public void update(int id, String satenum, String satelon, String mode,
                       String freq, String bw, String type, String modem, String rssi, String recvpol, String sendpol) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "_id" + " = ?";
        String[] whereValue = {Integer.toString(id)};
        ContentValues cv = new ContentValues();
        cv.put("satenum", satenum);
        cv.put("satelon", satelon);
        cv.put("mode", mode);
        cv.put("freq", freq);
        cv.put("bw", bw);
        cv.put("type", type);
        cv.put("modem", modem);
        cv.put("rssi", rssi);
        cv.put("recvpol", recvpol);
        cv.put("sendpol", sendpol);
        db.update(TABLE_NAME, cv, where, whereValue);
    }

    // 数据库版本更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists person");
            onCreate(db);
        }
    }
}
