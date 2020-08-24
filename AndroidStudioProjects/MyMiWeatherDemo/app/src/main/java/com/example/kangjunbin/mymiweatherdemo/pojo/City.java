package com.example.kangjunbin.mymiweatherdemo.pojo;

/**
 * Created by kangjunbin on 2020/8/1.
 */

public class City {

    private String name;
    private String location;

    public City(String name,String location){
        this.name=name;
        this.location=location;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
