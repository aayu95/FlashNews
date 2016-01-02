package com.example.abhishek.flashnews;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.abhishek.flashnews.DataClasses.NewsData;
import com.example.abhishek.flashnews.ThreadingParsingClasses.XMLPullParserHandler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class Splashscreen extends AppCompatActivity {

    public static List<NewsData> newsDataList = null;

    private String THE_HINDU = "http://www.thehindu.com/news/?service=rss";

    private int SCREEN_TIMEOUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        new ExtractXmlTask().execute(THE_HINDU);

        if (isFirstTime()) {

            startSplashscreen();
        } else {
            startSplashscreen();
        }

    }

    private boolean isFirstTime() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isfirstrun", true);
        if (isFirstRun) {
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().
                    putBoolean("isfirstrun", false).commit();
            return true;

        } else {
            return false;
        }
    }

    private void startSplashscreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent proceedToMain = new Intent(Splashscreen.this, MainActivity.class);
                startActivity(proceedToMain);
                finish();
            }
        }, SCREEN_TIMEOUT);
    }

    private class ExtractXmlTask extends AsyncTask<String, Void, List<NewsData>> {

        private String getXmlFromUrl(String urlString) {
            StringBuffer output = new StringBuffer("");
            try {
                InputStream stream = null;
                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();
                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
                    String s = "";
                    while ((s = buffer.readLine()) != null)
                        output.append(s);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return output.toString();
        }
        @Override
        protected List<NewsData> doInBackground(String... params) {
            List<NewsData> newsDataList = null;
            String xml = getXmlFromUrl(params[0]);
            InputStream stream = new ByteArrayInputStream(xml.getBytes());
            newsDataList = new XMLPullParserHandler().parse(stream);
            return newsDataList;
        }

        @Override
        protected void onPostExecute(List<NewsData> newsDatas) {
            if (newsDatas != null) {
                newsDataList = newsDatas;
            }
        }
    }
}