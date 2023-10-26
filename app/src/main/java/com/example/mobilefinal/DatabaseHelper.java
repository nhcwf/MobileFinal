package com.example.mobilefinal;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MobileFinal.db";
    public static final int DATABASE_VERSION = 1;
    public static final String USERS_TABLE_NAME = "USERS";
    public static final String HIGHSCORES_TABLE_NAME = "HIGHSCORES";

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USERS_TABLE_NAME + "(USERNAME TEXT PRIMARY KEY, PASSWORD TEXT)");
        db.execSQL("CREATE TABLE " + HIGHSCORES_TABLE_NAME + "(ID INT PRIMARY KEY, SCORE INT, PLAYTIME LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HIGHSCORES_TABLE_NAME);

        onCreate(db);
    }

    public boolean insertUser(String username, String password) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("username", username);
        contentValues.put("password", password);

        long result = database.insert(USERS_TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public boolean isValidUsername(String username) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + USERS_TABLE_NAME + " WHERE USERNAME = ?", new String[] { username });

        return cursor.getCount() == 0;
    }

    public boolean isValidLogin(String username, String password) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + USERS_TABLE_NAME + " WHERE USERNAME = ? AND PASSWORD = ?", new String[] { username, password });

        return cursor.getCount() != 0;
    }

    public boolean insertGameSession(int id, int score, long playtimeMilliseconds) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id", id);
        contentValues.put("score", score);
        contentValues.put("playtime", playtimeMilliseconds);

        long result = database.insert(HIGHSCORES_TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public int getScoresCount() {
        SQLiteDatabase database = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(String.format("SELECT * FROM %s", HIGHSCORES_TABLE_NAME), null);

        return cursor.getCount();
    }

    @SuppressLint("Range")
    public void setGameSession(GameSession gameSession, int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(String.format("SELECT * FROM %s WHERE ID = ?", HIGHSCORES_TABLE_NAME), new String[] { String.valueOf(id) });

        if (cursor.moveToFirst()) {
            int score = cursor.getInt(cursor.getColumnIndex("SCORE"));
            long playtimeMillisecond = cursor.getInt(cursor.getColumnIndex("PLAYTIME"));

            gameSession.setId(id);
            gameSession.setScore(score);
            gameSession.setPlaytime(playtimeMillisecond);
        }
    }
}
