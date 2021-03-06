package com.wobby;

import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobEditFragment extends Fragment implements RetrieveJSONTask.RequestListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{

    private Context context;
    private ListView list;
    ArrayList<Job> jobArrayList;
    JSONArray old = null;



    public JobEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_job_edit, container, false);

        list = v.findViewById(R.id.listView);
        jobArrayList = new ArrayList<>();
        doRequest(v);
        final SwipeRefreshLayout pullToRefresh = v.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRequest(getView());
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
        jobArrayList = new ArrayList<>();
        try {
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
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.wtf("JOB", jobArrayList.get(position).jobToJSONWithID());
        Intent intent = new Intent(getContext(), JobEditActivity.class);
        intent.putExtra("JOB_ID", jobArrayList.get(position).getJobId());
        intent.putExtra("JOB_TITLE", jobArrayList.get(position).getJobTitle());
        intent.putExtra("JOB_SNIPPET", jobArrayList.get(position).getJobSnippet());
        intent.putExtra("JOB_WAGE", jobArrayList.get(position).getJobWage());
        intent.putExtra("JOB_LAT", jobArrayList.get(position).getJobLatLng().latitude);
        intent.putExtra("JOB_LONG", jobArrayList.get(position).getJobLatLng().longitude);
        startActivity(intent);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}