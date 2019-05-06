package com.wobby;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        RetrieveJSONTask.RequestListener{

    String id, title, snippet;
    float wage;

    private GoogleMap mMap;

    // fused services
    // - android location services
    // - google maps
    private GoogleApiClient client;
    private Location lastLocation;
    private LocationRequest request;
    private String mode = "";
    private BackEndManager b;
    private Intent startIntent;
    private ArrayList<Job> jobArrayList;


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

        startIntent = getIntent();
        mode = startIntent.getStringExtra("MODE");
        if(mode.equals("JOB_EDIT") || mode.equals("JOB_CREATE")){
            id = startIntent.getStringExtra("JOB_ID");
            title = startIntent.getStringExtra("JOB_TITLE");
            snippet = startIntent.getStringExtra("JOB_SNIPPET");
            wage = startIntent.getFloatExtra("JOB_WAGE", 0);
        }

        request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000 * 5);

        b = new BackEndManager();
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
        setMyLocation();
        client.connect();

        //LatLng classroom = new LatLng(20.734540, -103.455803);

        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), PinDetailActivity.class);
                intent.putExtra("JOB_TITLE", marker.getTitle());
                intent.putExtra("JOB_SNIPPET", marker.getSnippet());
                for (Job job:jobArrayList) {
                    if(marker.getTitle().equals(job.getJobTitle())){
                        if(marker.getSnippet().equals(job.getJobSnippet())){
                            intent.putExtra("JOB_WAGE", job.getJobWage());
                        }
                    }
                }
                startActivity(intent);
            }
        });

        if(mode.equals("JOB_SEEKER")){
            doRequest();
        }
        if(mode.equals("JOB_EDIT")){
            MarkerOptions newMarker = (new MarkerOptions()
                    .position(new LatLng(startIntent.getDoubleExtra("JOB_LAT", 0), startIntent.getDoubleExtra("JOB_LONG", 0)))
                    .title(title)
                    .snippet(snippet)
                    .alpha(1f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.addMarker(newMarker);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(mode.equals("JOB_EDIT")){
            Job newJob = new Job(id, title, snippet, wage, latLng);
            Log.wtf("Marker JSON", newJob.jobToJSON());
            editJob(newJob);
            Toast.makeText(getApplicationContext(), "Job edited successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(mode.equals("JOB_CREATE")){
            Job newJob = new Job(title, snippet, wage, latLng);
            Log.wtf("Marker JSON", newJob.jobToJSON());
            postJob(newJob);
            Toast.makeText(getApplicationContext(), "Job created successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void postJob(final Job job) {

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {
                return b.Create_Job(job.getJobTitle(), job.getJobSnippet(), job.getJobLatLng().latitude, job.getJobLatLng().longitude, (double)job.getJobWage());
            }
            @Override
            protected void onPostExecute(String r) {
                super.onPostExecute(r);
                Log.wtf("POST RESULT", r);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void editJob(final Job job) {

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {
                return b.Modify_Job(job.getJobId(),job.getJobTitle(), job.getJobSnippet(), job.getJobLatLng().latitude, job.getJobLatLng().longitude, (double)job.getJobWage());
            }
            @Override
            protected void onPostExecute(String r) {
                super.onPostExecute(r);
                Log.wtf("POST RESULT", r);
            }
        }.execute();
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

        if(mode.equals("GET_POSITION")){
            startIntent.putExtra("CurrentLat", lastLocation.getLatitude());
            startIntent.putExtra("CurrentLong",  lastLocation.getLongitude());
            finish();
        }

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
        doRequest();
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.disconnect();
    }

    public void doRequest() {
        RetrieveJSONTask task = new RetrieveJSONTask(this);
        //task.execute("http://10.0.2.2/jobs.json");
        task.execute("https://sheltered-retreat-56384.herokuapp.com/api/Job/");
    }

    @Override
    public void requestDone(JSONArray jsonArray) {
        try {
            jobArrayList = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++){
                String id = jsonArray.getJSONObject(i).getString("_id");
                String title = jsonArray.getJSONObject(i).getString("title");
                String snippet = jsonArray.getJSONObject(i).getString("snippet");
                float wage = (float)jsonArray.getJSONObject(i).getDouble("wage");
                double latitude = jsonArray.getJSONObject(i).getDouble("lat");
                double longitude = jsonArray.getJSONObject(i).getDouble("long");
                Job tempJob = new Job(id, title, snippet, wage, latitude, longitude);
                jobArrayList.add(tempJob);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(mode.equals("JOB_SEEKER")) {
            for (Job job : jobArrayList) {

                MarkerOptions newMarker = (new MarkerOptions()
                        .position(job.getJobLatLng())
                        .title(job.getJobTitle())
                        .snippet(job.getJobSnippet())
                        .alpha(1f)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.addMarker(newMarker);
            }
        }
    }
}