package dadm.frba.utn.edu.ar.ufceventfinder;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

import dadm.frba.utn.edu.ar.ufceventfinder.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private TextView mUFCTitle;
    private TextView mUFCEventsResults;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUFCTitle = (TextView) findViewById(R.id.tv_tittle);

        mUFCEventsResults = (TextView) findViewById(R.id.tv_results);
        mUFCEventsResults.setText("UFC Event Dummy");

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

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


    public class UFCNetworkTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String UFCEventsResults = null;
            try {
                UFCEventsResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return UFCEventsResults;
        }

        @Override
        protected void onPostExecute(String UFCEventsResult) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (UFCEventsResult != null && !UFCEventsResult.equals("")) {
                showUFCEvents();
                mUFCEventsResults.setText(UFCEventsResult);
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
