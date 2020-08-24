package com.example.kangjunbin.mymiweatherdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.kangjunbin.mymiweatherdemo.R;
import com.example.kangjunbin.mymiweatherdemo.db.HistoryCity;
import com.example.kangjunbin.mymiweatherdemo.pojo.City;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by kangjunbin on 2020/7/31.
 */

public class SearchFragment extends Fragment {

    private ListView listView;
    private List<City> cityList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.city_search_result,container,false);
        listView=(ListView)view.findViewById(R.id.searched_city);

        return view;
    }
    public void refresh(List<City> results){
        cityList=results;
        CityAdapter adapter=new CityAdapter(getActivity(),R.layout.city_item_layout,cityList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                City city=cityList.get(i);
                HistoryCity historyCity = new HistoryCity();
                historyCity.setCityName(city.getName().split(" ")[0].split("-")[0]);
                historyCity.setCityLocation(city.getLocation());
                historyCity.setTimestamp(System.currentTimeMillis());
                historyCity.saveOrUpdate("cityName=?",city.getName().split(" ")[0].split("-")[0]);
                if(LitePal.count("HistoryCity")>10){
                    LitePal.delete(HistoryCity.class,LitePal.order("timestamp asc").limit(1).find(HistoryCity.class).get(0).getId());
                }
                Intent intent=new Intent(getActivity(),MainActivity.class);
                intent.putExtra("location",city.getLocation());
                intent.putExtra("address",city.getName());
                startActivity(intent);
            }
        });
    }
}
