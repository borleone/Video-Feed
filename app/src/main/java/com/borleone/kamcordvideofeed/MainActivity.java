package com.borleone.kamcordvideofeed;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    GridView grid;

    ArrayList<Integer> heartCount = new ArrayList<Integer>();
    ArrayList<String> shotThumbnail = new ArrayList<String>();
    ArrayList<String> play = new ArrayList<String>();

    //URL to get JSON Array
    private static String url = "https://api.kamcord.com/v1/feed/set/featuredShots?count=20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new JSONParse().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Asynctask for fetching data from url using REST API
    private class JSONParse extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            String json = "";
            JSONObject jObj = null;
            try {

                // Create URL
                URL url_videofeed = new URL(url);

                // Create connection
                HttpsURLConnection myConnection = (HttpsURLConnection) url_videofeed.openConnection();

                myConnection.setRequestProperty("Accept-Language", "en");
                myConnection.setRequestProperty("device-token", "abc123");
                myConnection.setRequestProperty("client-name", "android");
                myConnection.setRequestProperty("Accept", "application/json");

                if (myConnection.getResponseCode() == 200) {
                    // Success
                    // Further processing here
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                    try {
                        BufferedReader reader = new BufferedReader(responseBodyReader);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        responseBody.close();
                        responseBodyReader.close();
                        json = sb.toString();
                    } catch (Exception e) {
                        Log.e("Buffer Error", "Error converting result " + e.toString());
                    }

                    // try parse the string to a JSON object
                    try {
                        jObj = new JSONObject(json);
                    } catch (JSONException e) {
                        Log.e("JSON Parser", "Error parsing data " + e.toString());
                    }

                    // return JSON String
                    return jObj;


                } else {
                    // Error handling code goes here
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }
            return jObj;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            Log.d("JSON", jsonObject.toString());

            try {
                JSONArray arrray_groups = jsonObject.getJSONArray("groups");
                JSONObject jObj = arrray_groups.getJSONObject(0);
                Log.d("JSON", jObj.toString());
                JSONArray array_cards = jObj.getJSONArray("cards");

                for (int i = 0; i < array_cards.length(); ++i) {
                    JSONObject jObj1 = array_cards.getJSONObject(i);
                    Log.d("JSON", jObj1.toString());
                    JSONObject jObj2 = jObj1.getJSONObject("shotCardData");
                    Log.d("JSON", jObj2.toString());
                    int heart_count = jObj2.getInt("heartCount");
                    heartCount.add(heart_count);
                    JSONObject jObj3 = jObj2.getJSONObject("shotThumbnail");
                    String thumbnail_url = jObj3.getString("medium");
                    shotThumbnail.add(thumbnail_url);
                    JSONObject jObj4 = jObj2.getJSONObject("play");
                    String play_video = jObj4.getString("mp4");
                    play.add(play_video);
                    Log.d("jsarray", heart_count + " " + thumbnail_url + " " + play_video);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            CustomGrid adapter = new CustomGrid(MainActivity.this, heartCount, shotThumbnail, play);
            grid = (GridView) findViewById(R.id.grid);
            grid.setAdapter(adapter);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MainActivity.this, "Playing video " + position, Toast.LENGTH_SHORT).show();
                    //Create intent
                    Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                    intent.putExtra("url", play.get(position));

                    //Start details activity
                    startActivity(intent);
                }
            });

        }

    }

}
