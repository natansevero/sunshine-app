package com.example.natan.sunshine;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherDatasTextView;
    private TextView mErrorMessageTextView;
    private ProgressBar mRequestLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeatherDatasTextView = (TextView) findViewById(R.id.tv_weather_datas);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mRequestLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_request);


        loadData();
    }

    private void loadData() {
        showWheatherData();
        URL url = Util.buildURL("1600 Amphitheatre Parkway, lMountain View, CA 94043");
        new RequestDataTask().execute(url);
    }

    private void showErrorMessage() {
        mWeatherDatasTextView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void showWheatherData() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mWeatherDatasTextView.setVisibility(View.VISIBLE);
    }

    public class RequestDataTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRequestLoadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {

            if(urls.length == 0) return null;

            try {

                return Util.requestData(urls[0]);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            mRequestLoadingProgressBar.setVisibility(View.INVISIBLE);
            if (response != null) {
                showWheatherData();

                List<String> formatedData = Util.makeWeatherDatas(response);

                for(String data: formatedData) {
                    mWeatherDatasTextView.append(data + "\n\n");
                }
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemWasSelected = item.getItemId();
        if(itemWasSelected == R.id.refresh_action) {
            mWeatherDatasTextView.setText("");
            loadData();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
