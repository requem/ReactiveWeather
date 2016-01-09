package com.requem.reactiveweather.ui.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.requem.reactiveweather.BuildConfig;
import com.requem.reactiveweather.R;
import com.requem.reactiveweather.ReactiveWeatherApplication;
import com.requem.reactiveweather.data.weather.api.OpenWeatherApi;
import com.requem.reactiveweather.data.weather.model.CurrentForecast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.edit_text_city)
    EditText mCityEditText;

    @Bind(R.id.text_city_name)
    TextView mCityNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @SuppressWarnings("unused")
    @OnEditorAction(R.id.edit_text_city)
    public boolean onEditorActionCityEditText(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchCurrentWeather(mCityEditText.getText().toString());
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.button_search)
    public void onClickSearchButton() {
        searchCurrentWeather(mCityEditText.getText().toString());
    }

    private void searchCurrentWeather(String city) {
        OpenWeatherApi api = ReactiveWeatherApplication.get(this).getOpenWeatherApi();
        api.getCurrentWeather(city, BuildConfig.WEATHER_APP_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CurrentForecast>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "complete!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "error!", e);
                    }

                    @Override
                    public void onNext(CurrentForecast weather) {
                        Log.d(TAG, weather.toString());
                        mCityNameTextView.setText(getString(R.string.text_city_country, weather.city, weather.country.country));
                        Glide.with(MainActivity.this)
                                .load("http://openweathermap.org/img/w/" + weather.weatherList.get(0).icon + ".png")
                                .into((ImageView) findViewById(R.id.image_weather_icon));
                    }
                });
    }
}
