package com.safrzone.safrzone.services.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StorageHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "primary.db";
    private static final int DATABASE_VERSION = 1;

    public static final String COLUMN_ID = "_id";
    public static final String TABLE_HISTORY = "history";
    public static final String COLUMN_QUERY = "query";

    private static final String CREATE_TABLE_HISTORY = "create table " + TABLE_HISTORY + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_QUERY + " TEXT"
            + ");";

    static {
        /*cupboard().register(Something.class);*/
    }

    public StorageHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
