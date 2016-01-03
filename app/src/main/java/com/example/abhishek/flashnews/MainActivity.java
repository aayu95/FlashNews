package com.example.abhishek.flashnews;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.abhishek.flashnews.DataClasses.NewsData;
import com.example.abhishek.flashnews.ThreadingParsingClasses.ImageDownloaderTask;
import com.example.abhishek.flashnews.ThreadingParsingClasses.XMLPullParserHandler;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RVAdapter adapter;
    private RecyclerView newslist;
    private String THE_HINDU = "http://www.thehindu.com/news/?service=rss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        newslist = (RecyclerView) findViewById(R.id.newsList);
        new ExtractXmlTask().execute(THE_HINDU);
    }

    private class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

        List<NewsData> newsList;

        public RVAdapter(List<NewsData> laptops){
            this.newsList = laptops;
        }

        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_layout, parent, false);
            PersonViewHolder pvh = new PersonViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(final PersonViewHolder holder, int position) {
            final NewsData data = newsList.get(position);
            holder.newsHeading.setText(data.getTitle());
            if (holder.newsImage != null) {
                new ImageDownloaderTask(holder.newsImage, holder.progressBar).execute(data.getUrl());
            }
        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public class PersonViewHolder extends RecyclerView.ViewHolder {
            TextView newsHeading;
            ProgressBar progressBar;
            ImageView newsImage;

            PersonViewHolder(View itemView) {
                super(itemView);
                newsHeading = (TextView) itemView.findViewById(R.id.newsHeading);
                progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
                newsImage = (ImageView) itemView.findViewById(R.id.newsImage);
            }
        }

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
                adapter = new RVAdapter(newsDatas);
                newslist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                newslist.setAdapter(adapter);
            }
        }
    }

}


