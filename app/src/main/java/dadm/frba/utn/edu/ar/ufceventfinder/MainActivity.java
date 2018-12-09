package dadm.frba.utn.edu.ar.ufceventfinder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import dadm.frba.utn.edu.ar.ufceventfinder.utilities.NetworkUtils;
import dadm.frba.utn.edu.ar.ufceventfinder.utilities.UFCJsonUtils;

public class MainActivity extends AppCompatActivity {

    private TextView mUFCTitle;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final int NUM_LIST_ITEMS = 100;
    private UFCAdapter mAdapter;
    private RecyclerView mUFCEventsList;

    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);

        mUFCTitle = (TextView) findViewById(R.id.tv_tittle);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mUFCEventsList = (RecyclerView) findViewById(R.id.rv_results);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mUFCEventsList.setLayoutManager(layoutManager);
        mUFCEventsList.setHasFixedSize(true);
        mAdapter = new UFCAdapter(NUM_LIST_ITEMS);
        mUFCEventsList.setAdapter(mAdapter);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }


    private void makeSearch() {
        URL UFCEventSearchURL = NetworkUtils.buildUrl();
        new UFCNetworkTask().execute(UFCEventSearchURL);
    }

    private void showErrorMessage(){
        mUFCEventsList.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showUFCEvents(){
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mUFCEventsList.setVisibility(View.VISIBLE);
    }


    public class UFCNetworkTask extends AsyncTask<URL, Void, JSONObject[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject[] doInBackground(URL... params) {

            if(params.length == 0){
                return null;
            }

            URL searchUrl = params[0];
            String UFCEventsResults = null;
            try {
                UFCEventsResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                JSONObject[] UFCEventsJsons = UFCJsonUtils.getSimpleEventsJsonsFromWholeJson(MainActivity.this,UFCEventsResults);
                return UFCEventsJsons;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject[] UFCEventsResult) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (UFCEventsResult != null) {
                showUFCEvents();
                mAdapter.setUFCEventsData(UFCEventsResult);
               // mUFCEventsResults.setText(UFCEventsResult[0].toString());
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();
        if(itemThatWasClicked == R.id.action_search){

            //Check Permissions
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                makeSearch();
                return true;
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public Address getLocationFromCity(String city){

        Geocoder coder = new Geocoder(MainActivity.this);
        List<Address> address;

        if (city == null){
            return null;
        }

        try {
            address = coder.getFromLocationName(city,1);
            if (address == null || address.size()==0){
                return null;
            }
            Address location = address.get(0);
            return location;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        makeSearch();
                    }

                } else {

                    // permission denied, boo!
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                    makeSearch();
                }
                return;
            }
        }
    }


}
