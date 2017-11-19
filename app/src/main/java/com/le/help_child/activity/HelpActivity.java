package com.le.help_child.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Text;
import com.le.help_child.MainActivity;
import com.le.help_child.R;
import com.le.help_child.update.NetWork;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class HelpActivity extends Fragment implements AMap.OnMarkerDragListener,
        AMap.OnMapLoadedListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter {
    /**
     * 基础地图
     */
    private MapView mapView;
    private AMap aMap;
    private Bundle ss;
    private String phonenum = null; // 电话号码
    private String result = "";     // 服务端传回值
    private double longt;          // 经度
    private double lant;//纬度
    private String pname;//照片名
    private String ptime;//照片时间
    private String plocation;//照片时间
    private LatLng latlng = new LatLng(39.90403, 116.407525);
    private TextView registerinfo_feedback;

    public boolean isNet;

    ImageView imageView_MatchResult;

    TextView textView_MatchResult;

    public HelpActivity() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // 初始化加载界面文件
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ss = savedInstanceState;
        return inflater.inflate(R.layout.activity_help, container, false);
    }
//*************************************************************************************************************************************//
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView = (MapView) this.getView().findViewById(R.id.helpmap);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        // final Button login_h = (Button) this.getView().findViewById(R.id.click_button);
        // 初始化用于显示用户名的textView
        registerinfo_feedback = (TextView) this.getView().findViewById(R.id.registerinfo_feedback);
        imageView_MatchResult = (ImageView) this.getView().findViewById(R.id.imageView_MatchResult);
        showMatchResult();

//*************************************************************************************************************************************//
        final Handler myone = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 找到匹配信息
                if (msg.what == 0x1233) {
                    String[] A = result.split("/");//返回字符串分隔
                    String[] B = A[0].substring(1, A[0].length() - 1).split(",");
                    longt = Double.valueOf(B[0]);
                    lant = Double.valueOf(B[1]);
                    pname = A[1];
                    ptime = A[2];
                    plocation = A[4];
                    init();// 初始化AMap对象
                    // 未找到匹配信息
                } else if (msg.what == 0x1244) {
                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "未登录！请返回主页进行帐户登录！", Toast.LENGTH_SHORT);
                    toast1.show();
                } else if (msg.what == 0x1255) {
                    Toast toast2 = Toast.makeText(getActivity().getApplicationContext(), "未找到匹配信息,请您密切关注推送消息！", Toast.LENGTH_SHORT);
                    toast2.show();
                }
                else if(msg.what == 0x1256){
                    registerinfo_feedback.setText(phonenum);
                    Toast toast2 = Toast.makeText(getActivity().getApplicationContext(), "未找到匹配信息,请您密切关注推送消息！", Toast.LENGTH_SHORT);
                    toast2.show();
                }
            }
        };

//***********************************************************已登录情况*************************************************************//
        if (MainActivity.isLogin) {
            String map_result = getActivity().getIntent().getStringExtra("help_result");
            registerinfo_feedback.setText(MainActivity.common_name);
            if (map_result.equals("")) {
                myone.sendEmptyMessage(0x1255);
            } else {
                result = map_result;
                myone.sendEmptyMessage(0x1233);
            }
//***********************************************************未登录情况*************************************************************//
        } else {
            // 弹出登录弹窗
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setView(new EditText(getActivity()));
            // 使弹窗能显示出来
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.help_login_dialog);
            final EditText phone_et = (EditText) window.findViewById(R.id.username_h);
            phone_et.setFocusable(true);
            phone_et.setFocusableInTouchMode(true);
            // 小窗口登陆的点击事件
            final Button login = (Button) window.findViewById(R.id.login_h);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread() {
                        @Override
                        public void run() {
                            phonenum = phone_et.getText().toString();
                            // 调用loginByPost方法
                            result = loginByPost(phonenum);
                            if (result.equals("")) {
                                // 未找到任何信息
                                myone.sendEmptyMessage(0x1256);
                            } else {
                                registerinfo_feedback.setText(MainActivity.common_name);
                                // 存在匹配信息，匹配信息显示的结果在收到信息之后处理
                                myone.sendEmptyMessage(0x1233);
                            }
                        }
                    }.start();
                    alertDialog.dismiss();
                }

            });
//***********************************************************************************************************************************//
            // 小窗口"取消"按钮的点击事件
            Button exit = (Button) window.findViewById(R.id.exit_h);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

        }
    }

    private void showMatchResult() {
        imageView_MatchResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNet = NetWork.checkNetWorkStatus(getActivity());
                if(isNet)
                {
                    // 弹出登录弹窗
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setView(new EditText(getActivity()));
                    // 使弹窗能显示出来
                    alertDialog.show();
                    Window window = alertDialog.getWindow();
                    WindowManager.LayoutParams params_window = window.getAttributes();
                    WindowManager m = getActivity().getWindowManager();
                    Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                    params_window.width = (int) (d.getHeight() * 0.4);
                    params_window.height = (int) (d.getHeight() * 0.4);
                    window.setAttributes(params_window);
                    window.setContentView(R.layout.match_result);
                    textView_MatchResult = (TextView) window.findViewById(R.id.textView_MatchResult);
                    if(!result.equals("")){
                        textView_MatchResult.setText("已找到匹配信息，请关注地图信息！");
                    }
                    else {
                        textView_MatchResult.setText("暂无消息！");
                    }
                }

            }
        });

    }

//*************************************************************************************************************************************//

//---------------------------------------------------------以下不动--------------------------------------------------------------------//

    // 初始化AMap对象
    private void init() {
        aMap = mapView.getMap();
        setUpMap();
    }

    private void setUpMap() {
        aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        addMarkersToMap();// 往地图上添加marker
    }

    // 在地图上添加marker
    private void addMarkersToMap() {
        latlng = new LatLng(lant, longt);
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, 10, 0, 30)));
        Marker marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latlng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("时间：" + ptime)
                .snippet("地点：" + plocation)
                .draggable(true));
        marker.showInfoWindow();// 设置默认显示一个infowinfow

    }
    // 获取匹配反馈信息
    public String loginByPost(String phoneNum) {
        String result = "";
        try {
            String target = "http://123.57.249.60:8080/help_child_t1/childservice?method=help";   //要提交的目标地址
            URL url = new URL(target);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection(); // 创建一个HTTP连接
            urlConn.setRequestMethod("POST"); // 指定使用POST请求方式
            urlConn.setDoInput(true); // 向连接中写入数据
            urlConn.setDoOutput(true); // 从连接中读取数据
            urlConn.setUseCaches(false); // 禁止缓存
            urlConn.setInstanceFollowRedirects(true);   //自动执行HTTP重定向
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // 设置内容类型
            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream()); // 获取输出流
            String param = "ca=" + URLEncoder.encode(phoneNum, "utf-8"); //连接要提交的数据
            out.writeBytes(param);//将要传递的数据写入数据输出流
            out.flush();    //输出缓存
            out.close();    //关闭数据输出流
            // 判断是否响应成功
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(urlConn.getInputStream()); // 获得读取的内容
                BufferedReader buffer = new BufferedReader(in); // 获取输入流对象
                String inputLine = null;
                while ((inputLine = buffer.readLine()) != null) {
                    result = inputLine;
                }
                in.close(); //关闭字符输入流
            } else {
                Toast.makeText(this.getContext(), "响应失败！", Toast.LENGTH_LONG).show();
            }
            urlConn.disconnect();   //断开连接
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onMapLoaded() {
        // 设置所有maker显示在当前可视区域地图中
        LatLngBounds bounds = new LatLngBounds.Builder().include(latlng).build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    // 监听自定义infowindow窗口的infowindow事件回调
    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow = getLayoutInflater(ss).inflate(R.layout.custom_info_window, null);
        render(marker, infoWindow);
        return infoWindow;
    }

    // 监听自定义infowindow窗口的infocontents事件回调
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    // 自定义infowinfow窗口
    public void render(Marker marker, View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.badge);
        Picasso.with(this.getActivity())
               // .load("http://10.100.34.108:8080/help_child_t1/help_image/" + pname + ".png")
                .load("http://123.57.249.60:8080/help_child_t1/help_image/" + pname + ".png")
                .error(R.drawable.error)
                .into(imageView);

        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
                    titleText.length(), 0);
            titleUi.setTextSize(12);
            titleUi.setText(titleText);

        } else {
            titleUi.setText("");
        }
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.RED), 0,
                    snippetText.length(), 0);
            snippetUi.setTextSize(12);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }

    }
}
