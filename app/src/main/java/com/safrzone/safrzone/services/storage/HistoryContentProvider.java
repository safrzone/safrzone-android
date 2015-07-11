package com.safrzone.safrzone.services.storage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.safrzone.safrzone.SafrZoneApp;

public class HistoryContentProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.safrzone.safrzone.history";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/items" );

    StorageHelper _databaseHelper;
    SQLiteDatabase _database;

    @Override
    public boolean onCreate() {
        _databaseHelper = new StorageHelper(getContext());
        _database = _databaseHelper.getWritableDatabase();

        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = _database.query(
                StorageHelper.TABLE_HISTORY,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    @Override public Uri insert(Uri uri, ContentValues values) {
        long rowID = _database.insert(
                StorageHelper.TABLE_HISTORY,
                null,
                values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to insert into " + uri);
    }

    @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    public static void insertQuery(String query) {
        ContentValues values = new ContentValues();
        values.put(StorageHelper.COLUMN_QUERY, query);
        SafrZoneApp.getContext().getContentResolver().insert(CONTENT_URI, values);
    }
}
