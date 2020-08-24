package com.example.kangjunbin.mymiweatherdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.kangjunbin.mymiweatherdemo.util.ImageUtil;
import com.example.kangjunbin.mymiweatherdemo.util.LocationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import interfaces.heweather.com.interfacesmodule.bean.IndicesBean;
import interfaces.heweather.com.interfacesmodule.bean.MinutelyBean;
import interfaces.heweather.com.interfacesmodule.bean.air.AirDailyBean;
import interfaces.heweather.com.interfacesmodule.bean.base.Code;
import interfaces.heweather.com.interfacesmodule.bean.base.IndicesType;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherDailyBean;
import interfaces.heweather.com.interfacesmodule.bean.weather.WeatherNowBean;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.baidu.location.e.l.I;
import static com.baidu.location.e.l.M;
import static com.baidu.location.e.l.b;
import static com.baidu.location.e.l.e;
import static com.baidu.location.e.l.i;
import static com.baidu.location.e.l.t;
import static interfaces.heweather.com.interfacesmodule.bean.base.Lang.ZH_HANS;

public class MainActivity extends AppCompatActivity {

    //设置温度符号和圆圈符号
    public static final String tem_symbol="\u2103";
    public static final char circle_symbol=0x00B0;

    private LocationClient mLocationClient;
    private String[] locations;
    private TextView addr_textView;
    private TextView temperature_view;
    private TextView today_describe;
    private TextView today_weather;
    private TextView tomorrow_weather;
    private TextView day_after_day_weather;
    private TextView air_number;
    private TextView today_aiq;
    private TextView tomorrow_aiq;
    private TextView day_after_aiq;
    private TextView today_max_min;
    private TextView tomorrow_max_min;
    private TextView day_after_max_min;
    private TextView if_rain_next;
    private TextView wind_level;
    private TextView wet_percent;
    private TextView body_temprate;
    private TextView air_pressure;
    private TextView wind_direct;
    private String minuteRain;
    private TextView life_carw;
    private TextView life_sun;
    private TextView life_sport;
    private TextView life_cloth;
    private TextView life_fish;
    private TextView sunRise;
    private TextView sunLeft;
    //定义图片控件
    private ImageView today_image;
    private ImageView tomorrow_image;
    private ImageView day_afer_image;
    private ImageView add_city_image;
    private SunView   sv;
    //定义布局控件
    ConstraintLayout fifteen_detail;
    NestedScrollView nestedScrollView;
    //定义toolbar控件
    private Toolbar toolbar;
    //定义PopUpWindow
    private ImageView popWindow;
    private PopupWindow mPopUpWindow;
   //定义http3 client
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .build();
    //定义下拉刷新控件
     private SwipeRefreshLayout weather_fresh;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locations=new String[2];
        //初始化和风天气SDK
        HeConfig.init("HE2007271513151648", "f3dc2b1714fa4ebabaed3af7c2c029b1");
        HeConfig.switchToDevService();
        //设置百度定位
        mLocationClient = new LocationClient(MainActivity.this);
        mLocationClient.registerLocationListener(new MyLocationListener());
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setNeedNewVersionRgc(true);
        mLocationClient.setLocOption(option);
        //获取文本控件
        addr_textView=findViewById(R.id.address);
        temperature_view=findViewById(R.id.index_temperate);
        today_describe=findViewById(R.id.cloud);
        air_number=findViewById(R.id.air);
        if_rain_next=findViewById(R.id.if_rain);
        today_weather=findViewById(R.id.today_weather_text);
        today_max_min=findViewById(R.id.today_max_min);
        today_aiq=findViewById(R.id.today_air_all);
        tomorrow_weather=findViewById(R.id.tomorrow_weather_text);
        tomorrow_max_min=findViewById(R.id.tomorrow_max_min);
        tomorrow_aiq=findViewById(R.id.tomorrow_air_all);
        day_after_day_weather=findViewById(R.id.day_after_weather_text);
        day_after_aiq=findViewById(R.id.day_after_air_all);
        day_after_max_min=findViewById(R.id.day_after_max_min);
        wind_level=findViewById(R.id.wind_level);
        wind_direct=findViewById(R.id.wind_level_direct);
        body_temprate=findViewById(R.id.body_temperate);
        wet_percent=findViewById(R.id.wet_percent);
        air_pressure=findViewById(R.id.air_press);
        life_carw=findViewById(R.id.wash);
        life_cloth=findViewById(R.id.cloth);
        life_sun=findViewById(R.id.sunshine);
        life_sport=findViewById(R.id.sport);
        life_fish=findViewById(R.id.umbrella);
       // sunRise=findViewById(R.id.rise_time);
      //  sunLeft=findViewById(R.id.down_time);

        //获取图片控件
         today_image=findViewById(R.id.image_weather_today);
         tomorrow_image=findViewById(R.id.image_weather_tomorrow);
         day_afer_image=findViewById(R.id.image_weather_day_after);
         add_city_image=findViewById(R.id.add_city);
         toolbar=findViewById(R.id.tool_bar);
         popWindow=findViewById(R.id.menu_right);
         setSupportActionBar(toolbar);
         sv=findViewById(R.id.sun_image);
         //获取布局控件
        fifteen_detail=findViewById(R.id.fifteen_weather);
        nestedScrollView=findViewById(R.id.index_layout_scroll);
        //获取下拉刷新控件
         weather_fresh=findViewById(R.id.weather_refresh);
        //权限控制
        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION
        )!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }
        else {
            Intent intent=getIntent();
            if(intent.getStringExtra("location")!=null&&intent.getStringExtra("address")!=null){
                //设置显示当前地址
                //设置显示当前地址
                locations[0]=intent.getStringExtra("location");
                locations[1]=intent.getStringExtra("address");
                addr_textView.setText(locations[1]);
                //显示天气信息
                getDataAndSetUI(locations[0]);
            }
            else {
                mLocationClient.start();
            }
        }
        //跳转到选择城市页面
        add_city_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,CityActivity.class);
                intent.putExtra("location",locations[0]);
                intent.putExtra("address",locations[1]);
                startActivity(intent);
            }
        });
       // 设置下拉刷新
        weather_fresh.setColorSchemeColors(R.color.colorAccent);
        weather_fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLocationClient.restart();
                                mLocationClient.requestLocation();
                                weather_fresh.setRefreshing(false);
                            }
                        });
                    }
                }).start();
            }
        });
        //跳转到15天天气预报画面
        fifteen_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,FifteenActivity.class);
                intent.putExtra("location",locations[0]);
                startActivity(intent);
            }
        });
        //点击右侧菜单键进行刷新或分享
        popWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View contentView= LayoutInflater.from(MainActivity.this).inflate(R.layout.right_menu_window,null);
                mPopUpWindow=new PopupWindow(contentView);
                mPopUpWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                mPopUpWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

                ConstraintLayout refresh=contentView.findViewById(R.id.refresh_all);
                ConstraintLayout share=contentView.findViewById(R.id.share_all);
                ConstraintLayout pop_all=contentView.findViewById(R.id.popWind_all);

                refresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLocationClient.restart();
                        mLocationClient.requestLocation();
                        weather_fresh.setRefreshing(false);
                        mPopUpWindow.dismiss();
                    }
                });
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      mPopUpWindow.dismiss();
                      int h1=0,h2=0;
                      Bitmap bitmap=null,bitmap1=null,bitmap2=null;
                     //绘制标题栏
                      h1=toolbar.getHeight();
                      bitmap1=Bitmap.createBitmap(toolbar.getWidth(),h1,Bitmap.Config.RGB_565);
                      final Canvas canvas1=new Canvas(bitmap1);
                      toolbar.draw(canvas1);
                      //绘制NestedScrollerView
                       for(int i=0;i<nestedScrollView.getChildCount();i++) {
                           h2 += nestedScrollView.getChildAt(i).getHeight();
                       }
                        bitmap2=Bitmap.createBitmap(nestedScrollView.getWidth(),h2,Bitmap.Config.RGB_565);
                        final Canvas canvas2=new Canvas(bitmap2);
                        nestedScrollView.draw(canvas2);
                        bitmap=Bitmap.createBitmap(nestedScrollView.getWidth(),h1+h2,Bitmap.Config.RGB_565);
                        final Canvas canvas=new Canvas(bitmap);
                        canvas.drawBitmap(bitmap1,0,0,null);
                        canvas.drawBitmap(bitmap2,0,h1,null);

                         //保存图片
                        File appDir = new File(Environment.getExternalStorageDirectory(), "image");
                        if (!appDir.exists()) {
                            appDir.mkdir();
                        }
                        String fileName = System.currentTimeMillis() + ".jpg";
                        File file = new File(appDir, fileName);
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // 其次把文件插入到系统图库
                        try {
                            MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(),
                                    file.getAbsolutePath(), fileName, null);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        // 通知图库更新
                        String path = appDir + fileName;
                        MainActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));

                       //通过其他应用进行分享
                      Intent imageIntent = new Intent(Intent.ACTION_SEND);
                      //由文件得到uri
                      Uri uri = Uri.fromFile(file);
                      imageIntent.putExtra(Intent.EXTRA_STREAM, uri);
                      imageIntent.setType("image/*");
                      startActivity(Intent.createChooser(imageIntent, "分享到"));
                    }
                });

                pop_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopUpWindow.dismiss();
                    }
                });
                mPopUpWindow.showAsDropDown(popWindow);
            }
        });
        //防止分享时发生崩溃
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.detectFileUriExposure();
        }
//        //绘制日升日落曲线
//        sv.setSunrise(05, 39);
//        // 设置日落时间
//        sv.setSunset(18, 48);
//        // 获取系统 时 分
//        Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//        // 设置当前时间
//        sv.setCurrentTime(hour, minute);
    }


    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append(bdLocation.getLongitude()).append(",");
            currentPosition.append(bdLocation.getLatitude());
            MainActivity.this.locations[0] = currentPosition.toString();
            String district = bdLocation.getDistrict();    //获取区县
            String street = bdLocation.getStreet(); //获取街道
            MainActivity.this.locations[1] = district + " " + street;
            //设置显示当前地址
            if(district==null||street==null) addr_textView.setText("请连接网络后重试");
            else addr_textView.setText(locations[1]);
            //显示天气信息
            getDataAndSetUI(locations[0]);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case  1:
                if(grantResults.length>0){
                    for(int result :grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    mLocationClient.start();
                }
                else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

    }
    /*
    调用和风天气API获取天气数据并将其渲染到页面
     */
    public void getDataAndSetUI(String location){
        //调用API获取实时天气状况
        HeWeather.getWeatherNow(MainActivity.this,location,new HeWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable e) {
                Log.i("Error", "getWeather onError: " + e);
            }

            @Override
            public void onSuccess(WeatherNowBean weatherBean) {

                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(weatherBean.getCode())) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                    temperature_view.setText(now.getTemp()+tem_symbol);
                    today_describe.setText(now.getText());
                    wind_level.setText(now.getWindScale()+"级");
                    wind_direct.setText(now.getWindDir());
                    body_temprate.setText(now.getFeelsLike()+ tem_symbol);
                    air_pressure.setText(now.getPressure()+"mb");
                    wet_percent.setText(now.getHumidity()+"%");
                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i("Error", "failed code: " + code);
                }
            }
        });
        HeWeather.getWeather3D(MainActivity.this,location,new HeWeather.OnResultWeatherDailyListener(){
            @Override
            public void onError(Throwable e) {
                Log.i("Error", "getWeather onError: " + e);
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {

                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(weatherDailyBean.getCode())) {
                    List<WeatherDailyBean.DailyBean> dailyBean = weatherDailyBean.getDaily();
                    WeatherDailyBean.DailyBean todayWeather=dailyBean.get(0),tomorrowWeather=dailyBean.get(1),dayAfterWeather=dailyBean.get(2);
                    //三天天气预报
                    today_weather.setText("今天  "+todayWeather.getTextDay());
                    today_image.setImageBitmap(ImageUtil.getImageFromAssetsFile(MainActivity.this,todayWeather.getIconDay()+".png"));
//                    sunRise.setText("日出"+" "+todayWeather.getSunrise());
//                    sunLeft.setText("日落"+" "+todayWeather.getSunset());

                    String[] sunrise=todayWeather.getSunrise().split(":");
                    String[] sunset=todayWeather.getSunset().split(":");

                    //绘制日升日落曲线
                    sv.setSunrise(Integer.parseInt(sunrise[0]), Integer.parseInt(sunrise[1]));
                    // 设置日落时间
                    sv.setSunset(Integer.parseInt(sunset[0]), Integer.parseInt(sunset[1]));
                    // 获取系统 时 分
                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    // 设置当前时间
                    sv.setCurrentTime(hour, minute);
                    tomorrow_weather.setText("明天  "+tomorrowWeather.getTextDay());
                    tomorrow_image.setImageBitmap(ImageUtil.getImageFromAssetsFile(MainActivity.this,tomorrowWeather.getIconDay()+".png"));
                    day_after_day_weather.setText("后天  "+dayAfterWeather.getTextDay());
                    day_afer_image.setImageBitmap(ImageUtil.getImageFromAssetsFile(MainActivity.this,dayAfterWeather.getIconDay()+".png"));
                    today_max_min.setText(todayWeather.getTempMax()+circle_symbol +"/"+todayWeather.getTempMin()+circle_symbol);
                    tomorrow_max_min.setText(tomorrowWeather.getTempMax()+circle_symbol +"/"+tomorrowWeather.getTempMin()+circle_symbol);
                    day_after_max_min.setText(dayAfterWeather.getTempMax()+circle_symbol +"/"+dayAfterWeather.getTempMin()+circle_symbol);

                } else {
                    //在此查看返回数据失败的原因
                    String status = weatherDailyBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i("Error", "failed code: " + code);
                }
            }
        });
        //获取分钟级降水
        Request request=new Request.Builder()
                .get()
                .url("https://devapi.heweather.net/v7/minutely/5m?location="+location+"&"+"key=d0592e435607447590e92c27c2571f3d")
                .build();
        Call call=OK_HTTP_CLIENT.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Failure", "onFailure: "+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                     String responseData=response.body().string();
                    JSONObject jsonObject=new JSONObject(responseData);
                    minuteRain=jsonObject.getString("summary");
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if_rain_next.setText(minuteRain.split("，")[0]);
                        }
                    });
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });

        //获取空气质量
        HeWeather.getAir5D(MainActivity.this,location,ZH_HANS,new HeWeather.OnResultAirDailyListener(){

            @Override
            public void onError(Throwable throwable) {
                Log.i("Error","getAIQ onError" + throwable);
            }

            @Override
            public void onSuccess(AirDailyBean airDailyBean) {
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(airDailyBean.getCode())) {
                    List<AirDailyBean.DailyBean> airList=airDailyBean.getAirDaily();
                    air_number.setText("空气"+ airList.get(0).getCategory()+" "+airList.get(0).getAqi());
                    today_aiq.setText(airList.get(0).getCategory());
                    tomorrow_aiq.setText(airList.get(1).getCategory());
                    day_after_aiq.setText(airList.get(2).getCategory());
                } else {
                    //在此查看返回数据失败的原因
                    String status = airDailyBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i("Error", "failed code: " + code);
                }
            }
        });

        //获取生活指数
        List<IndicesType> types=new ArrayList<>();
        types.add(IndicesType.DRSG);
        types.add(IndicesType.SPI);
        types.add(IndicesType.SPT);
        types.add(IndicesType.CW);
        types.add(IndicesType.FIS);
        HeWeather.getIndices1D(MainActivity.this,location,ZH_HANS,types,new HeWeather.OnResultIndicesListener(){

            @Override
            public void onError(Throwable throwable) {
                Log.i("Error","getIndices onError" + throwable);
            }

            @Override
            public void onSuccess(IndicesBean indicesBean) {
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK.getCode().equalsIgnoreCase(indicesBean.getCode())) {
                    List<IndicesBean.DailyBean> indicesList=indicesBean.getDailyList();
                    life_carw.setText(indicesList.get(0).getCategory());
                    life_cloth.setText(indicesList.get(1).getCategory());
                    life_sun.setText(indicesList.get(2).getCategory());
                    life_sport.setText(indicesList.get(3).getCategory());
                    life_fish.setText(indicesList.get(4).getCategory());
                } else {
                    //在此查看返回数据失败的原因
                    String status = indicesBean.getCode();
                    Code code = Code.toEnum(status);
                    Log.i("Error", "failed code: " + code);
                }

            }
        });

    }

}
