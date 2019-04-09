package com.wobby;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


// AsyncTask - a logical flow that runs "concurrently"
// can be triggered / executed and it will run asynchronously
// asynchronously - request something to be done but don't wait for it to finish
public class RetrieveJSONTask extends AsyncTask<String, Void, JSONArray>{

    private RequestListener listener;

    public RetrieveJSONTask(RequestListener listener){
        this.listener = listener;
    }

    @Override
    protected JSONArray doInBackground(String... strings) {

        for(int i = 0; i < strings.length; i++){
            //Log.wtf("STRINGS", strings[i]);
        }

        // the actual request
        // receive data
        // parse data
        JSONArray result = null;

        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            int code = connection.getResponseCode();
            if(code == HttpURLConnection.HTTP_OK){
                // information can be retrieved from a stream
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                StringBuilder builder = new StringBuilder();
                String currentLine = "";

                while((currentLine = br.readLine()) != null){

                    //Log.wtf("HTTP RESPONSE", currentLine);
                    builder.append(currentLine);
                }

                String jsonContent = builder.toString();
                result = new JSONArray(jsonContent);
            }

        } catch (MalformedURLException e) {
            //e.printStackTrace();
            Log.wtf("ERROR", e.toString());
        } catch (IOException e) {
            //e.printStackTrace();
            Log.wtf("ERROR", e.toString());
        } catch (JSONException e) {
            //e.printStackTrace();
            Log.wtf("ERROR", e.toString());
        }


        return result;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        listener.requestDone(jsonArray);
    }

    public interface RequestListener{

        void requestDone(JSONArray jsonArray);
    }
}