package com.example.coursework;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LocationSearch extends AppCompatActivity {
    Button button;
    EditText edittext;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        button = findViewById(R.id.button);
        edittext = findViewById(R.id.editText);
    }

    public void collectData(View v){
        location = edittext.getText().toString();
        Intent intent = new Intent(LocationSearch.this, LocationMap.class);
        intent.putExtra("location",location);
        startActivity(intent);
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

}
