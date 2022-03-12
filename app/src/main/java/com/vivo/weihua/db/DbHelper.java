package com.vivo.weihua.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    private static Integer version = 1;

    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DbHelper(Context context, String name) {
        this(context, name, null, version);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "create table location(id integer primary key autoincrement,day varchar(16),time integer,address varchar(64),latitude varchar(16),longitude varchar(16))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
