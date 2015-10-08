package com.example.surfa.smanknorrcurrent;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by surfa on 12-9-2015.
 */
public class Country {

    private String countryName;
    private String countryISO;
    private Bitmap countryFlag;
    private String countryCapital;
    private String countryContintent;
    private Double countryPopulation;

    public LatLng getCountryLatLng() {
        return countryLatLng;
    }

    public void setCountryLatLng(LatLng countryLatLng) {
        this.countryLatLng = countryLatLng;
    }

    private LatLng countryLatLng;

    public Country(String CountryName, String CountryISO)
    {
        this.countryName = CountryName;
        this.countryISO = CountryISO.toLowerCase();
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryISO() {
        return countryISO;
    }

    public void setCountryISO(String countryISO) {
        this.countryISO = countryISO;
    }

    public Bitmap getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(Bitmap countryFlag) {
        this.countryFlag = countryFlag;
    }

    public String getCountryCapital() {
        return countryCapital;
    }

    public void setCountryCapital(String countryCapital) {
        this.countryCapital = countryCapital;
    }

    public String getCountryContintent() {
        return countryContintent;
    }

    public void setCountryContintent(String countryContintent) {
        this.countryContintent = countryContintent;
    }

    public Double getCountryPopulation() {
        return countryPopulation;
    }

    public void setCountryPopulation(Double countryPopulation) {
        this.countryPopulation = countryPopulation;
    }
}
