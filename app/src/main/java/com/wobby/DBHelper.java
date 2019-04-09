package com.wobby;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_FILE = "Wobby.db";
    private static final String TABLE = "Pins";
    private static final String FIELD_ID = "id";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_SNIPPET = "snippet";
    private static final String FIELD_LAT = "latitude";
    private static final String FIELD_LON = "longitude";

    public DBHelper(Context context){
        super(context, DB_FILE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE + "(" +
                FIELD_ID + " INTEGER PRIMARY KEY, " +
                FIELD_TITLE +  " TEXT, " +
                FIELD_SNIPPET + " TEXT," +
                FIELD_LAT + " REAL," +
                FIELD_LON +  " REAL) ";
                db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // prepared statements
        String query = "DROP TABLE IF EXISTS ?";
        String[] params = {TABLE};
        db.execSQL(query, params);

        onCreate(db);
    }

    public void add(String title, String snippet, double lat, double lon){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        // autoboxing - interchangeability between primitive and corresponding class
        values.put(FIELD_TITLE, title);
        values.put(FIELD_SNIPPET, snippet);
        values.put(FIELD_LAT, lat);
        values.put(FIELD_LON, lon);
        db.insert(TABLE, null, values);
    }

    public void add(MarkerOptions marker){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        // autoboxing - interchangeability between primitive and corresponding class
        values.put(FIELD_TITLE, marker.getTitle());
        values.put(FIELD_SNIPPET, marker.getSnippet());
        values.put(FIELD_LAT, marker.getPosition().latitude);
        values.put(FIELD_LON, marker.getPosition().longitude);
        db.insert(TABLE, null, values);
    }

    public int delete(double lat){
        SQLiteDatabase db = getWritableDatabase();
        // clause - clausula - condition for this query to happen
        String clause = FIELD_LAT + " = ?";
        String[] args = {lat + ""};

        return db.delete(TABLE, clause, args);
    }

    public MarkerOptions find(String title){
        SQLiteDatabase db = getReadableDatabase();
        String clause = FIELD_TITLE + " = ?";
        String[] args = {title};

        Cursor c = db.query(TABLE, null, clause, args, null, null, null);

        String titleDB = "", snippet = "";

        double lat = 0,
               lon = 0;

        if(c.moveToFirst()){
            titleDB = c.getString(1);
            snippet = c.getString(2);
            lat = c.getDouble(3);
            lon = c.getDouble(4);
        }

        LatLng coords = new LatLng(lat, lon);

        MarkerOptions returnMarker = new MarkerOptions().position(coords).title(titleDB).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        Log.wtf("Pin", "Title: " + titleDB + " Snippet: " + snippet + " X: " + lat + " Y: " + lon);
        return returnMarker;
    }

    public MarkerOptions find(int id){
        SQLiteDatabase db = getReadableDatabase();
        String clause = FIELD_ID + " = ?";
        String[] args = {Integer.toString(id)};

        Cursor c = db.query(TABLE, null, clause, args, null, null, null);

        String title = "", snippet = "";

        double lat = 0,
                lon = 0;

        if(c.moveToFirst()){
            title = c.getString(1);
            snippet = c.getString(2);
            lat = c.getDouble(3);
            lon = c.getDouble(4);
        }

        LatLng coords = new LatLng(lat, lon);

        MarkerOptions returnMarker = new MarkerOptions().position(coords).title(title).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        Log.wtf("Pin", "Title: " + title + " Snippet: " + snippet + " X: " + lat + " Y: " + lon);
        return returnMarker;
    }

    public ArrayList<MarkerOptions> getAllPins(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from Pins",null);

        ArrayList<MarkerOptions> pins = new ArrayList<>();

        while (cursor.moveToNext()) {
            pins.add(find(cursor.getInt(0)));
        }
        return pins;
    }
}
