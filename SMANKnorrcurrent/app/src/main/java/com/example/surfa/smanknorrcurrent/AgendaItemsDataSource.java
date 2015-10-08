package com.example.surfa.smanknorrcurrent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by surfa on 16-9-2015.
 */
public class AgendaItemsDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {dbHelper.AGENDA_ID, dbHelper.AGENDA_ISO, dbHelper.AGENDA_NAME, dbHelper.AGENDA_DATE, dbHelper.AGENDA_LAT, dbHelper.AGENDA_LNG};

    public AgendaItemsDataSource(Context context)
    {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public AgendaItem createAgendaItem(Country country, Date date) {
        ContentValues values = new ContentValues();
        values.put(dbHelper.AGENDA_ISO, country.getCountryISO());
        values.put(dbHelper.AGENDA_NAME, country.getCountryName());
        values.put(dbHelper.AGENDA_DATE, date.toString());
        values.put(dbHelper.AGENDA_LAT, country.getCountryLatLng().latitude);
        values.put(dbHelper.AGENDA_LNG, country.getCountryLatLng().longitude);

        long insertId = database.insert(dbHelper.TABLE_AGENDA, null,values);

        Cursor cursor = database.query(dbHelper.TABLE_AGENDA,allColumns, dbHelper.AGENDA_ID + " = " + insertId, null,null, null, null);

        cursor.moveToFirst();

        AgendaItem newAgendaItem = cursorToComment(cursor);

        cursor.close();

        return newAgendaItem;
    }

    private AgendaItem cursorToComment(Cursor cursor) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-DD");
        AgendaItem agendaItem = new AgendaItem();

        try {
            agendaItem.setID(cursor.getInt(0));
            agendaItem.setCountryISO(cursor.getString(1));
            agendaItem.setCountryName(cursor.getString(2));
            agendaItem.setDate(dateFormat.parse(cursor.getString(3)));
            agendaItem.setLatLng(new LatLng(Double.valueOf(cursor.getString(4)),Double.valueOf(cursor.getString(5))));
        }
        catch (Exception ex)
        {
            Log.e("Database", ex.toString());
        }

        return agendaItem;
    }

    public List<AgendaItem> getAllAgendaItems() {
        List<AgendaItem> AgendaItems = new ArrayList<AgendaItem>();

        Cursor cursor = database.query(dbHelper.TABLE_AGENDA,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AgendaItem agendaItem = cursorToComment(cursor);
            AgendaItems.add(agendaItem);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return AgendaItems;
    }

    public List<AgendaItem> getAgendaItemsForCountry(Country country) {
        List<AgendaItem> AgendaItems = new ArrayList<>();

        Cursor cursor = database.query(dbHelper.TABLE_AGENDA,
                allColumns, dbHelper.AGENDA_ISO + " = '" + country.getCountryISO() + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AgendaItem agendaItem = cursorToComment(cursor);
            AgendaItems.add(agendaItem);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return AgendaItems;
    }





}
