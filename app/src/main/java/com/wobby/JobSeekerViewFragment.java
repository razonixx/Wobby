package com.wobby;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class JobSeekerViewFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private static final int MAP_CODE = 1;
    private Context context;
    private ListView list;
    private BackEndManager b;
    private Location lastLocation;
    private GoogleApiClient client = null;
    private ArrayList<Job> jobArrayList;
    private String m_Text = ".0000001";


    public JobSeekerViewFragment() {
        // Required empty public constructor
    }

    public void geoDistance(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter the maximum distance in kilometers");

// Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if(lastLocation != null){
                    Log.wtf("LAST LOCATION", lastLocation.getLatitude() + ", " + lastLocation.getLongitude());
                    displayNearJobs(getNearJobs(lastLocation));
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_job_view, container, false);

        TextView textView = v.findViewById(R.id.textView4);
        textView.setText("Jobs Near You");

        list = v.findViewById(R.id.listView);
        geoDistance();

        final SwipeRefreshLayout pullToRefresh = v.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(client != null && lastLocation != null){
                    client.connect();
                    displayNearJobs(getNearJobs(lastLocation));
                }
                pullToRefresh.setRefreshing(false);
            }
        });

        b = new BackEndManager();
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        Log.wtf("INFO", "STARTING CLIENT");

        client = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);

                        lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);

                        if(lastLocation != null){
                            Log.wtf("LAST LOCATION", lastLocation.getLatitude() + ", " + lastLocation.getLongitude());
                            //JobSeekerViewFragment.this.geoDistance();
                            displayNearJobs(getNearJobs(lastLocation));
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.wtf("NEAR JOBS FAILED", connectionResult.toString());
                    }
                })
                .addApi(LocationServices.API)
                .build();
        client.connect();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void displayNearJobs(JSONArray jsonArray) {
        jobArrayList = new ArrayList<>();
        Log.wtf("DATA", "STARTING REQUEST");
        try {
            for(int i = 0; i < jsonArray.length(); i++){
                String id = jsonArray.getJSONObject(i).getString("_id");
                String title = jsonArray.getJSONObject(i).getString("title");
                String snippet = jsonArray.getJSONObject(i).getString("snippet");
                float wage = (float)jsonArray.getJSONObject(i).getDouble("wage");
                double latitude = jsonArray.getJSONObject(i).getDouble("lat");
                double longitude = jsonArray.getJSONObject(i).getDouble("long");
                Job tempJob = new Job(id, title, snippet, wage, latitude, longitude);
                Log.wtf("DATA", tempJob.jobToJSON());
                jobArrayList.add(tempJob);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            Log.wtf("ERROR", e.toString());
        }
        CustomAdapter customAdapter = new CustomAdapter(jobArrayList, getActivity());
        list.setAdapter(customAdapter);
        list.setOnItemClickListener(this);
        Log.wtf("DATA", "ENDING REQUEST");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(context)
                .setTitle("Share Job")
                .setMessage("Are you sure you want to share this job on Facebook?")
                .setIcon(android.R.drawable.ic_menu_share)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        FacebookSdk.sdkInitialize(context);
                        ShareDialog shareDialog;
                        shareDialog = new ShareDialog(getActivity());
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setQuote(jobArrayList.get(position).getJobTitle())
                                .setContentDescription(jobArrayList.get(position).getJobSnippet())
                                .setContentUrl(Uri.parse("http://www.google.com/maps/place/" + jobArrayList.get(position).getJobLatLng().latitude + "," + jobArrayList.get(position).getJobLatLng().longitude)).build();

                        if(ShareDialog.canShow(ShareLinkContent.class)){
                            shareDialog.show(linkContent);
                        }
                    }
                })
                .setNegativeButton("NO", null).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @SuppressLint("StaticFieldLeak")
    public JSONArray getNearJobs(final Location lastLocation) {
        AsyncTask<String, Void, JSONArray> asyncTask = new AsyncTask<String, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(String... strings) {
                return b.Close_Jobs(Double.toString(lastLocation.getLatitude()), Double.toString(lastLocation.getLongitude()), Double.parseDouble(m_Text)*1000);
            }
        }.execute();

        try {
            return asyncTask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
