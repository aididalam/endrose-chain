package com.aididalam.endorsevalidator.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "local.db";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table user " +
                        "(id integer primary key, " +
                        "user_id text," +
                        "access_token text," +
                        "token_type text," +
                        "description text," +
                        "email text," +
                        "type integer," +
                        "publickey text," +
                        "privatekey text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    public boolean insertUser(int user_id, String access_token, String token_type, String description, String email, int type, String publickey, String privatekey) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "select * from user where id=1", null );
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", user_id);
        contentValues.put("access_token", access_token);
        contentValues.put("token_type", token_type);
        contentValues.put("description", description);
        contentValues.put("email", email);
        contentValues.put("type", type);
        contentValues.put("publickey", publickey);
        contentValues.put("privatekey", privatekey);
        if(res.getCount()==0){
            db.insert("user", null, contentValues);
        }else{
            db.update("user", contentValues, "id = ? ", new String[] { Integer.toString(1) } );
        }
        return true;
    }

    public boolean deleteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("user", "id=?", new String[]{"1"});
        return true;
    }

    @SuppressLint("Range")
    public HashMap getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from user where id="+id+"", null );
        if(res.getCount()>0){
            res.moveToFirst();
            HashMap hashMap=new HashMap();
            hashMap.put("user_id",res.getString(res.getColumnIndex("user_id")));
            hashMap.put("access_token",res.getString(res.getColumnIndex("access_token")));
            hashMap.put("token_type",res.getString(res.getColumnIndex("token_type")));
            hashMap.put("description",res.getString(res.getColumnIndex("description")));
            hashMap.put("email",res.getString(res.getColumnIndex("email")));
            hashMap.put("type",res.getString(res.getColumnIndex("type")));
            hashMap.put("publickey",res.getString(res.getColumnIndex("publickey")));
            hashMap.put("privatekey",res.getString(res.getColumnIndex("privatekey")));

            return hashMap;
        }else{
            return null;
        }
    }

}
