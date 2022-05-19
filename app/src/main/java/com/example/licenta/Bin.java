package com.example.licenta;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

public class Bin {
    private double latitude;
    private double longitude;
    private boolean isFull;
    private int icon;
    private String title;

    public Bin(){
        this.isFull = true;
        this.icon = R.mipmap.bin_foreground;
        this.title = "Bin la dracu";
    }

    public Bin(double latitude, double longitude, boolean isFull, String title) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.isFull = isFull;
        this.icon = R.mipmap.bin_foreground;
        this.title = title;
    }

    public MarkerOptions binToMarker(Bin bin, Context context)
    {
        String isFullSnippet = "Bin is ";
        LatLng coordinates = new LatLng(bin.getLatitude(), bin.getLongitude());

        if(bin.isFull()){
            isFullSnippet += "FULL";
            bin.setIcon(R.mipmap.bin_full_foreground);
        }
        else{
            isFullSnippet += "not FULL";
        }
        BitmapDescriptor bitmapIcon = Manager.bitmapFromInt(context, bin.getIcon());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(coordinates)
                .title(bin.getTitle())
                .snippet(isFullSnippet)
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

    public boolean isFull() {
        return isFull;
    }

    public void setFull(boolean full) {
        isFull = full;
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
