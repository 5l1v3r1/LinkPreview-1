package com.example.mayank.linkpreview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    EditText mEditText;
    Button mButton;

    String s;
    URL url;

    ProgressDialog mProgressDialog;

    String previewTitle;
    String previewDescription;
    Bitmap previewLogo;

    TextView mTitleTv;
    TextView mDescriptionTv;
    ImageView mLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.urlLinkTv);
        mButton = (Button) findViewById(R.id.findPreviewButton);

        View cardview_layout = findViewById(R.id.card_view);
        mTitleTv = (TextView) cardview_layout.findViewById(R.id.title);
        mDescriptionTv = (TextView) cardview_layout.findViewById(R.id.description);
        mLogo = (ImageView) cardview_layout.findViewById(R.id.thumbnail);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                s = mEditText.getText().toString();
                try {
                    url = new URL(s);
                    Log.d(TAG, "Success in forming URL.");
                    Log.d(TAG, "URL FOUND. URL = " + url);
                    new Title().execute();
                    new Description().execute();
                    new Logo().execute();

                    mTitleTv.setText(previewTitle);
                    mDescriptionTv.setText(previewDescription);
                    mLogo.setImageBitmap(previewLogo);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error = " + e);
                }

            }
        });
    }

    // Title AsyncTask
    private class Title extends AsyncTask<Void, Void, Void> {
        String title;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Please wait");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(String.valueOf(url)).get();
                // Get the html document title
                title = document.title();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into previewTitle
            previewTitle  = title;
            mProgressDialog.dismiss();
        }
    }

    // Description AsyncTask
    private class Description extends AsyncTask<Void, Void, Void> {
        String desc;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Please wait");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document document = Jsoup.connect(String.valueOf(url)).get();
                // Using Elements to get the Meta data
                Elements description = document
                        .select("meta[name=description]");
                // Locate the content attribute
                desc = description.attr("content");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set desc into previewDescription
            previewDescription = desc;
            mProgressDialog.dismiss();
        }
    }

    // Logo AsyncTask
    private class Logo extends AsyncTask<Void, Void, Void> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Please wait");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                // Connect to the web site
                Document document = Jsoup.connect(String.valueOf(url)).get();
                // Using Elements to get the class data
                Elements img = document.select("a[class=brand brand-image] img[src]");
                // Locate the src attribute
                String imgSrc = img.attr("src");
                // Download image from URL
                InputStream input = new java.net.URL(imgSrc).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set downloaded image into previewLogo
            previewLogo = bitmap;
            mProgressDialog.dismiss();
        }
    }

}
