package com.example.mayank.linkpreview;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    EditText mEditText;
    Button mButton;

    String s;
    URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.urlLinkTv);
        mButton = (Button) findViewById(R.id.findPreviewButton);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                s = mEditText.getText().toString();
                try {
                    url = new URL(s);
                    Log.d(TAG, "Success in forming URL.");
                    Log.d(TAG, "URL FOUND. URL = " + url);

                    ( new ParseURL() ).execute(new String[]{s});

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error = " + e);
                }

            }
        });

    }


    private class ParseURL extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            StringBuffer buffer = new StringBuffer();
            try {
                Log.d("JSwa", "Connecting to [" + strings[0] + "]");
                Document doc = Jsoup.connect(strings[0]).get();
                Log.d("JSwa", "Connected to [" + strings[0] + "]");
                // Get document (HTML page) title
                String title = doc.title();
                Log.d("JSwA", "Title [" + title + "]");
                buffer.append("Title: " + title + "\r\n");

                // Get meta info
                Elements metaElems = doc.select("meta");
                buffer.append("META DATA\r\n");
                for (Element metaElem : metaElems) {
                    String name = metaElem.attr("name");
                    String content = metaElem.attr("content");
                    buffer.append("name [" + name + "] - content [" + content + "] \r\n");
                }

                Elements topicList = doc.select("h2.topic");
                buffer.append("Topic list\r\n");
                for (Element topic : topicList) {
                    String data = topic.text();

                    buffer.append("Data [" + data + "] \r\n");
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }

            return buffer.toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);


            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Link Preview")
                    .setMessage(data)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "User pressed okay.");
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
    }
}
