package com.example.natan.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<String[]>, SharedPreferences.OnSharedPreferenceChangeListener{

    private static final int ID_FORECAST_LOADER = 22;
    private boolean PREFERENCES_HAS_BEEN_CHANGED = false;

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private TextView mErrorMessageTextView;
    private ProgressBar mRequestLoadingProgressBar;

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

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[]> loader = loaderManager.getLoader(ID_FORECAST_LOADER);
        loaderManager.initLoader(ID_FORECAST_LOADER, null, this);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
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

    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            String[] mWeatherData = null;

            @Override
            protected void onStartLoading() {
                if(mWeatherData != null) {
                    deliverResult(mWeatherData);
                } else {
                    mRequestLoadingProgressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public String[] loadInBackground() {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String location = sharedPreferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_defautl));
                Log.d("LOCATION", location);
                URL url = Util.buildURL(location);

                try {

                    String response = Util.requestData(url);
                    String[] formatedData = Util.makeWeatherDatas(response);

                    return formatedData;

                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] data) {
        mRequestLoadingProgressBar.setVisibility(View.INVISIBLE);
        if (data != null) {
            showWheatherData();

            mForecastAdapter.setmWeatherData(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {

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
            showWheatherData();
            getSupportLoaderManager().restartLoader(ID_FORECAST_LOADER, null, this);

            return true;
        }

        if(itemWasSelected == R.id.open_map_action) {
            openMap();
            
            return true;
        }

        if(itemWasSelected == R.id.settings_action) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

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

    @Override
    protected void onStart() {
        super.onStart();

        if(PREFERENCES_HAS_BEEN_CHANGED) {
            getSupportLoaderManager().restartLoader(ID_FORECAST_LOADER, null, this);
            PREFERENCES_HAS_BEEN_CHANGED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAS_BEEN_CHANGED = true;
    }
}
