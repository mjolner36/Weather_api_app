package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    String cityString = "";
    String APIkey = "f512d90c89c076d365eaa2e7752736a3";

    Button main_btn;
    EditText user_field;
    TextView result_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_btn = findViewById(R.id.main_btn);
        user_field = findViewById(R.id.user_field);
        result_info = findViewById(R.id.result_info);
    }

    public void OnClick(View view){
        if (!user_field.getText().toString().trim().equals("")){
            cityString = user_field.getText().toString().trim();
            DownloadTask task = new DownloadTask();
            task.execute("https://api.openweathermap.org/data/2.5/weather?q="+cityString+"&appid="+APIkey+"&units=metric");
        }
        else Toast.makeText(MainActivity.this,R.string.no_user_input,Toast.LENGTH_SHORT).show();


    }

    private class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result +=current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";

                for (int i=0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")) {
                        message += main + ": " + description + "\r\n";
                    }
                }
                result_info.setText(message+"Temperature: "+jsonObject.getJSONObject("main").getString("temp"));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }
    }

}