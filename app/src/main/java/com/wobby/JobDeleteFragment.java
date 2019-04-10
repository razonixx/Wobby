package com.wobby;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

public class JobDeleteFragment extends Fragment implements RetrieveJSONTask.RequestListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener{

    private Context context;
    private ListView list;
    private ArrayList<Job> jobArrayList;
    private BackEndManager b;


    public JobDeleteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_job_delete, container, false);

        list = v.findViewById(R.id.listView);
        doRequest(v);
        b = new BackEndManager();

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
                long wage = jsonArray.getJSONObject(i).getInt("wage");
                float latitude = (float)jsonArray.getJSONObject(i).getDouble("lat");
                float longitude = (float)jsonArray.getJSONObject(i).getDouble("long");
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
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Job")
                .setMessage("Are you sure you want to delete this job?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Toast.makeText(context, "Job with id " + jobArrayList.get(position).getJobId() + " deleted ", Toast.LENGTH_SHORT).show();
                        deleteJob(jobArrayList.get(position).getJobId());
                    }})
                .setNegativeButton("NO", null).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteJob(final String jobID) {

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {
                return b.Delete_Job(jobID);
            }
            @Override
            protected void onPostExecute(String r) {
                super.onPostExecute(r);
                Log.wtf("POST RESULT", r);
            }
        }.execute();
        doRequest(getView());
    }
}