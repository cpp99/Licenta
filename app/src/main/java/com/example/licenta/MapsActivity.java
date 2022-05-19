package com.example.licenta;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.example.licenta.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.SphericalUtil;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private GeoApiContext mGeoApiContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        try{
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            Log.e(TAG, "ERROR!!!" + e.getMessage());
        }

        if(mGeoApiContext == null)
        {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.maps_api_key))
                    .build();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DatabaseReference databaseRef;

        databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    mMap.clear();
                    readSnapshot(snapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void calculateDirections(Car car, Bin bin)
    {
        Log.d(TAG, "calculate directions: calculating");
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(car.getLatitude(), car.getLongitude());
        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(bin.getLatitude(), bin.getLongitude());
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
        directions.alternatives(true);
        directions.origin(origin);
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "Failed to get directions" + e.getMessage());
            }
        });
    }

    public void addPolylinesToMap(final DirectionsResult result)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //Log.d(TAG, "result routes: " + result.routes.length);
                for(DirectionsRoute route: result.routes)
                {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();
                    for(com.google.maps.model.LatLng latLng : decodedPath)
                    {
                        newDecodedPath.add(new LatLng(latLng.lat, latLng.lng));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setClickable(true);
                    break;
                }
            }
        });
    }

    public void readSnapshot(DataSnapshot snapshot)
    {
        Bin bin = new Bin();
        String stringLatBin = "", stringLngBin = "", stringIsFull = "";
        if (snapshot.exists()){
            stringLatBin = snapshot.child("Bin").child("Latitude").getValue().toString();
            stringLngBin = snapshot.child("Bin").child("Longitude").getValue().toString();
            stringIsFull = snapshot.child("Bin").child("isFull").getValue().toString();
        }
        if (stringLatBin.equals("") || stringLngBin.equals("") || stringIsFull.equals("")){
            Log.e(TAG, "Error reading from database!");
        }
        else {
            bin.setLatitude(Double.parseDouble(stringLatBin));
            bin.setLongitude(Double.parseDouble(stringLngBin));
            bin.setFull(Boolean.parseBoolean(stringIsFull));
        }

        String stringLatCar = "", stringLngCar = "", stringTitleCar = "";
        ArrayList<Car> garbageTrucks = new ArrayList<>();
        DataSnapshot carSnapshot = snapshot.child("Cars");
        for(DataSnapshot ds : carSnapshot.getChildren()){
            Car car = new Car();
            if(ds.exists()){
                stringLatCar = ds.child("Latitude").getValue().toString();
                stringLngCar = ds.child("Longitude").getValue().toString();
                stringTitleCar = ds.child("Title").getValue().toString();
            }
            if (stringLatCar.equals("") || stringLngCar.equals("") || stringTitleCar.equals("")){
                Log.e(TAG, "Error reading from database!");
            }
            else {
                car.setTitle(stringTitleCar);
                car.setLatitude(Double.parseDouble(stringLatCar));
                car.setLongitude(Double.parseDouble(stringLngCar));
                garbageTrucks.add(car);
            }
        }

        createBinAndCars(bin, garbageTrucks);
    }

    public void createBinAndCars(Bin bin, ArrayList<Car> garbageTrucks)
    {
        Car closestCar = calculateClosestCar(bin, garbageTrucks);
        for(Car car: garbageTrucks) {
            mMap.addMarker(car.carToMarker(car, getApplicationContext()));
        }
        mMap.addMarker(bin.binToMarker(bin, getApplicationContext()));

        if(bin.isFull()) {
            calculateDirections(closestCar, bin);
            Toast.makeText(this.getApplicationContext(), closestCar.getTitle() + " is on the way!", Toast.LENGTH_LONG).show();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(bin.getLatitude(), bin.getLongitude())));
        mMap.setMinZoomPreference(13.0f);
    }

    public Car calculateClosestCar(Bin bin, ArrayList<Car> garbageTrucks)
    {
        double val;
        double min = 99999;
        Car closest = garbageTrucks.get(0);
        for(Car car: garbageTrucks)
        {
            val = SphericalUtil.computeDistanceBetween(new LatLng(bin.getLatitude(), bin.getLongitude()),
                    new LatLng(car.getLatitude(), car.getLongitude()));
            if(val < min){
                min = val;
                closest = car;
            }
        }
        return closest;
    }
}