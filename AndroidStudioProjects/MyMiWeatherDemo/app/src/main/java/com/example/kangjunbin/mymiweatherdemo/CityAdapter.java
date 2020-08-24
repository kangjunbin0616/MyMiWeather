package com.example.kangjunbin.mymiweatherdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kangjunbin.mymiweatherdemo.pojo.City;

import java.util.List;

/**
 * Created by kangjunbin on 2020/8/1.
 */

public class CityAdapter extends ArrayAdapter<City> {
    private int resouceId;
    public CityAdapter(@NonNull Context context, int resource, @NonNull List<City> objects) {
        super(context, resource, objects);
        resouceId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        City city=getItem(position);
        View view;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resouceId,parent,false);
        }else {
            view=convertView;
        }
        TextView cityName=(TextView)view.findViewById(R.id.city_single);
        cityName.setText(city.getName());
        return view;
    }
}
