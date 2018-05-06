package com.example.natan.sunshine;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Util {

    public static final String URL_REQUEST = "https://andfun-weather.udacity.com/staticweather";
    public static final String PARAMETER_QUERY = "q";

    public static URL buildURL(String query) {
        Uri uri = Uri.parse(URL_REQUEST).buildUpon()
                .appendQueryParameter(PARAMETER_QUERY, query)
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String requestData(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if(scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static List<String> makeWeatherDatas(String data) {
        List<String> weatherData = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(data);
            JSONArray list = json.getJSONArray("list");

            for(int i = 0; i < list.length(); i++) {
                JSONObject objectList = list.getJSONObject(i);
                JSONArray weather = objectList.getJSONArray("weather");
                JSONObject weatherJSONObject = weather.getJSONObject(0);

                String format = String.format("Id: %d / Main: %s / Description: %s",
                        weatherJSONObject.getInt("id"),
                        weatherJSONObject.getString("main"),
                        weatherJSONObject.getString("description"));

                weatherData.add(format);
            }

            return weatherData;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();

    }

}
