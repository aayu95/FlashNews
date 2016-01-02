package com.example.abhishek.flashnews.ThreadingParsingClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.abhishek.flashnews.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by abhishek on 1/2/16.
 */
public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private final WeakReference<ProgressBar> progressBarWeakReference;

    public ImageDownloaderTask(ImageView imageView, ProgressBar bar) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        progressBarWeakReference = new WeakReference<ProgressBar>(bar);
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String url = "";
        Bitmap bitmap = null;
        try {
            Document doc = Jsoup.connect(params[0]).get();
            Elements imageLink = doc.getElementsByClass("main-image");
            for (Element src: imageLink) {
                if (src.tagName().equals("img")) {
                    url = src.attr("src");
                }
            }
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPreExecute() {
        ImageView imageView = imageViewReference.get();
        ProgressBar progressBar = progressBarWeakReference.get();
        imageView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            ProgressBar progressBar = progressBarWeakReference.get();
            if (imageView != null) {
                imageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.placeholder);
                    imageView.setImageDrawable(placeholder);
                }
            }
        }
    }
}
