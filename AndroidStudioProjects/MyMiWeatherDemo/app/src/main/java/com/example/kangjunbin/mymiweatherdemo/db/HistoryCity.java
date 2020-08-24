package com.example.kangjunbin.mymiweatherdemo.db;

import org.litepal.crud.LitePalSupport;

import java.sql.Timestamp;

/**
 * Created by kangjunbin on 2020/7/30.
 */

public class HistoryCity extends LitePalSupport{
    private int id;
    private String cityName;
    private String cityLocation;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityLocation() {
        return cityLocation;
    }

    public void setCityLocation(String cityCode) {
        this.cityLocation = cityCode;
    }
}
