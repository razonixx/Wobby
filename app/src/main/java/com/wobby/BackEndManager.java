package com.wobby;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class BackEndManager {

    //Use for local testing
    //private String API_URL = "http://10.0.2.2:5000/";

    private String API_URL = "https://sheltered-retreat-56384.herokuapp.com/";

    public BackEndManager() {

    }

    public String Create_Job(String title, String snippet, Double lat, Double lon, Double wage) {

        String response = "";

        String params = "title="+title+"&" +
                "snippet="+snippet+"&" +
                "lat="+lat+"&" +
                "long="+lon+"&" +
                "wage="+wage+"&";


        byte[] postData = params.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;

        String endpoint = API_URL + "api/" + "job";
        try {
            URL url = new URL( endpoint );
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
            conn.setUseCaches(false);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write( postData );

            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String text = "";
            while ((text = br.readLine()) != null) {
                response += text;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", e.toString());
        }

        return response;

    }

    public String Modify_Job(String id, String title, String snippet, Double lat, Double lon, Double wage) {
        String response = "";

        String params = "title="+title+"&" +
                "snippet="+snippet+"&" +
                "lat="+lat+"&" +
                "long="+lon+"&" +
                "wage="+wage+"&";


        byte[] postData = params.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;

        String endpoint = API_URL + "api/" + "Job/" + id;
        Log.e("URL", endpoint);
        try {
            URL url = new URL( endpoint );
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            //Used to override for patch requests
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
            conn.setUseCaches(false);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write( postData );

            int status = conn.getResponseCode();
            Log.e("Status", Integer.toString(status));

            InputStreamReader in;

            if (status >= 400) {
                in = new InputStreamReader(conn.getErrorStream());
            } else {
                in = new InputStreamReader(conn.getInputStream());
            }


            BufferedReader br = new BufferedReader(in);
            String text = "";
            while ((text = br.readLine()) != null) {
                response += text;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FATAL", e.toString());
        }

        return response;

        /*
        JSONObject data = null;

        try {
            data = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            data.getString("");
            //return data.getString("token");
        } catch (JSONException e) {
            //return "NULL";
        }
        */

    }

    public String Delete_Job(String id) {

        String response = "";

        String endpoint = API_URL + "api/" + "Job/" + id;
        try {
            URL url = new URL( endpoint );
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("DELETE");
            conn.setUseCaches(false);
            int status = conn.getResponseCode();
            Log.e("Status", Integer.toString(status));

            InputStreamReader in;

            if (status >= 400) {
                in = new InputStreamReader(conn.getErrorStream());
            } else {
                in = new InputStreamReader(conn.getInputStream());
            }


            BufferedReader br = new BufferedReader(in);
            String text = "";
            while ((text = br.readLine()) != null) {
                response += text;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FATAL", e.toString());
        }
        return response;


    }

    public String Login(String email, String password) {
        String response = "";

        String params = "username="+email+"&" +
                "password="+password;


        byte[] postData = params.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;

        String endpoint = API_URL + "api/" + "User/" + "login/";
        Log.e("URL", endpoint);
        try {
            URL url = new URL( endpoint );
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
            conn.setUseCaches(false);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write( postData );

            int status = conn.getResponseCode();
            Log.e("Status", Integer.toString(status));

            InputStreamReader in;

            if (status >= 400) {
                in = new InputStreamReader(conn.getErrorStream());
            } else {
                in = new InputStreamReader(conn.getInputStream());
            }


            BufferedReader br = new BufferedReader(in);
            String text = "";
            while ((text = br.readLine()) != null) {
                response += text;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FATAL", e.toString());
        }

        return response;
        //return data.getString("token");

    }

    public String GetJobs() {
        String result = "";
        String endpoint = API_URL + "api/" + "Job/";
        try {
            URL url = new URL(endpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            int code = connection.getResponseCode();
            if(code == HttpURLConnection.HTTP_OK){

                // information can be retrieved from a stream
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                StringBuilder builder = new StringBuilder();
                String currentLine = "";

                while((currentLine = br.readLine()) != null){
                    Log.i("HTTP RESPONSE", currentLine);
                    builder.append(currentLine);
                }

                result = builder.toString();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Response", result);

        try {
            JSONArray arr = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String Login() {
        return "";
    }

    public JSONArray Close_Jobs(String lat, String lon, Double distance) {
        ///api/Job/closest?long=10&lat=10.01&distance=10000 200 6.944 ms - 196
        String result = "";
        String endpoint = API_URL + "api/" + "Job/" + "closest?" + "long=" + lon + "&" + "lat=" + lat + "&distance=" + distance;
        Log.e("Closest Endpoint", endpoint);
        try {
            URL url = new URL(endpoint);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            int code = connection.getResponseCode();
            if(code == HttpURLConnection.HTTP_OK){

                // information can be retrieved from a stream
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                StringBuilder builder = new StringBuilder();
                String currentLine = "";

                while((currentLine = br.readLine()) != null){
                    Log.i("HTTP RESPONSE", currentLine);
                    builder.append(currentLine);
                }

                result = builder.toString();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Response", result);

        JSONArray arr = null;
        try {
            JSONObject data = new JSONObject(result);
            arr = data.getJSONArray("jobs");
        } catch (JSONException e) {

        }
        Log.e("R",arr.toString());
        return arr;
    }

    public String Post_Image(String username, String image) {
        String response = "";

        String params = "profile_img="+image;


        byte[] postData = params.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        String endpoint = API_URL + "api/" + "user/" + username;
        Log.e("URL", endpoint);
        try {
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            //Used to override for patch requests
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);

            int status = conn.getResponseCode();
            Log.e("Status", Integer.toString(status));

            InputStreamReader in;

            if (status >= 400) {
                in = new InputStreamReader(conn.getErrorStream());
            } else {
                in = new InputStreamReader(conn.getInputStream());
            }


            BufferedReader br = new BufferedReader(in);
            String text = "";
            while ((text = br.readLine()) != null) {
                response += text;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("FATAL", e.toString());
        }

        return response;
    }
}