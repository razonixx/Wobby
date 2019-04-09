package com.wobby;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    String title, snippet;
    float wage;

    private GoogleMap mMap;

    // fused services
    // - android location services
    // - google maps
    private GoogleApiClient client;
    private Location lastLocation;
    private LocationRequest request;
    private String mode = "";
    private DBHelper db;


    private FusedLocationProviderClient fusedClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(client == null){

            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Intent intent = getIntent();
        mode = intent.getStringExtra("MODE");
        if(mode.equals("JOB_CREATE")){
            title = intent.getStringExtra("JOB_TITLE");
            snippet = intent.getStringExtra("JOB_SNIPPET");
            wage = intent.getFloatExtra("JOB_WAGE", 0);
        }

        request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000 * 5);

        db = new DBHelper(getApplicationContext());

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

        //LatLng classroom = new LatLng(20.734540, -103.455803);
        setMyLocation();

        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), PinDetailActivity.class);
                intent.putExtra("Title", marker.getTitle());
                startActivity(intent);
            }
        });
        if(mode.equals("JOB_SEEKER")){
            refreshPins();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(mode.equals("JOB_PROVIDER")) {
            MarkerOptions newMarker = (new MarkerOptions()
                    .position(latLng)
                    .title("TEMP")
                    .snippet("TEST SNIPPET")
                    .alpha(1f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            mMap.addMarker(newMarker);
            db.add(newMarker);
        }
        if(mode.equals("JOB_CREATE")){
            Job newJob = new Job(title, snippet, wage, latLng);
            Log.wtf("Marker JSON", newJob.jobToJSON());
            Toast.makeText(getApplicationContext(), "Job added succesfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void refreshPins(){
        ArrayList<MarkerOptions> pins = db.getAllPins();
        for (int i = 0; i < pins.size(); i++){
            mMap.addMarker(pins.get(i));
        }
    }

    public void setMyLocation() {

        // enable the location layer on google maps
        // in order to use location layer from google maps we need as an app
        // the location permission
        // -COARSE
        // -FINE

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] p, int[] r){

        if(requestCode == 0 && r[0] == PackageManager.PERMISSION_GRANTED){

            Log.wtf("PERMISSIONS", "GRANTED");

        } else {
            Log.wtf("PERMISSIONS", "DENIED");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);

        if(lastLocation != null){

            Log.wtf("LAST LOCATION", lastLocation.getLatitude() + ", " +
                    lastLocation.getLongitude());
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
        LatLng userPosition = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 18));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(this,"LOCATION: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        //LatLng userPosition = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 18));
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
        Log.wtf("CONNECTION", "START");
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.disconnect();
        Log.wtf("CONNECTION", "STOP");
    }
}


/*
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private DBHelper db;
    private String mode  = ""; //JOB_PROVIDER or JOB_SEEKER
    private GoogleApiClient client;
    private Location lastLocation;
    private LocationRequest request;

    private FusedLocationProviderClient fusedClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        mode = intent.getStringExtra("MODE");

        if(client == null){

            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000 * 5);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        db = new DBHelper(getApplicationContext());
        setMyLocation();
        LatLng userPosition = new LatLng(lastLocation.getLongitude(), lastLocation.getLatitude());
        final CameraPosition TEC = new CameraPosition.Builder().target(userPosition).zoom(15.5f).bearing(0).tilt(25).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(TEC));


        if(mode == "JOB_SEEKER") {
            // Add a marker in Tec and move the camera
            LatLng tec = new LatLng(20.734574, -103.455687);
            mMap.addMarker(new MarkerOptions().position(tec).title("ITESM GDL").snippet("Aqui estudiamos"));

            refreshPins();
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(getApplicationContext(), PinDetailActivity.class);
                    intent.putExtra("Title", marker.getTitle());
                    startActivity(intent);
                }
            });
        }
    }

    private void refreshPins(){
        ArrayList<MarkerOptions> pins = db.getAllPins();
        for (int i = 0; i < pins.size(); i++){
            mMap.addMarker(pins.get(i));
        }
    }

    public void setMyLocation() {

        // enable the location layer on google maps
        // in order to use location layer from google maps we need as an app
        // the location permission
        // -COARSE
        // -FINE

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] p, int[] r){

        if(requestCode == 0 && r[0] == PackageManager.PERMISSION_GRANTED){

            Log.wtf("PERMISSIONS", "GRANTED");

        } else {
            Log.wtf("PERMISSIONS", "DENIED");
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            return;

        lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);

        if(lastLocation != null){

            Log.wtf("LAST LOCATION", lastLocation.getLatitude() + ", " +
                    lastLocation.getLongitude());
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions marker = new MarkerOptions().position(latLng).title("Custom location").snippet("test").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(marker);
        db.add(marker);
        refreshPins();
    }
}
*/