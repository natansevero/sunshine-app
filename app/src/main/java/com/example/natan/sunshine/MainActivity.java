package com.example.natan.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mRequestLoadingProgressBar;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mRequestLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_request);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mForecastAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mForecastAdapter);

        loadData();
    }

    private void loadData() {
        showWheatherData();
        URL url = Util.buildURL("1600 Amphitheatre Parkway, lMountain View, CA 94043");
        new RequestDataTask().execute(url);
    }

    @Override
    public void onClick(String weatherDayData) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, weatherDayData);
        startActivity(intent);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void showWheatherData() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
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

                String[] formatedData = Util.makeWeatherDatas(response);

                mForecastAdapter.setmWeatherData(formatedData);
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
            mForecastAdapter.setmWeatherData(null);
            loadData();

            return true;
        }

        if(itemWasSelected == R.id.open_map_action) {
            openMap();
            
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openMap() {
        String address = "1600 Ampitheatre Parkway, CA";
        Uri uri = Uri.parse("geo:0,0?q="+address);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        } else {
            Log.d("ERROR_OPEN_MAP", uri.toString() + " no app for open this intent");
        }
    }
}
