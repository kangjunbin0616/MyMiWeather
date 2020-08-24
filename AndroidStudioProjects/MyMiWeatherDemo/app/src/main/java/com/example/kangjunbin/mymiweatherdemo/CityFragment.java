package com.example.kangjunbin.mymiweatherdemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.kangjunbin.mymiweatherdemo.db.HistoryCity;
import com.google.android.flexbox.FlexboxLayout;

import org.litepal.LitePal;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.base.Lang;
import interfaces.heweather.com.interfacesmodule.bean.base.Range;
import interfaces.heweather.com.interfacesmodule.bean.geo.GeoBean;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

import static com.example.kangjunbin.mymiweatherdemo.R.drawable.botton_board;

/**
 * Created by kangjunbin on 2020/7/30.
 */

public class CityFragment extends Fragment {
    FlexboxLayout hot_city_layout;
    FlexboxLayout history_city_layout;
    private ImageView currentLocation;
    private List<String> hot_city_ll=new ArrayList<>();
    private List<String> hot_city_name=new ArrayList<>();
    private List<String> history_city_ll=new ArrayList<>();
    private List<String> history_city_name=new ArrayList<>();
    private String[] currentLocations=new String[2];
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.hot_history_city,container,false);
        hot_city_layout=(FlexboxLayout)view.findViewById(R.id.all_hot_city);
        history_city_layout=(FlexboxLayout)view.findViewById(R.id.all_history_city);
        currentLocation=(ImageView)view.findViewById(R.id.current_location);
        currentLocations[0]=getActivity().getIntent().getStringExtra("location");
        currentLocations[1]=getActivity().getIntent().getStringExtra("address");
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),MainActivity.class);
                intent.putExtra("location",currentLocations[0]);
                intent.putExtra("address",currentLocations[1]);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //查询热门城市并渲染到页面
        HeWeather.getGeoTopCity(getActivity(), 20, Range.CN, Lang.ZH_HANS, new HeWeather.OnResultGeoListener(){

            @Override
            public void onError(Throwable e) {
                Log.i("Error", "getWeather onError: " + e);
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(geoBean.getStatus())) {
                    List<GeoBean.LocationBean> cities=geoBean.getLocationBean();
                    for(int i=0;i<cities.size();i++){
                        final Button hot_city=new Button(getActivity());
                        hot_city.setId(i);
                        hot_city_ll.add(i,cities.get(i).getLon()+","+cities.get(i).getLat());
                        hot_city_name.add(i,cities.get(i).getName());
                        hot_city.setText(cities.get(i).getName());
                        hot_city.setTextColor(getResources().getColor(R.color.colorblack));
                        hot_city.setBackgroundResource(R.drawable.botton_board);
                        hot_city.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                HistoryCity historyCity=new HistoryCity();
                                historyCity.setCityName(hot_city_name.get(view.getId()));
                                historyCity.setCityLocation(hot_city_ll.get(view.getId()));
                                historyCity.setTimestamp(System.currentTimeMillis());
                                historyCity.saveOrUpdate("cityName=?",hot_city_name.get(view.getId()));
                                if(LitePal.count("HistoryCity")>10){
                                    LitePal.delete(HistoryCity.class,LitePal.order("timestamp asc").limit(1).find(HistoryCity.class).get(0).getId());
                                }
                                Intent intent=new Intent(getActivity(),MainActivity.class);
                                intent.putExtra("location",hot_city_ll.get(view.getId()));
                                intent.putExtra("address",hot_city_name.get(view.getId()));
                                startActivity(intent);
                            }
                        });
                        FlexboxLayout.LayoutParams flp=new FlexboxLayout.LayoutParams( FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT );
                        flp.setMargins(5,2,5,2);
                        hot_city.setLayoutParams(flp);
                        hot_city_layout.addView(hot_city);

                    }
                } else {
                    //在此查看返回数据失败的原因
                    String status = geoBean.getStatus();
                    Code code = Code.toEnum(status);
                    Log.i("Error", "failed code: " + code);
                }
            }
        });
        //从数据库中查询历史记录并显示
        List<HistoryCity> historyCityList= LitePal.select("cityName","cityLocation")
                .order("timestamp desc")
                .limit(15)
                .find(HistoryCity.class);
        if(historyCityList.size()>0) {
            for (int i = 0; i<historyCityList.size(); i++) {
                final Button history_city = new Button(getActivity());
                history_city.setId(i);
                history_city_ll.add(i, historyCityList.get(i).getCityLocation());
                history_city_name.add(i, historyCityList.get(i).getCityName());
                history_city.setText(historyCityList.get(i).getCityName());
                history_city.setTextColor(getResources().getColor(R.color.colorblack));
                history_city.setBackgroundResource(R.drawable.botton_board);
                history_city.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("location", history_city_ll.get(view.getId()));
                        intent.putExtra("address", history_city_name.get(view.getId()));
                        startActivity(intent);
                    }
                });
                FlexboxLayout.LayoutParams flp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT);
                flp.setMargins(5, 2, 5, 2);
                history_city.setLayoutParams(flp);
                history_city_layout.addView(history_city);
            }
        }
    }
}
