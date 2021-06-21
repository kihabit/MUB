package com.prayosof.yvideo.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.prayosof.yvideo.model.BookmarkModel;
import com.prayosof.yvideo.view.browser.models.HistoryModel;

import java.util.ArrayList;

public class DBHelperBookmark extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "music_bazaar.db";

    private static final String TABLE_BOOKMARK = "bookmarks";
    private static final String TABLE_HISTORY = "history";
    private static final String KEY_ID = "id";
    private static final String KEY_WEB_NAME = "web_name";
    private static final String KEY_WEB_URL = "web_url";
    private static final String KEY_WEB_ICON = "web_icon";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";

    public DBHelperBookmark(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BOOKMARKS_TABLE = "CREATE TABLE " + TABLE_BOOKMARK + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_WEB_NAME + " TEXT," + KEY_WEB_URL + " TEXT," + KEY_WEB_ICON + " BLOB)";
        db.execSQL(CREATE_BOOKMARKS_TABLE);

        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_WEB_NAME + " TEXT," + KEY_WEB_URL + " TEXT," + KEY_WEB_ICON + " BLOB," + KEY_DATE + " DATE," + KEY_TIME
                + " TIME)";
        db.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public void AddBookmark(BookmarkModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WEB_NAME, model.getName());
        values.put(KEY_WEB_URL, model.getUrl());
        values.put(KEY_WEB_ICON, model.getIcon());

        db.insert(TABLE_BOOKMARK, null, values);
        db.close();
    }

    public void AddHistory(HistoryModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WEB_NAME, model.getName());
        values.put(KEY_WEB_URL, model.getUrl());
        values.put(KEY_WEB_ICON, model.getIcon());
        values.put(KEY_DATE, model.getDate());
        values.put(KEY_TIME, model.getTime());

        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    public ArrayList<BookmarkModel> GetAllBookmarks() {
        ArrayList<BookmarkModel> arrayList = new ArrayList<BookmarkModel>();

        String query = "SELECT * FROM " + TABLE_BOOKMARK;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                BookmarkModel model = new BookmarkModel();
                model.setId(cursor.getString(0));
                model.setName(cursor.getString(1));
                model.setUrl(cursor.getString(2));
                model.setIcon(cursor.getBlob(3));
                arrayList.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return arrayList;
    }

    public ArrayList<HistoryModel> GetAllHistory() {
        ArrayList<HistoryModel> arrayList = new ArrayList<HistoryModel>();

        String query = "SELECT * FROM " + TABLE_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                HistoryModel model = new HistoryModel();
                model.setId(cursor.getString(0));
                model.setName(cursor.getString(1));
                model.setUrl(cursor.getString(2));
                model.setIcon(cursor.getBlob(3));
                model.setDate(cursor.getString(4));
                model.setTime(cursor.getString(5));
                arrayList.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return arrayList;
    }
}