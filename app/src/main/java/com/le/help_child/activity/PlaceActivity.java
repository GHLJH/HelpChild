package com.le.help_child.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.le.help_child.R;
import com.le.help_child.bean.ChildBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class PlaceActivity extends Fragment implements View.OnClickListener,AMap.OnMarkerDragListener,
        AMap.OnMapLoadedListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter {
    private MapView mapView;
    private AMap aMap;

    public static final LatLng DONG = new LatLng(48.428702,135.106271);// 北京市经纬度
    public static final LatLng XI = new LatLng(39.178862, 73.4072);// 北京市中关村经纬度
    public  String IP ="http://123.57.249.60:8080";// 服务器IP地址


    private double longt;//经度
    private double lant;//纬度
    private String pname;//照片名
    private String response;
    private LatLng latlng ;
    private Bundle ss;


    JSONArray jsonhelp = null;
    JSONArray jsonrecord = null;
    private String hname;//help图片名
    private String rname;//record图片名
    private double hlongt;//help经度
    private double hlant;//help纬度
    private double rlongt;//record经度
    private double rlant;//record纬度
    private String htime;//help时间
    private String rcname;//record失踪人员姓名
    private String rpname;//record家属性名
    private String raddress;//record失踪地点
    private String rdetail;//record失踪人员细节
    RadioGroup radiogroup;
    RadioButton radio1,radio2,radio3;//图片显示时间范围
    public static String ShowTime = null;//已选择图片显示时间范围
    public static final int SHOW_RESPONSE = 0;

    private String str;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private Double myLon = 110.222;
    private Double myLat = 30.222;
    private Boolean firstLocation = true;
    private ImageView iv_location;
    private String user_address="";

    private TextView tv_go_site,tv_go_day;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建默认的ImageLoader配置参数??
        ImageLoaderConfiguration configuration=ImageLoaderConfiguration.createDefault(this.getContext());
        //Initialize?ImageLoader?with?configuration.??
        ImageLoader.getInstance().init(configuration);
        initLocation();
        startLocation();
    }
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(getActivity().getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    // 根据控件的选择，重新设置定位参数
    private void resetOption() {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption.setGpsFirst(true);
        // 设置是否开启缓存
        locationOption.setLocationCacheEnable(false);
        // 设置是否单次定位
        locationOption.setOnceLocation(false);
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
        locationOption.setOnceLocationLatest(false);
        //设置是否使用传感器
        locationOption.setSensorEnable(true);

    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        //根据控件的选择，重新设置定位参数
        resetOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ss = savedInstanceState;
        return inflater.inflate(R.layout.activity_place, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView = (MapView)this.getView().findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        ShareSDK.initSDK(this.getContext());//分享功能ShareSDK初始化
        //图片显示时间范围
        radiogroup=(RadioGroup)this.getView().findViewById(R.id.radiogroup1);
        iv_location = (ImageView) this.getView().findViewById(R.id.iv_location);
        radio1=(RadioButton)this.getView().findViewById(R.id.RadioWeek);
        radio2=(RadioButton)this.getView().findViewById(R.id.RadioMonth);
        radio3=(RadioButton)this.getView().findViewById(R.id.RadioYear);
//        基础地图和卫星地图变换
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        tv_go_day=(TextView) this.getView().findViewById(R.id.tv_go_day);
        tv_go_site=(TextView) this.getView().findViewById(R.id.tv_go_site);
        tv_go_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_go_site.setVisibility(View.VISIBLE);
                tv_go_day.setVisibility(View.GONE);
                if (aMap == null) {
                    aMap = mapView.getMap();
                }
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            }
        });
        tv_go_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_go_day.setVisibility(View.VISIBLE);
                tv_go_site.setVisibility(View.GONE);
                if (aMap == null) {
                    aMap = mapView.getMap();
                }
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
            }
        });


        ShowTime="1";//默认一周
        firststart();//显示图片
        iv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aMap == null) {
                    aMap = mapView.getMap();
                }
                //窗口显示地图范围
                LatLng marker1 = new LatLng(myLat, myLon);
                LatLngBounds bounds = new LatLngBounds.Builder().include(marker1).build();
                aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
            }
        });
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==radio2.getId()){
                    ShowTime="2";
                    aMap.clear();
                    firstLocation = true;
                    firststart();
                }else if (checkedId==radio3.getId()){
                    ShowTime="3";
                    aMap.clear();
                    firstLocation = true;
                    firststart();
                }else {
                    ShowTime="1";
                    aMap.clear();
                    firstLocation = true;
                    firststart();
                }
            }
        });



    }

    //连接服务器传送数据
    public void firststart(){
        //新建Handler的对象，在这里接收Message，然后更新TextView控件的内容
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SHOW_RESPONSE:
                        response = (String) msg.obj;
                        //textView_response.setText(response);
                        JSONTokener jsonParser = new JSONTokener(response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = (JSONObject) jsonParser.nextValue();
                            jsonhelp = jsonObject.getJSONArray("help");
                            jsonrecord = jsonObject.getJSONArray("record");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(jsonhelp==null && jsonrecord==null){
                            init1();
                        }else{
                            init();
                        }

                        break;
                    case 148:
                        Toast.makeText(getActivity(), "服务器连接失败！请稍后重试", Toast.LENGTH_SHORT).show();
                        init1();
                        break;
                    default:
                        break;
                }
            }

        };

        //方法：发送网络请求，获取百度首页的数据。在里面开启线程

        new Thread(new Runnable() {

            @Override
            public void run() {
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象
                HttpClient httpCient = new DefaultHttpClient();
                //第二步：创建代表请求的对象,参数是访问的服务器地址
//                HttpGet httpGet = new HttpGet("http://www.baidu.com");123.57.249.60
                  HttpGet httpGet = new HttpGet(IP+"/help_child_t1/childservice?method=place&choosetime="+ShowTime);//01


                try {
                    //第三步：执行请求，获取服务器发还的相应对象
                    HttpResponse httpResponse = httpCient.execute(httpGet);
                    //第四步：检查相应的状态是否正常：检查状态码的值是200表示正常
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        //第五步：从相应对象当中取出数据，放到entity当中
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串

                        //在子线程中将Message对象发出去
                        Message message = new Message();
                        message.what = SHOW_RESPONSE;
                        message.obj = response.toString();
                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    //在子线程中将Message对象发出去
                    Message message = new Message();
                    message.what = 148;
                    handler.sendMessage(message);
                }

            }
        }).start();//这个start()方法不要忘记了
    }
    //显示地图并显示头像图标
    public void init() {

        if (aMap == null) {
            aMap = mapView.getMap();
            //窗口显示地图范围
//            LatLng marker1 = new LatLng(34.48995,108.597783);
            LatLng marker1 = new LatLng(myLat, myLon);
            LatLngBounds bounds = new LatLngBounds.Builder().include(marker1).include(DONG).include(XI).build();
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
        }

        setUpMap();//显示头像标记


    }
    //未获取返回值只显示地图
    public void init1() {
        if (aMap == null) {
            aMap = mapView.getMap();
            //窗口显示地图范围
//            LatLng marker1 = new LatLng(34.48995,108.597783);
            LatLng marker1 = new LatLng(myLat, myLon);
            LatLngBounds bounds = new LatLngBounds.Builder().include(marker1).include(DONG).include(XI).build();
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
        }
    }
    //显示头像标记
    private void setUpMap() {
        aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        addMarkersToMap();// 往地图上添加marker
        final Handler handler = new Handler(){
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        addMarkersToMap();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        final Timer timer = new Timer();
        TimerTask task = new TimerTask(){
            int i=0;
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
                i++;
                if(i==2){
                    i=0;
                    timer.cancel();
                }
            }
        };
        timer.schedule(task,5000,5000); //延时1000ms后执行，1000ms执行一次
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {

       /* str = response;

        String[] A =str.split("/|,");//返回字符串分隔

        for (int i = 4; i < A.length; i=i+3) {

            longt =Double.parseDouble(A[i].substring(1));
            lant =Double.parseDouble(A[i+1].substring(0,A[i+1].length()-1));
            pname = A[i-1];

            url = "http://10.9.34.126:8080/help_child_t1/help_image/"+pname+".png";
            latlng = new LatLng(lant,longt);


            View mview = getLayoutInflater(ss).inflate(R.layout.activity_marker_help, null);

            ImageView imageView = (ImageView) mview.findViewById(R.id.marker);

            Picasso.with(this.getActivity())
                    .load(url)
                    .error(R.drawable.error)
                    .into(imageView);

            MarkerOptions markerOptions = new MarkerOptions().position(latlng).
                    icon(BitmapDescriptorFactory.fromView(mview)).snippet("");

            // 添加到地图上
            Marker marker = aMap.addMarker(markerOptions);
            getInfoWindow(marker);
            marker.showInfoWindow();// 设置默认显示一个infowinfow

        }*/


        //help 图片显示
        if (jsonhelp !=null) {
            for (int i = 0; i < jsonhelp.length(); i++) {
                JSONObject jsonhelp1 = null;
                jsonhelp1 = ((JSONObject) jsonhelp.opt(i));

                try {
                    str = jsonhelp1.getString("location");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String[] A = str.substring(1, str.length() - 1).split(",");//返回字符串分隔
                hlongt = Double.valueOf(A[0]);
                hlant = Double.valueOf(A[1]);

                try {
                    hname = jsonhelp1.getString("img");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                latlng = new LatLng(hlant, hlongt);

                View mview = getLayoutInflater(ss).inflate(R.layout.activity_marker_help, null);

                final ImageView mImageView = (ImageView) mview.findViewById(R.id.marker);
                String imageUrl = IP+"/help_child_t1/help_image/" + hname + ".png";//02

                //显示图片的配置
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageOnLoading(R.drawable.loading)
                        .showImageOnFail(R.drawable.error)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();



                ImageSize mImageSize = new ImageSize(50, 50);


                ImageLoader.getInstance().loadImage(imageUrl, mImageSize, options, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        mImageView.setImageBitmap(loadedImage);
                    }

                });


                try {
                    htime = jsonhelp1.getString("time");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String year=htime.substring(0, 4);
                String month=htime.substring(4, 6);
                String day=htime.substring(6, 8);
                String hour=htime.substring(8, 10);
                String minute=htime.substring(10, 12);
//                MarkerOptions markerOptions = new MarkerOptions().position(latlng).
//                        icon(BitmapDescriptorFactory.fromView(mview)).title(hname).snippet("上传时间：\n" + year +"年"+ month +"月"+ day +"日"+ hour +"："+ minute );
                ChildBean childBean = new ChildBean();
                childBean.setChild_imgs("http://123.57.249.60:8080/help_child_t1/help_image/" + hname + ".png");
                childBean.setImageid(hname);
//                childBean.setImagename(hname);
                childBean.setYear(year);
                childBean.setMonth(month);
                childBean.setDay(day);
                childBean.setHour(hour);
                childBean.setMinute(minute);
                childBean.setUser_address(user_address);
                childBean.setSflat(myLat);
                childBean.setSflon(myLon);
                childBean.setDflat(hlant);
                childBean.setDflon(hlongt);
                childBean.setDistance(AMapUtils.calculateLineDistance(latlng,new LatLng(myLat,myLon)));


                MarkerOptions markerOptions = new MarkerOptions().position(latlng).
                        icon(BitmapDescriptorFactory.fromView(mview))
                        .title(hname)
//                        .snippet("姓名：" + rcname +"\n联系人：" + rpname + "\n联系方式：" + raddress+ "\n特征信息：" + rdetail);
                        .snippet(JSON.toJSONString(childBean));

                // 添加到地图上
                Marker marker = aMap.addMarker(markerOptions);
                getInfoWindow(marker);

            }
        }
        //record 图片显示
        if ( jsonrecord != null) {
            for (int i = 0; i < jsonrecord.length(); i++) {
                JSONObject jsonhelp1 = null;
                jsonhelp1 = ((JSONObject) jsonrecord.opt(i));

                try {
                    str = jsonhelp1.getString("location");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String[] A = str.substring(1, str.length() - 1).split(",");//返回字符串分隔
                rlongt = Double.valueOf(A[0]);
                rlant = Double.valueOf(A[1]);

                try {
                    rname = jsonhelp1.getString("img");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                latlng = new LatLng(rlant, rlongt);


                View mview = getLayoutInflater(ss).inflate(R.layout.activity_marker_record, null);



                final ImageView mImageView = (ImageView) mview.findViewById(R.id.marker);
                String imageUrl = IP+"/help_child_t1/record_image/" + rname + ".png";//03

                //显示图片的配置
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showStubImage(R.drawable.loading)
                        .showImageOnLoading(R.drawable.loading)
                        .showImageOnFail(R.drawable.error)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();

                //ImageLoader.getInstance().displayImage(imageUrl, mImageView, options);


                ImageSize mImageSize = new ImageSize(50, 50);


                ImageLoader.getInstance().loadImage(imageUrl, mImageSize, options, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        mImageView.setImageBitmap(loadedImage);
                    }

                });


                try {
                    rcname = jsonhelp1.getString("c_name");
                    rpname = jsonhelp1.getString("p_name");
                    raddress = jsonhelp1.getString("address");
                    rdetail = jsonhelp1.getString("detail");
                    rname = jsonhelp1.getString("img");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ChildBean childBean = new ChildBean();
                childBean.setChild_imgs("http://123.57.249.60:8080/help_child_t1/record_image/" + rname + ".png");
                childBean.setImageid(rname);
//                childBean.setImagename(rname);
                childBean.setChild_info(rdetail);
                childBean.setChild_name(rcname);
                childBean.setParent_name(rpname);
                childBean.setChild_lose_place(raddress);
                childBean.setChild_phone(raddress);
                childBean.setUser_address(user_address);
                childBean.setSflat(myLat);
                childBean.setSflon(myLon);
                childBean.setDflat(rlant);
                childBean.setDflon(rlongt);
                childBean.setDistance(AMapUtils.calculateLineDistance(latlng, new LatLng(myLat, myLon)));

                MarkerOptions markerOptions = new MarkerOptions().position(latlng).
                        icon(BitmapDescriptorFactory.fromView(mview))
                        .title(rname)
//                        .snippet("姓名："+rcname+"\n联系人："+rpname+"\n联系方式： " + raddress+ " \n特征信息："+ rdetail);
                        .snippet(JSON.toJSONString(childBean));

                // 添加到地图上
                Marker marker = aMap.addMarker(markerOptions);
                getInfoWindow(marker);

            }
        }

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 监听自定义infowindow窗口的infowindow事件回调
     * @param marker
     * @return
     */
/*    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow = getLayoutInflater(ss).inflate(R.layout.custom_info_window, null);

        render(marker, infoWindow);
        return infoWindow;
    }*/

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }



    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.isInfoWindowShown()){
            marker.hideInfoWindow();//这个是隐藏infowindow窗口的方法

        }

    }

    @Override
    public void onMapLoaded() {
        // 设置所有maker显示在当前可视区域地图中
        /*LatLngBounds bounds = new LatLngBounds.Builder()
                .include(XI).include(DONG).build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1));*/


    }
    //图标点击事件
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getSnippet() != null) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailActivity.class);
            intent.putExtra("data", marker.getSnippet());
            startActivity(intent);
        }

//        String mname=marker.getTitle();//获取图片名
//        ChildBean childBean = new ChildBean();
//        childBean.setImageid(mname);
//        int len = mname.length();
//        String imageUrl=null;//图片地址
//        //判断图片来自于help还是record表
//        if (len>20){
//        imageUrl = IP+"/help_child_t1/help_image/" +mname+ ".png";//04
//
//            //自定义弹窗
//            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//            alertDialog.setView(new EditText(getActivity()));
//            alertDialog.show();
//            Window window = alertDialog.getWindow();
//            WindowManager m = window.getWindowManager();
//            Display d = m.getDefaultDisplay();
//            WindowManager.LayoutParams p = window.getAttributes();
//            p.height = (int) (d.getHeight() * 0.5);
//            p.width = (int) (d.getWidth() * 0.9);
//            window.setAttributes(p);
//            window.setContentView(R.layout.detail_dialog_help);
//            final ImageView dimage = (ImageView) window.findViewById(R.id.dimage);
//            TextView dcname = (TextView) window.findViewById(R.id.dcname);
//
//            //（1）配置弹窗中显示的图片属性
//            DisplayImageOptions options = new DisplayImageOptions.Builder()
//                    .showStubImage(R.drawable.loading)
//                    .showImageOnLoading(R.drawable.loading)
//                    .showImageOnFail(R.drawable.error)
//                    .cacheInMemory(true)
//                    .cacheOnDisk(true)
//                    .bitmapConfig(Bitmap.Config.RGB_565)
//                    .build();
//            ImageSize mImageSize = new ImageSize(750, 750);
//            ImageLoader.getInstance().loadImage(imageUrl, mImageSize, options, new SimpleImageLoadingListener() {
//                @Override
//                public void onLoadingComplete(String imageUri, View view,
//                                              Bitmap loadedImage) {
//                    super.onLoadingComplete(imageUri, view, loadedImage);
//                    dimage.setImageBitmap(loadedImage);
//                }
//            });
//            //（2）配置弹窗中显示的文字信息
//            String snippet=marker.getSnippet();
//            dcname.setText(snippet);
//            dcname.setMovementMethod(ScrollingMovementMethod.getInstance());
//        }
//        else {
//        imageUrl = IP+"/help_child_t1/record_image/" +mname+ ".png";//05
//
//
//            //自定义弹窗
//            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//            alertDialog.setView(new EditText(getActivity()));
//            alertDialog.show();
//            Window window = alertDialog.getWindow();
//            WindowManager m = window.getWindowManager();
//            Display d = m.getDefaultDisplay();
//            WindowManager.LayoutParams p = window.getAttributes();
//            p.height = (int) (d.getHeight() * 0.9);
//            p.width = (int) (d.getWidth() * 0.9);
//            window.setAttributes(p);
//            window.setContentView(R.layout.detail_dialog_record);
//            final ImageView dimage = (ImageView) window.findViewById(R.id.dimage);
//            TextView dcname = (TextView) window.findViewById(R.id.dcname);
//            TextView contact_sb = (TextView) window.findViewById(R.id.contact_sb);
//            TextView share_info = (TextView) window.findViewById(R.id.share_info);
//
//            //（1）配置弹窗中显示的图片属性
//            DisplayImageOptions options = new DisplayImageOptions.Builder()
//                    .showStubImage(R.drawable.loading)
//                    .showImageOnLoading(R.drawable.loading)
//                    .showImageOnFail(R.drawable.error)
//                    .cacheInMemory(true)
//                    .cacheOnDisk(true)
//                    .bitmapConfig(Bitmap.Config.RGB_565)
//                    .build();
//            ImageSize mImageSize = new ImageSize(750, 750);
//            ImageLoader.getInstance().loadImage(imageUrl, mImageSize, options, new SimpleImageLoadingListener() {
//                @Override
//                public void onLoadingComplete(String imageUri, View view,
//                                              Bitmap loadedImage) {
//                    super.onLoadingComplete(imageUri, view, loadedImage);
//                    dimage.setImageBitmap(loadedImage);
//                }
//            });
//            //（2）配置弹窗中显示的文字信息
//            String snippet=marker.getSnippet();
//            dcname.setText(snippet);
//            dcname.setMovementMethod(ScrollingMovementMethod.getInstance());
//            //（3）配置联系按钮点击事件
//            final String[] detail = snippet.substring(1, snippet.length()).split(" ");
//            //final String phone=detail[3].replace("特征信息","");
//            contact_sb.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    //调到拨号界面
//                    Uri uri = Uri.parse("tel:"+detail[1]);
//                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
//                    startActivity(intent);
//                }
//            });
//            //（4）配置分享按钮点击事件
//            final String imgPath = imageUrl;
//            final String msgText = snippet;
//
//            share_info.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    showShare(imgPath,msgText);
//
//                }
//            });
//
//        }

        return true;
    }

//    private void showShare(String imgPath,String msgText) {
//        ShareSDK.initSDK(this.getContext());
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//        //url中汉字转码
//        String msg = null;
//        try {
//            msg = URLEncoder.encode(URLEncoder.encode(msgText.replaceAll(" ", ""), "utf-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle("中国失踪儿童互助系统发布消息");
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://123.57.249.60:8080/helpchildshare/index.html?img="+imgPath+"&msg="+msg);
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText(msgText);
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://123.57.249.60:8080/helpchildshare/index.html?img="+imgPath+"&msg="+msg);
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        //oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://123.57.249.60:8080/helpchildshare/index.html?img="+imgPath+"&msg="+msg);
//        oks.setImageUrl(imgPath);
//        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
//            @Override
//            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
//                if ("QZone".equals(platform.getName())) {
//                    paramsToShare.setTitle(null);
//                    paramsToShare.setTitleUrl(null);
//                }
//                if ("SinaWeibo".equals(platform.getName())) {
//                    paramsToShare.setUrl(null);
//                    paramsToShare.setText("分享文本 http://www.baidu.com");
//                }
//                if ("Wechat".equals(platform.getName())) {
//                    Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.ssdk_logo);
//                    paramsToShare.setImageData(imageData);
//                }
//                if ("WechatMoments".equals(platform.getName())) {
//                    Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.ssdk_logo);
//                    paramsToShare.setImageData(imageData);
//                }
//
//            }
//        });
//
//// 启动分享GUI
//        oks.show(this.getContext());
//    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
    //定位相关

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(10000);//可选，设置定位间隔。默认为10秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    myLat = location.getLatitude();
                    myLon = location.getLongitude();
                    sb.append("定位成功" + "\n");
                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");
                    user_address = location.getAddress();
                    aMap = mapView.getMap();
                    if (firstLocation) {
                        //窗口显示地图范围
                        LatLng marker1 = new LatLng(myLat, myLon);
                        ImageView ivMarket = new ImageView(getActivity());
                        ivMarket.setImageResource(R.drawable.ic_location);
                        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromView(ivMarket)).position(marker1);

                        aMap.addMarker(markerOptions);
                        LatLngBounds bounds = new LatLngBounds.Builder().include(marker1).build();
                        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
                        firstLocation = false;
                    }

                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }

                //解析定位结果，
//                String result = sb.toString();
//                Toast.makeText(getActivity(),result, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "定位失败，loc is null", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
