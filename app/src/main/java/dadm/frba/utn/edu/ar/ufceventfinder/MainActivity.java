package dadm.frba.utn.edu.ar.ufceventfinder;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import dadm.frba.utn.edu.ar.ufceventfinder.utilities.NetworkUtils;
import dadm.frba.utn.edu.ar.ufceventfinder.utilities.UFCJsonUtils;

public class MainActivity extends AppCompatActivity {

    private TextView mUFCTitle;
    private TextView mUFCEventsResults;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private static final int NUM_LIST_ITEMS = 100;
    private UFCAdapter mAdapter;
    private RecyclerView mUFCEventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUFCTitle = (TextView) findViewById(R.id.tv_tittle);

        mUFCEventsResults = (TextView) findViewById(R.id.tv_results);
        mUFCEventsResults.setText("UFC Event Dummy");

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mUFCEventsList = (RecyclerView) findViewById(R.id.rv_results);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mUFCEventsList.setLayoutManager(layoutManager);
        mUFCEventsList.setHasFixedSize(true);
        mAdapter = new UFCAdapter(NUM_LIST_ITEMS);
        mUFCEventsList.setAdapter(mAdapter);

    }

    private void makeSearch() {
        URL UFCEventSearchURL = NetworkUtils.buildUrl();
        new UFCNetworkTask().execute(UFCEventSearchURL);
    }

    private void showErrorMessage(){
        mUFCEventsResults.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showUFCEvents(){
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mUFCEventsResults.setVisibility(View.VISIBLE);
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
                mUFCEventsResults.setText(UFCEventsResult[0].toString());
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
            makeSearch();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
