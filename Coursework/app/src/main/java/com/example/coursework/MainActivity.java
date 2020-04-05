package com.example.coursework;
//import libraries
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;


public class MainActivity extends AppCompatActivity{
    //Declaring map variables
    private MapView mapView;
    private MapboxMap map;
    private MarkerView markerView;
    private MarkerViewManager markerViewManager;
    //Handler and Ruunable variables are used for pausing code execution
    final Handler handler = new Handler();
    final Runnable r = new Runnable() {@Override public void run() {markerViewManager.removeMarker(markerView); }};
    //requiring permissions
    String[] requiredPermissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    //declaring variables used for geolocation and storing JSON data
    boolean validate = true;
    double lat;
    double lng;
     ArrayList<String> nameList = new ArrayList<>();
     ArrayList<String> latList = new ArrayList<>();
     ArrayList<String> lngList = new ArrayList<>();
     ArrayList<String> ratingList = new ArrayList<>();
     ArrayList<String> distanceList = new ArrayList<>();
     ArrayList<String> postcodeList = new ArrayList<>();
    ArrayList<String> addressList = new ArrayList<>();
    ArrayList<String> addressList2 = new ArrayList<>();
    ArrayList<String> addressList3 = new ArrayList<>();
    // intent variable for on location change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //accessing mapbox api
        Mapbox.getInstance(this, getString(R.string.mapbox_token));
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //check if required permissions are granted
        for (int i = 0; i < requiredPermissions.length; i++) {
            int result = ActivityCompat.checkSelfPermission(this, requiredPermissions[i]);
            if (result != PackageManager.PERMISSION_GRANTED) {
                validate = false;
            }
        }
        if (!validate) {
            ActivityCompat.requestPermissions(this, requiredPermissions, 1);
            System.exit(0);
        } else {
        }
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                obtainData();
                Log.i("old",nameList.get(3));
                LatLng position = new LatLng(lat,lng);
                if(position != null) {
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull MapboxMap mapboxMap) {
                            map = mapboxMap;
                            mapboxMap.setCameraPosition(
                                    new CameraPosition.Builder()
                                            .target(new LatLng(lat, lng))
                                            .zoom(16)
                                            .build());
                            map.setStyle(Style.DARK, new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    obtainData();
                                    SymbolManager sm = new SymbolManager(mapView, map, style);
                                    sm.setIconAllowOverlap(true);
                                    markerViewManager = new MarkerViewManager(mapView, map);
                                    final View customView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
                                    customView.setLayoutParams(new FrameLayout.LayoutParams(350, 950));

                                    SymbolOptions so = new SymbolOptions()
                                            .withLatLng(new LatLng(lat, lng))
                                            .withIconImage("marker-15")
                                            .withTextField("Current Location")
                                            .withTextColor("White")
                                            .withIconSize(2f);
                                    //these symbols have their lat and lang set as the size of the arraylist minus an integer to allow for dynamic styling where the arraylist will have more than 10 items
                                    SymbolOptions so1 = new SymbolOptions()
                                            .withLatLng(new LatLng(Double.parseDouble(latList.get(latList.size()-10)), Double.parseDouble(lngList.get(latList.size()-10))))
                                            .withIconImage("restaurant-15")
                                            .withIconOffset(new Float[]{0f, -9f})
                                            .withIconSize(1f);

                                    SymbolOptions so2 = new SymbolOptions()
                                            .withLatLng(new LatLng(Double.parseDouble(latList.get(latList.size()-9)), Double.parseDouble(lngList.get(latList.size()-9))))
                                            .withIconImage("restaurant-15")
                                            .withIconOffset(new Float[]{0f, 9f})
                                            .withIconSize(1f);

                                    SymbolOptions so3 = new SymbolOptions()
                                            .withLatLng(new LatLng(Double.parseDouble(latList.get(latList.size()-8)), Double.parseDouble(lngList.get(latList.size()-8))))
                                            .withIconImage("restaurant-15")
                                            .withIconOffset(new Float[]{9f, 0f})
                                            .withIconSize(1f);

                                    SymbolOptions so4 = new SymbolOptions()
                                            .withLatLng(new LatLng(Double.parseDouble(latList.get(latList.size()-7)), Double.parseDouble(lngList.get(latList.size()-7))))
                                            .withIconImage("restaurant-15")
                                            .withIconOffset(new Float[]{0f, 18f})
                                            .withIconSize(1f);

                                    SymbolOptions so5 = new SymbolOptions()
                                            .withLatLng(new LatLng(Double.parseDouble(latList.get(latList.size()-6)), Double.parseDouble(lngList.get(latList.size()-6))))
                                            .withIconImage("restaurant-15")
                                            .withIconOffset(new Float[]{0f, -9f})
                                            .withIconSize(1f);

                                    SymbolOptions so6 = new SymbolOptions()
                                            .withLatLng(new LatLng(Double.parseDouble(latList.get(latList.size()-5)), Double.parseDouble(lngList.get(latList.size()-5))))
                                            .withIconImage("restaurant-15")
                                            .withIconOffset(new Float[]{0f, 9f})
                                            .withIconSize(1f);

                                    SymbolOptions so7 = new SymbolOptions()
                                            .withLatLng(new LatLng(Double.parseDouble(latList.get(latList.size()-4)), Double.parseDouble(lngList.get(latList.size()-4))))
                                            .withIconImage("restaurant-15")
                                            .withIconOffset(new Float[]{9f, 0f})
                                            .withIconSize(1f);

                                    SymbolOptions so8 = new SymbolOptions()
                                            .withLatLng(new LatLng(Double.parseDouble(latList.get(latList.size()-3)), Double.parseDouble(lngList.get(latList.size()-3))))
                                            .withIconImage("restaurant-15")
                                            .withIconSize(1f);

                                    SymbolOptions so9 = new SymbolOptions()
                                            .withLatLng(new LatLng(Double.parseDouble(latList.get(latList.size()-2)), Double.parseDouble(lngList.get(latList.size()-2))))
                                            .withIconImage("restaurant-15")
                                            .withIconOffset(new Float[]{0f, -18f})
                                            .withIconSize(1f);

                                    SymbolOptions so10 = new SymbolOptions()
                                            .withLatLng(new LatLng(Double.parseDouble(latList.get(latList.size()-1)), Double.parseDouble(lngList.get(latList.size()-1))))
                                            .withIconImage("restaurant-15")
                                            .withIconOffset(new Float[]{0f, 9f})
                                            .withIconSize(1f);

                                    Symbol s1 = sm.create(so);
                                    Symbol s2 = sm.create(so1);
                                    Symbol s3 = sm.create(so2);
                                    Symbol s4 = sm.create(so3);
                                    Symbol s5 = sm.create(so4);
                                    Symbol s6 = sm.create(so5);
                                    Symbol s7 = sm.create(so6);
                                    Symbol s8 = sm.create(so7);
                                    Symbol s9 = sm.create(so8);
                                    Symbol s10 = sm.create(so9);
                                    Symbol s11 = sm.create(so10);
                                    final Symbol[] slist = new Symbol[]{s2, s3, s4, s5, s6, s7, s8, s9, s10, s11};


                                    sm.addClickListener(new OnSymbolClickListener() {
                                        @Override
                                        public void onAnnotationClick(Symbol symbol) {
                                            //this line is used to remove markers if the user click
                                            //another icon before the icon is removed automatically
                                            markerViewManager.removeMarker(markerView);
                                            for (Symbol s : slist) {
                                                if (symbol == s) {
                                                    int i = 10 - Arrays.asList(slist).indexOf(s);
                                                    TextView title = customView.findViewById(R.id.text);
                                                    ImageView rating = customView.findViewById(R.id.image);
                                                    rating.setMaxWidth(300);
                                                    title.setText(nameList.get(nameList.size()-i) + "\n" + "\n"+ "Address:" +"\n" + addressList.get(addressList.size()-i) + "\n" + addressList2.get(addressList2.size()-i)+"\n" + addressList3.get(addressList3.size()-i) +
                                                            "\n" + postcodeList.get(postcodeList.size()-i)+ "\n" + "\n" + "Distance from current location:" + distanceList.get(distanceList.size()-i) + "KM");
                                                    switch (Integer.parseInt(ratingList.get(i))) {
                                                        case 1:
                                                            rating.setImageResource(R.drawable.ratingone);
                                                            break;
                                                        case 2:
                                                            rating.setImageResource(R.drawable.ratingtwo);
                                                            break;
                                                        case 3:
                                                            rating.setImageResource(R.drawable.ratingthree);
                                                            break;
                                                        case 4:
                                                            rating.setImageResource(R.drawable.ratingfour);
                                                            break;
                                                        case 5:
                                                            rating.setImageResource(R.drawable.ratingfive);
                                                            break;
                                                    }
                                                    markerView = new MarkerView(new LatLng(Double.parseDouble(latList.get(latList.size()-i)), Double.parseDouble(lngList.get(latList.size()-i))), customView);
                                                    markerViewManager.addMarker(markerView);
                                                    handler.postDelayed(r, 5000);

                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    public void obtainData(){
        try {
            URL search = new URL("http://sandbox.kriswelsh.com/hygieneapi/hygiene.php?op=search_location&lat="+lat+"&long="+lng);
            URLConnection conn =  search.openConnection();
            InputStreamReader isr = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                JSONArray ja = new JSONArray(line);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = (JSONObject) ja.get(i);
                    //jLocation is needed for accessing nested JSON Data
                    JSONObject jlocation = (JSONObject) jo.get("Location");
                    nameList.add(jo.getString("BusinessName"));
                    addressList.add(jo.getString("AddressLine1"));
                    addressList2.add(jo.getString("AddressLine2"));
                    addressList3.add(jo.getString("AddressLine3"));
                    postcodeList.add(jo.getString("PostCode"));
                    ratingList.add(jo.getString("RatingValue"));
                    latList.add(jlocation.getString("Latitude"));
                    lngList.add(jlocation.getString("Longitude"));
                    distanceList.add(jo.getString("DistanceKM"));

                }
                //needed to be able to access JSON Data outside this method
                getList(nameList);
                getList(addressList);
                getList(addressList2);
                getList(addressList3);
                getList(latList);
                getList(lngList);
                getList(ratingList);
                getList(distanceList);
            }
        }
        catch(MalformedURLException e){e.printStackTrace();}
        catch(IOException e){e.printStackTrace();}
        catch(JSONException e){e.printStackTrace();}
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.searchCurrentLocation:
                Intent cl = new Intent(this,MainActivity.class);
                startActivity(cl);
                break;

            case R.id.searchMostRecent:
                Intent mr = new Intent(this, MostRecent.class);
                startActivity(mr);
                break;

            case R.id.searchAddress:
                Intent sa = new Intent(this,LocationSearch.class);
                startActivity(sa);
                break;

            case R.id.searchName:
                Intent sn = new Intent(this,NameSearch.class);
                startActivity(sn);
                break;
        }
        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
        mapView.onStart();
    }
    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }
    //getter function
    public ArrayList<String> getList(ArrayList<String> List){return List;}

}