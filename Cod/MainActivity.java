package com.example.sunshine;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText SearchGorod;
    private ImageButton poiskButton;
    private TextView textGorod;

    private TextView city_name;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        SearchGorod = findViewById(R.id.SearchGorod);
        poiskButton = findViewById(R.id.poiskButton);
        textGorod = findViewById(R.id.textGorod);
        city_name = findViewById(R.id.city_name);

        poiskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SearchGorod.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.Vvedite_Text, Toast.LENGTH_LONG).show();
                else {
                    String city = SearchGorod.getText().toString();
                    String key = "efb99e2eebcfda230b7c66d7cbdb9826";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";


                    new GetURLData().execute(url);
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                String data = buffer.toString();
                Log.d("JSON_DATA", data); // Добавленный код

                return data;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    JSONObject jsonObject = new JSONObject(result);

                    // Проверяем, есть ли данные о погоде в JSON-ответе
                    if (jsonObject.has("main")) {
                        // Получаем данные о погоде
                        JSONObject main = jsonObject.getJSONObject("main");
                        double temperature = main.getDouble("temp");
                        int roundedTemperature = (int) Math.round(temperature);
                        String cityName = jsonObject.getString("name");
                        String formattedTemperature = roundedTemperature + "°";
                        String displayText = formattedTemperature;
                        textGorod.setText(displayText);
                        city_name.setText(cityName);
                    }
                } else {
                    // Выводим сообщение об ошибке, если результат null
                    Toast.makeText(MainActivity.this, "Ошибка: Некорректное название города", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
