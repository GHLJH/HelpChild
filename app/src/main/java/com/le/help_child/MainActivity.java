package com.le.help_child;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.le.help_child.activity.HelpActivity;
import com.le.help_child.activity.MyRecordActivity;
import com.le.help_child.activity.TabActivity;
import com.le.help_child.activity.MyUploadActivity;
import com.le.help_child.activity.ToolsActivity;
import com.le.help_child.update.NetWork;
import com.le.help_child.update.VersionUpdate;
import com.le.help_child.view.SlidingMenu;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import cn.jpush.android.api.JPushInterface;
// 系统第二个要启动的页面<是一个activity>
public class MainActivity extends AppCompatActivity {
    private SlidingMenu mLeftMenu;
    public LinearLayout mUpload;
    public LinearLayout mRecord;
    public LinearLayout muMessage;
    public LinearLayout mMoney;
    public ImageButton mytools;
    public  static  Boolean isLogin = false;
    public  static String common_name = "";
    public String deliverMapInfo = null;

    public String input = null;
    public String upload_params = null;
    public String record_params = null;
    public int money_para = 0;
    private SharedPreferences sp;
    public boolean netState;    // 网络状态参数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 检查网络状态
        netState = NetWork.checkNetWorkStatus(MainActivity.this);
        // 检查软件更新
        if (netState) {
            VersionUpdate manager = new VersionUpdate(MainActivity.this);
            manager.checkUpdate();
        }
        // 极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //*********************************************************************************************************************//
        // 初始化登陆按钮
        final TextView tv = (TextView) findViewById(R.id.textView);

        sp = getSharedPreferences("userInfo", 0);
        // 获取上次系统记录的用户名，即手机号
        final String name = sp.getString("USER_NAME", "");

        //*************************************************begin--消息处理过程--begin***********************************************//
        final Handler loadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 未检测到该手机号的上传信息
                if (msg.what == 0) {
                    Toast toast = null;
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom, (ViewGroup) findViewById(R.id.llToast));
                    TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
                    text.setText("未检测到该手机号的上传信息");
                    common_name = null;
                    toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 20);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                } else if (msg.what == 1) {
                    assert tv != null;
                    tv.setText(input);
                    // 已经记忆用户名并且选择了自动登陆的情况，并且验证数据库中是有这个记录的
                } else if (msg.what == 2) {
                    assert tv != null;
                    tv.setText(name);
                }
            }
        };
        //*************************************************end--消息处理过程--end***********************************************//

//*******************************************************--begin--主界面--begin--***************************************************//
        // 主界面右侧底端初始化四个核心按钮
        ImageButton camera_imb = (ImageButton) findViewById(R.id.camera_button);
        ImageButton record_imb = (ImageButton) findViewById(R.id.record_button);
        ImageButton place_imb = (ImageButton) findViewById(R.id.place_button);
        ImageButton help_imb = (ImageButton) findViewById(R.id.help_button);

        // "拍照上传"按钮的点击事件
        if (camera_imb != null) {
            camera_imb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent i = new Intent(MainActivity.this, TabActivity.class);
                    i.putExtra("para", 0);
                    startActivity(i);
                }
            });
        }

        // "登记按钮"的点击事件
        if (record_imb != null) {
            record_imb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent i = new Intent(MainActivity.this, TabActivity.class);
                    i.putExtra("para", 1);
                    startActivity(i);
                }
            });
        }

        // "地图找"按钮的点击事件
        if (place_imb != null) {
            place_imb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent i = new Intent(MainActivity.this, TabActivity.class);
                    i.putExtra("para", 2);
                    startActivity(i);
                }
            });
        }

        // "反馈按钮"的点击事件
        if (help_imb != null) {
            help_imb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent i = new Intent(MainActivity.this, TabActivity.class);
                    i.putExtra("para", 3);
                    if(deliverMapInfo != null)
                    {
                        i.putExtra("help_result",deliverMapInfo);
                    }
                    startActivity(i);
                }
            });
        }

//*********************************************************--end--主界面--end--*****************************************************//

        final boolean choseRemember = sp.getBoolean("remember", false);  // 获取是否记住
        final boolean choseAutoLogin = sp.getBoolean("autologin", false);// 获取是否自动登录

//**************************************************第三种登录：记住用户名且自动登录************************************************//
        // name:sp存储里记忆的用户名。。如果不空，并且自动登录
        if (!name.equals("") && choseAutoLogin) {
            new Thread() {
                @Override
                public void run() {
                    // 检查上次随手一拍保存的用户名在数据库中有没有
                    String exam = check(name);
                    //*********************************第三种情况要传的值********************************//
                    deliverMapInfo = loginByPost(name);
                    //***********************************************************************************//
                    // 如果登记信息不空。。。注：这里是子线程不能控制UI，要向主线程发消息
                    if (!exam.equals(""))
                    {
                        // 要传递的名字
                        common_name = name;
                        // 向主线程发送消息，将登陆这个textView的内容从“未登陆”改变为“此次查询的这个手机号”
                        loadHandler.sendEmptyMessage(2);
                        // 对此次查询的手机号所查询出来的内容进行解析
                        try {
                            JSONObject obj_exam = new JSONObject(exam);
                            JSONArray a = obj_exam.getJSONArray("upload");
                            upload_params = a.toString();
                            JSONArray b = obj_exam.getJSONArray("record");
                            record_params = b.toString();
                            money_para = obj_exam.getInt("money");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 更改是否登录状态为真
                        isLogin = true;
                    }
                    // 验证失败
                    else {
                        loadHandler.sendEmptyMessage(0);
                    }
                }
            }.start();
        }

//***************************************************--begin--整体左侧侧滑部分--begin--**********************************************//

        mLeftMenu = (SlidingMenu) findViewById(R.id.id_menu);

        //***********************************--begin--左侧滑界面"顶部登陆"按钮的点击事件--begin--************************************//
        assert tv != null;
        /*final TextView tv = (TextView) findViewById(R.id.textView);*/
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = tv.getText().toString();
                if (username.equals("登录")) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setView(new EditText(MainActivity.this));
                    alertDialog.show();
                    Window window = alertDialog.getWindow();
                    window.setContentView(R.layout.login_dialog);
                    // 登陆小窗口中的控件初始化
                    final EditText phone_et = (EditText) window.findViewById(R.id.username);
                    phone_et.setFocusable(true);
                    phone_et.setFocusableInTouchMode(true);
                    final CheckBox isrem = (CheckBox) window.findViewById(R.id.is_rem);
                    final CheckBox autologin = (CheckBox) window.findViewById(R.id.is_auto);
                    // 如果上次选了记住用户名，那进入登录页面也自动勾选记住用户名，并填上用户名
                    if (choseRemember) {
                        phone_et.setText(name);
                        assert isrem != null;
                        isrem.setChecked(true);
                    }
                    // 左侧侧滑textView登陆所包含的小窗口"登陆"按钮的点击事件
                    Button login = (Button) window.findViewById(R.id.login);
                    login.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Thread() {
                                @Override
                                public void run() {
                                    // final EditText phone_et = (EditText) window.findViewById(R.id.username);
                                    // 输入手机号，值放在intput中
                                    input = phone_et.getText().toString();
                                    // 判断数据库中是否存在这个手输的input手机号
                                    String exam = check(input);
                                    //****************************************第二种登录************************************//
                                    if (!exam.equals("")) {
                                        //--------------------?????????????????????????????????????????---------------------//
                                        JPushInterface.setAlias(MainActivity.this, input, null);// 标记用户名为极光推送用户名
                                        // 记录登录名
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("USER_NAME", input);
                                        // 是否记住密码
                                        assert isrem != null;
                                        if (isrem.isChecked()) {
                                            editor.putBoolean("remember", true);
                                        } else {
                                            editor.putBoolean("remember", false);
                                        }
                                        // 是否自动登录
                                        assert autologin != null;
                                        if (autologin.isChecked()) {
                                            editor.putBoolean("autologin", true);
                                        } else {
                                            editor.putBoolean("autologin", false);
                                        }
                                        editor.commit();
                                        common_name = input;
                                        //*********************************第二种情况要传的值********************************//
                                        deliverMapInfo = loginByPost(input);
                                        //***********************************************************************************//
                                        loadHandler.sendEmptyMessage(1);
                                        try {
                                            JSONObject obj_exam = new JSONObject(exam);
                                            JSONArray a = obj_exam.getJSONArray("upload");
                                            upload_params = a.toString();
                                            JSONArray b = obj_exam.getJSONArray("record");
                                            record_params = b.toString();
                                            money_para = obj_exam.getInt("money");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        isLogin = true;
                                    }
                                    // 输入的手机号不存在登记信息
                                    else {
                                        loadHandler.sendEmptyMessage(0);
                                    }
                                }
                            }.start();
                            alertDialog.dismiss();
                        }

                    });

                    // 取消按钮的点击事件
                    Button exit = (Button) window.findViewById(R.id.exit);
                    exit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });


                }
            }
        });

        //**************************************--end--左侧滑界面"顶部登陆"按钮的点击事件--end--***************************************//

        // 侧滑左侧“爱心上传”按钮点击事件
        mUpload = (LinearLayout)findViewById(R.id.Layout_upload);
        mUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLogin){
                    Intent i = new Intent(MainActivity.this,MyUploadActivity.class);
                    i.putExtra("paras",upload_params);
                    startActivity(i);
                }
                else{
                    Toast toast = null;
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom,(ViewGroup)findViewById(R.id.llToast));
                    TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
                    text.setText("您尚未登录");
                    toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 20);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });

        // 侧滑左侧“我的登记”按钮点击事件
        mRecord = (LinearLayout)findViewById(R.id.Layout_record);
        mRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLogin){
                    Intent i = new Intent(MainActivity.this,MyRecordActivity.class);
                    i.putExtra("paras",record_params);
                    startActivity(i);
                }
                else{
                    Toast toast = null;
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom,(ViewGroup)findViewById(R.id.llToast));
                    TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
                    text.setText("您尚未登录");
                    toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 20);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });

        // 侧滑左侧“我的消息”按钮点击事件
        muMessage = (LinearLayout)findViewById(R.id.Layout_message);
        muMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLogin){
                    new  AlertDialog.Builder(MainActivity.this)
                            .setTitle("我的消息模块尚未开通！" )
                            .setMessage("为确保及时接受消息数据，请打开您的接收消息权限！")
                            .setPositiveButton("确定" , null )
                            .show();
                }
                else{
                    Toast toast = null;
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom,(ViewGroup)findViewById(R.id.llToast));
                    TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
                    text.setText("您尚未登录");
                    toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 20);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });

        // 侧滑左侧“我的积分”按钮点击事件
        mMoney = (LinearLayout)findViewById(R.id.Layout_money);
        mMoney.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLogin){
                    new  AlertDialog.Builder(MainActivity.this)
                            .setTitle("您的积分" )
                            .setMessage(money_para+"积分")
                            .setPositiveButton("确定" , null )
                            .show();
                }
                else{
                    Toast toast = null;
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom,(ViewGroup)findViewById(R.id.llToast));
                    TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
                    text.setText("您尚未登录");
                    toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 20);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });

        // 侧滑左侧“设置”按钮点击事件
        mytools = (ImageButton)findViewById(R.id.tools);
        mytools.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ToolsActivity.class);
                i.putExtra("paras", tv.getText().toString());
                startActivity(i);
            }
        });
        
        // 左侧侧滑最底端“退出登录”
        Button out_of_login = (Button)findViewById(R.id.out_login_button);
        assert out_of_login != null;
        out_of_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isLogin = false;
                tv.setText("登录");
                upload_params = "";
                record_params = "";
                money_para = 0;
            }
        });
    }

//*************************************************--end--整体左侧侧滑部分--end--***************************************************//

    // 查询手机号码的登记信息
    public String check(String input) {
        String url = "http://10.100.34.108:8080/help_child_t1/MyInfo?phone=" + input;
        String result = "";
        try {
            URL getUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String lines;
            String line = "";
            while ((lines = reader.readLine()) != null) {
                line += lines;
            }
            JSONObject obj = new JSONObject(line);
            String my_m = obj.getString("money");
            if (my_m.equals("")) {
                result = "";
            } else {
                result = line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void toggleMenu(View view)
    {
        mLeftMenu.toggle();
    }

    public String loginByPost(String phoneNum) {
        String result = "";
        try {
            String target = "http://10.100.34.108:8080/help_child_t1/childservice?method=help";   //要提交的目标地址
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
                Toast.makeText(this, "响应失败！", Toast.LENGTH_LONG).show();
            }
            urlConn.disconnect();   //断开连接
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
