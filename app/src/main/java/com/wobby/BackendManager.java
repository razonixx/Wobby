package com.wobby;


import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class BackendManager {
    final static String uri = "https://sheltered-retreat-56384.herokuapp.com";
    final static String auth = "/api/User/";
    private Context context;
    private View v;
    private RequestQueue queue;
    private String username;
    private String password;

    public BackendManager(Context context) {
        this.context = context;
        this.v = v;
        queue = Volley.newRequestQueue(this.context);

    }


    public void getRoot() {

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(BackendManager.this.context, response, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BackendManager.this.context, "Error Connecting to backend", Toast.LENGTH_SHORT).show();
            }
        });

        this.queue.add(stringRequest);
    }

    public void login (String username, String password) {
        this.username = username;
        this.password = password;

        StringRequest postRequest = new StringRequest(Request.Method.POST, uri + auth + "/login",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Toast.makeText(BackendManager.this.context, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(BackendManager.this.context, "NOK", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username", BackendManager.this.username);
                params.put("password", BackendManager.this.password);

                return params;
            }
        };
        this.queue.add(postRequest);
    }

}
