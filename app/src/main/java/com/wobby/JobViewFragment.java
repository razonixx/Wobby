package com.wobby;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobViewFragment extends Fragment implements RetrieveJSONTask.RequestListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{

    private Context context;
    private ListView list;
    private ArrayList<Job> jobArrayList;



    public JobViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_job_view, container, false);

        list = v.findViewById(R.id.listView);
        doRequest(v);

        final SwipeRefreshLayout pullToRefresh = v.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRequest(v);
                pullToRefresh.setRefreshing(false);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void doRequest(View v) {

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
            CustomAdapter customAdapter = new CustomAdapter(jobArrayList, getActivity());
            list.setAdapter(customAdapter);
            list.setOnItemClickListener(this);

        } catch (JSONException e) {
            //e.printStackTrace();
            Log.wtf("ERROR", e.toString());
        }
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
}