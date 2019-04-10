package com.wobby;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class JobSeekerViewFragment extends Fragment implements RetrieveJSONTask.RequestListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{

    private Context context;
    private ListView list;



    public JobSeekerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_job_view, container, false);

        TextView textView = v.findViewById(R.id.textView4);
        textView.setText("Jobs Near You");

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
        Log.wtf("DATA", "STARTING REQUEST");
    }

    @Override
    public void requestDone(JSONArray jsonArray) {
        ArrayList<Job> jobArrayList = new ArrayList<>();
        try {
            for(int i = 0; i < 10; i++){
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(context, "position: " + position + " id: " + id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
