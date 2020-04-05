package com.example.coursework;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

public class MostRecent extends AppCompatActivity {
    ArrayList<String> postcodeList = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<String> ratingList = new ArrayList<>();
    ArrayList<String> addressList = new ArrayList<>();
    ArrayList<String> addressList2 = new ArrayList<>();
    ArrayList<String> addressList3 = new ArrayList<>();
    boolean validate = true;
    int length;
    String[] requiredPermissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_recent);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //check if required permissions are granted
        for(int i = 0; i<requiredPermissions.length; i++){
            int result = ActivityCompat.checkSelfPermission(this,requiredPermissions[i]);
            if(result!= PackageManager.PERMISSION_GRANTED){
                validate = false;
            }
        }
        if(!validate){
            ActivityCompat.requestPermissions(this, requiredPermissions, 1);
            System.exit(0);
        }
        obtainData();
        popTable();
    }

    public int obtainData() {
        try {
            URL search = new URL("http://sandbox.kriswelsh.com/hygieneapi/hygiene.php?op=show_recent");
            URLConnection conn = search.openConnection();
            InputStreamReader isr = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                JSONArray ja = new JSONArray(line);
                length = ja.length();
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = (JSONObject) ja.get(i);
                    nameList.add(jo.getString("BusinessName"));
                    ratingList.add(jo.getString("RatingValue"));
                    addressList.add(jo.getString("AddressLine1"));
                    addressList2.add(jo.getString("AddressLine2"));
                    addressList3.add(jo.getString("AddressLine3"));
                    postcodeList.add(jo.getString("PostCode"));
                }
                //needed to be able to access JSON Data outside this method
                getList(nameList);
                getList(ratingList);
                getList(addressList);
                getList(addressList2);
                getList(addressList3);
                getList(postcodeList);

            }
        }
        catch (MalformedURLException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }
        catch (JSONException e) { e.printStackTrace(); }
        return length;
    }

    public void popTable(){
        TableLayout table = findViewById(R.id.table);
        for(int i = 0; i < length; i++){
            TableRow row = new TableRow(this);
            row.setLayoutParams(params);
            TextView name = new TextView(this);
            TextView address = new TextView(this);
            TextView rating = new TextView(this);
            name.setText(nameList.get(i));
            if(Integer.parseInt(ratingList.get(i)) < 0){
                rating.setText("Exempt");
            }else{
                rating.setText(ratingList.get(i));
            }
            address.setText(addressList.get(i) + "\n" +addressList2.get(i)+ "\n" + addressList3.get(i) + "\n" + postcodeList.get(i));
            name.setWidth(400);
            address.setWidth(500);
            rating.setWidth(200);
            row.addView(name);
            row.addView(address);
            row.addView(rating);
            table.addView(row, params);
        }
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
    }
    @Override
    public  void onResume(){ super.onResume(); }
    //getter functions
    public ArrayList<String> getList(ArrayList<String> List){return List;}
}
