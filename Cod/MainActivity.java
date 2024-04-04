package com.example.sunshine;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText SearchGorod;
    private ImageButton poiskButton;
    private TextView textGorod;
    private TextView city_name;
    private ImageButton SunnyButton;
    private ImageButton RainButton;
    private TextView SunnyText;
    private View RainText;

    public LinearLayout LinearLayout1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        SearchGorod = findViewById(R.id.Search_Gorod);
        poiskButton = findViewById(R.id.poiskButton);
        textGorod = findViewById(R.id.textGorod);
        city_name = findViewById(R.id.city_name);
        SunnyButton = findViewById(R.id.SunnyButton);
        RainButton = findViewById(R.id.RainButton);
        SunnyText = findViewById(R.id.SunnyText);
        RainText = findViewById(R.id.RainText);


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
        SunnyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!city_name.getText().toString().isEmpty()) {
                    String city = city_name.getText().toString();
                    String key = "efb99e2eebcfda230b7c66d7cbdb9826";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";
                    new GetSunriseSunsetData().execute(url);
                    SunnyText.setVisibility(View.VISIBLE);
                    RainText.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "Сначала введите город", Toast.LENGTH_SHORT).show();
                }
            }
        });

        RainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!city_name.getText().toString().isEmpty()) {
                    String city = city_name.getText().toString();
                    String key = "efb99e2eebcfda230b7c66d7cbdb9826";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric";
                    new GetSunriseSunsetData().execute(url);
                    new GetHumidityData().execute(url);
                    RainText.setVisibility(View.VISIBLE); // Показать ScrollView при нажатии на кнопку RainButton
                    SunnyText.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "Сначала введите город", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> implements com.example.sunshine.GetURLData {

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

    private class GetHumidityData extends AsyncTask<String, Void, String> {

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
                        double humidity = main.getDouble("humidity");
                        int roundedClouds = (int) Math.round(humidity);

                        Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);

                        // Установка значения в соответствующий ImageView
                        LinearLayout linearLayout;
                        ImageView humidityImageView;

                        if (hour >= 0 && hour < 2) {
                            linearLayout = findViewById(R.id.LinearLayout1);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView);
                        } else if (hour >= 2 && hour < 4) {
                            linearLayout = findViewById(R.id.LinearLayout2);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView2);
                        } else if (hour >= 4 && hour < 6) {
                            linearLayout = findViewById(R.id.LinearLayout3);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView3);
                        } else if (hour >= 6 && hour < 8) {
                            linearLayout = findViewById(R.id.LinearLayout4);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView4);
                        } else if (hour >= 8 && hour < 10) {
                            linearLayout = findViewById(R.id.LinearLayout5);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView5);
                        } else if (hour >= 10 && hour < 12) {
                            linearLayout = findViewById(R.id.LinearLayout6);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView6);
                        } else if (hour >= 12 && hour < 14) {
                            linearLayout = findViewById(R.id.LinearLayout7);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView7);
                        } else if (hour >= 14 && hour < 16) {
                            linearLayout = findViewById(R.id.LinearLayout8);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView8);
                        } else if (hour >= 16 && hour < 18) {
                            linearLayout = findViewById(R.id.LinearLayout9);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView9);
                        } else if (hour >= 18 && hour < 20) {
                            linearLayout = findViewById(R.id.LinearLayout10);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView10);
                        } else if (hour >= 20 && hour < 22) {
                            linearLayout = findViewById(R.id.LinearLayout11);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView11);
                        } else {
                            linearLayout = findViewById(R.id.LinearLayout12);
                            humidityImageView = linearLayout.findViewById(R.id.humidityImageView12);
                        }


                        if (roundedClouds <= 10) {
                            humidityImageView.setImageResource(R.drawable.solnechno);
                        } else if (roundedClouds <= 20) {
                            humidityImageView.setImageResource(R.drawable.zatyanyto);
                        } else if (roundedClouds <= 30) {
                            humidityImageView.setImageResource(R.drawable.doshdlivo);
                        } else {
                            humidityImageView.setImageResource(R.drawable.liveni);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

        private class GetSunriseSunsetData extends AsyncTask<String, Void, String> {
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
                    if (jsonObject.has("sys")) {
                        JSONObject sys = jsonObject.getJSONObject("sys");
                        long sunriseUnix = sys.getLong("sunrise");
                        long sunsetUnix = sys.getLong("sunset");

                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        Date sunriseDate = new Date(sunriseUnix * 1000);
                        Date sunsetDate = new Date(sunsetUnix * 1000);

                        String sunriseTime = dateFormat.format(sunriseDate);
                        String sunsetTime = dateFormat.format(sunsetDate);

                        String sunTime = "Время восхода: " + sunriseTime + "\nВремя заката: " + sunsetTime;
                        SunnyText.setText(sunTime);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка: Некорректные данные о времени восхода и заката", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

