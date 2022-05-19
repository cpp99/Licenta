package com.example.licenta;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Car {
    private double latitude;
    private double longitude;
    private int icon;
    private String title;

    public Car() {
        this.icon = R.mipmap.garbage_truck_foreground;
    }

    public Car(double latitude, double longitude, String title) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.icon = R.mipmap.garbage_truck_foreground;
        this.title = title;
    }

    public MarkerOptions carToMarker(Car car, Context context)
    {
        String isFullSnippet = "Bin is ";
        LatLng coordinates = new LatLng(car.getLatitude(), car.getLongitude());

        BitmapDescriptor bitmapIcon = Manager.bitmapFromInt(context, car.getIcon());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(coordinates)
                .title(car.getTitle())
                .icon(bitmapIcon)
                .anchor(0.5f,0.5f);

        return markerOptions;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
