package com.example.kangjunbin.mymiweatherdemo;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherDailyBean;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

/**
 * Created by kangjunbin on 2020/8/2.
 */

public class FifteenActivity extends AppCompatActivity{
    private List<WeatherDailyBean.DailyBean> mWeahterBean;
    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;
    private TextView back;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fifteen_weather_layout);
        Intent intent=getIntent();
        back=findViewById(R.id.fifteen_back);
        String location=intent.getStringExtra("location");
        recyclerView=(RecyclerView)findViewById(R.id.fifteen_all);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        HeWeather.getWeather15D(FifteenActivity.this, location,new HeWeather.OnResultWeatherDailyListener(){
            @Override
            public void onError(Throwable throwable) {
                Log.i("Error","get15weather onError" + throwable);
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
               mWeahterBean=weatherDailyBean.getDaily();
               weatherAdapter=new WeatherAdapter(mWeahterBean);
               recyclerView.setAdapter(weatherAdapter);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
