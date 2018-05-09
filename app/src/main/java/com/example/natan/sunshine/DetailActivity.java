package com.example.natan.sunshine;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String SHARE_HASHTAG = " #SunshineApp";
    private String mWeatherDataForDay;
    private TextView mWeatherDisplayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mWeatherDisplayTextView = (TextView) findViewById(R.id.tv_weather_display);

        Intent intent = getIntent();
        if(intent.hasExtra(Intent.EXTRA_TEXT)) {
            mWeatherDataForDay = intent.getStringExtra(Intent.EXTRA_TEXT);
            mWeatherDisplayTextView.setText(mWeatherDataForDay);
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mWeatherDataForDay + SHARE_HASHTAG)
                .getIntent();

        return shareIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.share_action);
        menuItem.setIntent(createShareIntent());
        return true;
    }
}
