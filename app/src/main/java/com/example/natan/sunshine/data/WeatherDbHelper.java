package com.example.natan.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.natan.sunshine.data.WeatherContract.WeatherEntry;

public class WeatherDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "sunshine.db";
    public static final int DATABASE_VERSION = 1;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                    WeatherEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                    WeatherEntry.COLUMN_DATE       + " INTEGER, "                 +

                    WeatherEntry.COLUMN_WEATHER_ID + " INTEGER, "                 +

                    WeatherEntry.COLUMN_MIN_TEMP   + " REAL, "                    +
                    WeatherEntry.COLUMN_MAX_TEMP   + " REAL, "                    +

                    WeatherEntry.COLUMN_HUMIDITY   + " REAL, "                    +
                    WeatherEntry.COLUMN_PRESSURE   + " REAL, "                    +

                    WeatherEntry.COLUMN_WIND_SPEED + " REAL, "                    +
                    WeatherEntry.COLUMN_DEGREES    + " REAL" + ");";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}
