package com.example.surfa.smanknorrcurrent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by surfa on 16-9-2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "knorrcurrent.db";

    public static final String TABLE_AGENDA = "agenda";
    public static final String AGENDA_ID = "ID";
    public static final String AGENDA_ISO = "CountryISO";
    public static final String AGENDA_NAME = "CountryName";
    public static final String AGENDA_DATE = "Date";
    public static final String AGENDA_LAT = "Latitude";
    public static final String AGENDA_LNG = "Longitude";


    public static final String TABLE_DISH = "dish";
    public static final String DISH_ID = "ID";
    public static final String DISH_AGENDAID = "AgendaID";
    public static final String DISH_PHOTOPATH = "PhotoPath";
    public static final String DISH_TITLE = "Title";
    public static final String DISH_RATING = "Rating";

    public static final String DATABASE_CREATE = "create table " + TABLE_AGENDA + "(" + AGENDA_ID + " integer primary key autoincrement, " + AGENDA_ISO + " string not null, " + AGENDA_NAME + " string not null, " + AGENDA_DATE + " string not null, " + AGENDA_LAT + " string not null, " + AGENDA_LNG + " string not null);"
            + "create table" + TABLE_DISH + "(" + DISH_ID + " integer primary key autoincrement, FOREIGN KEY(" + DISH_AGENDAID + ") REFERENCES " + TABLE_AGENDA + "(" + AGENDA_ID + "), " + DISH_PHOTOPATH + " string, " + DISH_TITLE + " string not null, " + DISH_RATING + "real not null);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AGENDA);
        onCreate(db);
    }
}
