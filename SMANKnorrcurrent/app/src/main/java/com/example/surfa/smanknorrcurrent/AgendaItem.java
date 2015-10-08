package com.example.surfa.smanknorrcurrent;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by surfa on 16-9-2015.
 */
public class AgendaItem {

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public String getCountryISO() {
        return CountryISO;
    }

    public void setCountryISO(String countryISO) {
        CountryISO = countryISO;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public void setLatLng(LatLng latlng)
    {
        this.latLng = latlng;
    }

    public LatLng getLatLng()
    {
        return this.latLng;
    }

    private int ID;
    private String CountryISO;
    private String CountryName;
    private Date Date;
    private LatLng latLng;

}
