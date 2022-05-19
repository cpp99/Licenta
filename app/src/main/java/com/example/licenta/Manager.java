package com.example.licenta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class Manager {
    public static BitmapDescriptor bitmapFromInt(Context context, int resource)
    {
        Drawable drawable = ContextCompat.getDrawable(context, resource);
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), resource);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 200, 200, false);
        Canvas canvas = new Canvas(smallMarker);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }
}
