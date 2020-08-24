package com.example.kangjunbin.mymiweatherdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kangjunbin.mymiweatherdemo.util.ImageUtil;

import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherDailyBean;

/**
 * Created by kangjunbin on 2020/8/2.
 */

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder>{
    private List<WeatherDailyBean.DailyBean> mWeahterBean;
    private Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fifteen_item,viewGroup,false);
        ViewHolder holder=new ViewHolder(view);
        context=viewGroup.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        WeatherDailyBean.DailyBean dailyBean=mWeahterBean.get(i);
        viewHolder.day_temp.setText(dailyBean.getTempMax()+MainActivity.tem_symbol);
        String[] date_form=dailyBean.getFxDate().split("-");
        viewHolder.date.setText(date_form[1]+"月"+date_form[2]+"日");
        viewHolder.day_conclude.setText(dailyBean.getTextDay());
        viewHolder.night_temp.setText(dailyBean.getTempMin()+MainActivity.tem_symbol);
        viewHolder.night_conclude.setText(dailyBean.getTextNight());
        viewHolder.day_image.setImageBitmap(ImageUtil.getImageFromAssetsFile(context,dailyBean.getIconDay()+".png"));
        viewHolder.night_image.setImageBitmap(ImageUtil.getImageFromAssetsFile(context,dailyBean.getIconNight()+".png"));
    }

    @Override
    public int getItemCount() {
        return mWeahterBean.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView date;
        TextView day_conclude;
        TextView day_temp;
        TextView night_conclude;
        TextView night_temp;
        ImageView day_image;
        ImageView night_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            day_conclude=itemView.findViewById(R.id.conclude);
            day_temp=itemView.findViewById(R.id.day_temp);
            night_temp=itemView.findViewById(R.id.night_temp);
            day_image=itemView.findViewById(R.id.weather_image);
            night_conclude=itemView.findViewById(R.id.night_conclude);
            night_image=itemView.findViewById(R.id.night_image);
        }
    }
    public WeatherAdapter(List<WeatherDailyBean.DailyBean> dailyBeans){
        mWeahterBean=dailyBeans;
    }
}
