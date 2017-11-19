package com.le.help_child.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.le.help_child.R;
import com.le.help_child.adapter.CommonAdapter;
import com.le.help_child.adapter.CommonViewHolder;
import com.le.help_child.bean.ChildBean;
import com.le.help_child.bean.MessageListBean;
import com.le.help_child.util.DateUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class DetailActivity extends Activity {
    TextView tv_name, tv_parent, tv_phone, tv_time, tv_place, tv_tps, tv_leave_msg, tv_distance, tv_tothere, contact_sb, share_info,tv_nothing;
    ListView lv_msg;
    ImageView ivHead;
    ChildBean myItem;
    private String shareText;


    public String IP = "http://123.57.249.60:8080";// 服务器IP地址
    String imgname;//图片名
    String imgType;
    List<MessageListBean.NoteBean> list = new ArrayList<MessageListBean.NoteBean>() {
    };
    CommonAdapter<MessageListBean.NoteBean> adapter;
    android.os.Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (list.size()==0){
                tv_nothing.setVisibility(View.VISIBLE);
            }else{
                tv_nothing.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
            return false;
        }
    });

    android.os.Handler handler1 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (list.size()==0){
                tv_nothing.setVisibility(View.VISIBLE);
            }else{
                tv_nothing.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        findview();
        initData();
    }

    private void findview() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_parent = (TextView) findViewById(R.id.tv_parent);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_place = (TextView) findViewById(R.id.tv_place);
        tv_tps = (TextView) findViewById(R.id.tv_tps);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_tothere = (TextView) findViewById(R.id.tv_tothere);
        tv_nothing = (TextView) findViewById(R.id.tv_nothing);

        contact_sb = (TextView) findViewById(R.id.contact_sb);
        share_info = (TextView) findViewById(R.id.share_info);

        tv_leave_msg = (TextView) findViewById(R.id.tv_leave_msg);

        lv_msg = (ListView) findViewById(R.id.lv_msg);

        ivHead = (ImageView) findViewById(R.id.iv_img);
    }
//    http://123.57.249.60:8080/help_child_t1/childservice?method=addnote&imageid=201607252007502bmjX&tel=12345678901&message=我又看见&datetime=20170801


//    http://123.57.249.60:8080/help_child_t1/childservice?method=getnote&imageid=201607252007502bmjX

    public void openWebGaoDeMap(View view) {
        try {
            myItem = JSON.parseObject(getIntent().getStringExtra("data"), ChildBean.class);

//            //经度
//            double lon= myItem.getFlon();
//            //纬度
//            double lat= myItem.getFlat();
            double[] start = {myItem.getSflon(), myItem.getSflat()};
            double[] dest = {myItem.getDflon(), myItem.getDflat()};
            openWebMap(start[0], start[1], dest[0], dest[1]);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(DetailActivity.this, "抱歉！数据初始化失败，暂时无法使用。", Toast.LENGTH_LONG).show();
        }

    }

    private void openWebMap(double slon, double slat, double dlon, double dlat) {
        Uri mapUri = Uri.parse(OpenLocalMapUtil.getWebGaoDeMapUri(
                String.valueOf(slon), String.valueOf(slat),
                String.valueOf(dlon), String.valueOf(dlat)));
        Intent loction = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(loction);
    }

    private void initData() {


        try {
            myItem = JSON.parseObject(getIntent().getStringExtra("data"), ChildBean.class);

            if (myItem.getChild_name() != null) {
                imgType="record_image";
                tv_name.setText( myItem.getChild_name());
                tv_parent.setText( myItem.getParent_name());
                tv_phone.setText( myItem.getChild_phone());
                tv_tps.setText(myItem.getChild_info());
                tv_distance.setText(myItem.getDistance() + "米");
                shareText = "姓名：" + myItem.getChild_name() + "\n联系人：" + myItem.getParent_name() + "\n联系方式： "
                        + myItem.getChild_phone() + " \n特征信息：" + myItem.getChild_info();
            } else {
                imgType="help_image";
//    "上传时间：\n" + year +"年"+ month +"月"+ day +"日"+ hour +"："+ minute
                tv_parent.setText("上传时间：" + myItem.getYear() + "年" + myItem.getMonth() + "月"
                        + myItem.getDay() + "日" + myItem.getHour() + "：" + myItem.getMinute());
                tv_phone.setText("直线距离：" + myItem.getDistance() + "米");
                tv_tps.setText("补充说明：此为爱心人士上传的疑似失踪人员照片");
                shareText = "上传时间：" + myItem.getYear() + "年" + myItem.getMonth() + "月"
                        + myItem.getDay() + "日" + myItem.getHour() + "：" + myItem.getMinute() + "\n补充说明：此为爱心人士的随手拍";
            }
//            tv_name.setText("姓名："+myItem.getChild_name());
//            tv_parent.setText("联系人："+myItem.getParent_name());
//            tv_phone.setText("联系电话："+myItem.getChild_phone());
//            tv_tps.setText("详细信息："+myItem.getChild_info());
//            tv_distance.setText("相距："+myItem.getDistance()+"米");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(DetailActivity.this, "数据初始化失败，请返回重试", Toast.LENGTH_LONG).show();
        }

        //显示图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.loading)
                .showImageOnLoading(R.drawable.loading)
                .showImageOnFail(R.drawable.error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageSize mImageSize = new ImageSize(750, 750);
        ImageLoader.getInstance().loadImage(myItem.getChild_imgs(), mImageSize, options, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                ivHead.setImageBitmap(loadedImage);
            }
        });


        //联系按钮点击事件
        contact_sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //调到拨号界面
                Uri uri = Uri.parse("tel:" + myItem.getChild_phone());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });


        //配置分享按钮点击事件
        imgname = myItem.getImageid();
        final String imgPath = IP + "/help_child_t1/"+imgType+"/"+ imgname + ".png";
        final String msgText = shareText;
        share_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showShare(imgPath, msgText);

            }
        });


//留言点击事件
        tv_leave_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.msg_dialog, null);
                //获取布局中的控件
                final EditText phoneNum = (EditText) layout.findViewById(R.id.edit_tel);
                final EditText msg = (EditText) layout.findViewById(R.id.edit_msg_info);
                new AlertDialog.Builder(DetailActivity.this)
                        .setView(layout)
                        .setTitle("线索留言")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                                final String datetime = df.format(new Date());
                                final String tel = phoneNum.getText().toString();
                                final String message = msg.getText().toString();
                                final String imageid = myItem.getImageid();
                                boolean boo = isMobileNO(tel);
//                                String re = postparas(imageid, tel, message, datetime);


                                try {
                                    if ((message.equals("")) && (tel.equals(""))){
                                        Toast.makeText(DetailActivity.this, "您填写的信息不完整！请重新填写！", Toast.LENGTH_SHORT).show();
                                    } else if (tel.equals("") && !(message.equals(""))){
                                        Toast.makeText(DetailActivity.this, "请输入手机号!", Toast.LENGTH_SHORT).show();
                                    } else if (!boo&& !(message.equals(""))){
                                        Toast.makeText(DetailActivity.this, "请输入正确的手机号!", Toast.LENGTH_SHORT).show();
                                    }else if (message.equals("")){
                                        Toast.makeText(DetailActivity.this, "留言内容不能为空！请重新填写！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        new Thread(new Runnable(){
                                            @Override
                                            public void run(){

                                                String re = postparas(imageid, tel, message, datetime);
                                            }

                                        }).start();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(DetailActivity.this, "留言失败，请检查网络！", Toast.LENGTH_LONG).show();
                                }




                            }

                            boolean isMobileNO(String tel) {
                                String num = "[1][358]\\d{9}";
                                return tel.matches(num);
                            }
//                            boolean isPhone(String inputText) {
//                                Pattern p = Pattern.compile("^((14[0-9])|(13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
//                                Matcher m = p.matcher(inputText);
//                                return m.matches();
//                            }

                            // 上传参数
                            private String postparas(String imageid, String tel, String message, String datetime) {
                                String result = "";
                                        String url = "http://123.57.249.60:8080/help_child_t1/childservice?method=addnote&imageid=" + imageid + "&tel=" + tel + "&message=" + message + "&datetime=" + datetime;

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
                                            result = line;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        loadMessage();

                                //    http://123.57.249.60:8080/help_child_t1/childservice?method=addnote&imageid=201607252007502bmjX&tel=12345678901&message=我又看见&datetime=20170801
                                return result;
                            }

////                            未改动
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                               MessageBean item = new MessageBean();
//                                item.setAddress(myItem.getUser_address());
//                                item.setMessage_info(etSay.getText().toString().trim());
//                                item.setPhone("13333333333");
//                                item.setUser_name("用户");
//                                list.add(item);
//                                adapter.notifyDataSetChanged();
//
//                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        });


//        --------------------------------------------------------------------------------------------------


        adapter = new CommonAdapter<MessageListBean.NoteBean>(DetailActivity.this, list, R.layout.item_message_layout) {
            //http://123.57.249.60:8080/help_child_t1/childservice?method=getnote&imageid=201607252007502bmjX
            @Override
            public void convert(CommonViewHolder holder, int position, MessageListBean.NoteBean item) {
                holder.setText(R.id.tv_phone, item.getTel());
                holder.setText(R.id.tv_time, DateUtils.format(DateUtils.parse(item.getDatetime(),"yyyyMMddHHmm"),"yyyy年MM月dd日 HH:mm"));
                holder.setText(R.id.tv_info, item.getMessage());

            }
        };
        lv_msg.setAdapter(adapter);
        loadMessage();


    }


//    private void upMessage(){
//        new Thread(new Runnable(){
//            @Override
//            public void run(){
//
//
//            }
//
//        }).start();
//
//    }



    private void loadMessage() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection.Response res = null;
                try {
                    res = Jsoup.connect(IP+"/help_child_t1/childservice?method=getnote&imageid=" + myItem.getImageid() )
                            .header("Accept", "*/*")
                            .header("Accept-Encoding", "gzip, deflate")
                            .header("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                            .header("Content-Type", "application/json;charset=UTF-8")
                            .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                            .data(new HashMap<String, String>())
                            .timeout(10000).ignoreContentType(true).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String body = res.body();
                MessageListBean myListData = JSON.parseObject(body,MessageListBean.class);
                list.clear();
                list.addAll(myListData.getNote());
                handler.sendEmptyMessage(0);
            }
        }).start();

    }


    private void showShare(String imgPath, String msgText) {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        //url中汉字转码
        String msg = null;
        try {
            msg = URLEncoder.encode(URLEncoder.encode(msgText.replaceAll(" ", ""), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("中国失踪儿童互助系统发布消息");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://123.57.249.60:8080/helpchildshare/index.html?img=" + imgPath + "&msg=" + msg);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(msgText);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://123.57.249.60:8080/helpchildshare/index.html?img=" + imgPath + "&msg=" + msg);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://123.57.249.60:8080/helpchildshare/index.html?img=" + imgPath + "&msg=" + msg);
        oks.setImageUrl(imgPath);
        /*oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                if ("QZone".equals(platform.getName())) {
                    paramsToShare.setTitle(null);
                    paramsToShare.setTitleUrl(null);
                }
                if ("SinaWeibo".equals(platform.getName())) {
                    paramsToShare.setUrl(null);
                    paramsToShare.setText("分享文本 http://www.baidu.com");
                }
                if ("Wechat".equals(platform.getName())) {
                    Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.ssdk_logo);
                    paramsToShare.setImageData(imageData);
                }
                if ("WechatMoments".equals(platform.getName())) {
                    Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.ssdk_logo);
                    paramsToShare.setImageData(imageData);
                }

            }
        });*/

// 启动分享GUI
        oks.show(this);
    }


}
