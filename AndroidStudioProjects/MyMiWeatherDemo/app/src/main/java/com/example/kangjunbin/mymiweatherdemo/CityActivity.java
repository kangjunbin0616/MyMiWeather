package com.example.kangjunbin.mymiweatherdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.kangjunbin.mymiweatherdemo.pojo.City;
import com.google.android.flexbox.FlexboxLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.base.Mode;
import interfaces.heweather.com.interfacesmodule.bean.geo.GeoBean;

import interfaces.heweather.com.interfacesmodule.bean.base.Lang;
import interfaces.heweather.com.interfacesmodule.bean.base.Range;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

/**
 * Created by kangjunbin on 2020/7/30.
 */

public class CityActivity extends AppCompatActivity {

    private FragmentManager fragmentManager=getSupportFragmentManager();
    private EditText search_city;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.city_layout);


        search_city=(EditText)findViewById(R.id.search);
        search_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Fragment flag=fragmentManager.findFragmentById(R.id.city_body_frame);
                if(flag instanceof CityFragment) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.city_body_frame, new SearchFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                String cityName=editable.toString();
                HeWeather.getGeoCityLookup(CityActivity.this, cityName, Mode.FUZZY, Range.CN, 20, Lang.ZH_HANS,new HeWeather.OnResultGeoListener(){

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d("Error", "getGeo onError: " + throwable);
                    }

                    @Override
                    public void onSuccess(GeoBean geoBean) {
                        List<GeoBean.LocationBean> list=geoBean.getLocationBean();
                        List<City> results=new ArrayList<>();
                        for(int i=0;i<list.size();i++){
                            String s=list.get(i).getName();
                            if(!s.contains(list.get(i).getAdm2())) s=s+"-"+list.get(i).getAdm2();
                            if(!s.contains(list.get(i).getAdm1())) s=s+" "+list.get(i).getAdm1();
                            String location=list.get(i).getLon()+","+list.get(i).getLat();
                            results.add(new City(s,location));
                        }
                        SearchFragment searchFragment=(SearchFragment)getSupportFragmentManager().findFragmentById(R.id.city_body_frame);
                        searchFragment.refresh(results);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //加载热门城市和历史查询
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.city_body_frame,new CityFragment());
        transaction.commit();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
