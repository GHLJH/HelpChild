package com.le.help_child.activity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.le.help_child.R;
import com.le.help_child.update.NetWork;
import com.le.help_child.util.NetTool;
import com.le.help_child.util.UploadFileTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class RecordActivity extends Fragment {
    Button btn_Upload;
    Button btn_featureInfo;
    Button btn_window_feature;

    TextView btn_LossTime;
    EditText edt_child;
    EditText edt_parent;
    EditText edt_location;
    EditText edt_address;
    EditText edt_feature;

    ImageView imageView;
    Window window;
    String str_picName_DataBase = ""; // 存到数据库中的照片名：当前时间加随机数（不带后缀）
    String str_edt_feature="";
    String new_btn_up_feature;
    String temp_path;
    String picName_png;
    String str_current_time; // 系统的当前时间(到秒)
    String str_system_time;  // 系统的当前时间(到天)
    String img;
    String msg;
    String msg1;
    private String picPath = null;
    private String initStartDateTime = "请选择时间";
    private String initEndDateTime;
    public String dateTime;
    public String str_LossTime = "";

    Map<String, String> params = new HashMap<String, String>();
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_record, container, false);
    }

    public final Handler myhandle = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (msg.what == 0x1211) {
                //getActivity().finish();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                  builder.setMessage("确认分享吗？");
                 builder.setTitle("分享");
                  builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            showShare(img,msg1);
                           }
                      });
                  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                           }
                      });
                  builder.create().show();
            }
        }

    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 初始化控件
        edt_child = (EditText) this.getView().findViewById(R.id.childname_et); // 孩子姓名
        edt_parent = (EditText) this.getView().findViewById(R.id.parentname_et); //　家长姓名
        edt_location = (EditText) this.getView().findViewById(R.id.location_et); // 丢失位置
        edt_address = (EditText) this.getView().findViewById(R.id.address_et); // 联系方式
        imageView = (ImageView) this.getView().findViewById(R.id.imageView);
        btn_featureInfo = (Button) this.getView().findViewById(R.id.btn_featureInfo);
        btn_LossTime = (TextView) this.getView().findViewById(R.id.btn_LossTime);
        btn_Upload = (Button) this.getView().findViewById(R.id.btn_s);

        // 生成系统当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
        str_current_time = df.format(new Date()); // 系统时间：用于拼照片名
        str_system_time = df2.format(new Date()); // 系统时间：用于上传到服务器

        // 生成随机数
        StringBuffer buffer1 = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb1 = new StringBuffer();
        Random randNum = new Random();
        int range = buffer1.length();
        for (int i = 0; i < 5; i ++) {
            sb1.append(buffer1.charAt(randNum.nextInt(range)));
        }
        // 生成随机数的结果
        String randStr = sb1.toString();
        // 存到数据库中的照片名：当前时间 + 随机数（不带后缀）
        str_picName_DataBase = str_current_time + randStr;
        img= "http://123.57.249.60:8080/help_child_t1/record_image/" +str_picName_DataBase+ ".png";//相片服务器路径gong
        // 选择照片的点击事件
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        // "填写特征信息按钮"的点击事件
        btn_featureInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setView(new EditText(getActivity()));
                alertDialog.show();
                window = alertDialog.getWindow();
                WindowManager m = window.getWindowManager();
                Display d = m.getDefaultDisplay();
                WindowManager.LayoutParams p = window.getAttributes();
                p.height = (int) (d.getHeight() * 0.49);
                p.width = (int) (d.getWidth() * 0.9);
                window.setAttributes(p);
                window.setContentView(R.layout.custom_dialog);
                edt_feature = (EditText) window.findViewById(R.id.edt_feature);
                btn_window_feature = (Button) window.findViewById(R.id.btn_feature);
                edt_feature.setText(str_edt_feature);

                btn_window_feature.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_edt_feature = edt_feature.getText().toString();
                        new_btn_up_feature = str_edt_feature;
                        alertDialog.dismiss();
                    }
                });
            }
        });

        // “输入丢失时间”的点击事件
        btn_LossTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(initEndDateTime);
                dateTimePicKDialog.dateTimePicKDialog(btn_LossTime);
            }
        });

        // "确定上传"按钮的点击事件
        btn_Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNormalonline = NetWork.checkNetWorkStatus(getActivity());
                if(isNormalonline)
                {
                    if (!(edt_child.getText().toString().equals("")) && !(edt_parent.getText().toString().equals("")) && !(edt_location.getText().toString().equals("")) && !(edt_address.getText().toString().equals("")) && !(str_LossTime.equals("")))
                    {
                        // 在内存卡中创建一个临时的文件夹路径存放照片
                        temp_path = Environment.getExternalStorageDirectory() + "/new_finger/";
                        String new_picPath = temp_path + picName_png;
                        if (new_picPath.length() > 0) {
                             UploadFileTask uploadFileTask = new UploadFileTask(getActivity(),"http://123.57.249.60:8080/help_child_t1/FileRecord");
                            // UploadFileTask uploadFileTask = new UploadFileTask(getActivity(),"http://10.100.15.200:8080/help_child_t1/FileRecord");
                            //UploadFileTask uploadFileTask = new UploadFileTask(getActivity(), "http://10.100.34.108:8080/help_child_t1/FileRecord");
                            uploadFileTask.execute(new_picPath);
                        }
                        // 默认特征信息为无
                        if (new_btn_up_feature == null || new_btn_up_feature.length() == 0) {
                            new_btn_up_feature = "无";
                        }
                        params.put("de", new_btn_up_feature);
                        params.put("cn", edt_child.getText().toString());
                        params.put("pn", edt_parent.getText().toString());
                        params.put("rl", edt_location.getText().toString());
                        params.put("ra", edt_address.getText().toString());
                        params.put("ri", str_picName_DataBase);
                        params.put("method", "record");
                        params.put("loss_time", str_LossTime);
                        params.put("current_time", str_system_time);
                        params.put("r", "android");
                        msg="姓名："+edt_child.getText().toString()+"联系人："+edt_parent.getText().toString()+"联系方式：" + edt_address.getText().toString()+ "特征信息："+new_btn_up_feature;
                        msg1=msg.replaceAll(" ", "");
                        new Thread() {
                            @Override
                            public void run() {
                                InputStream is = null;
                                //String url1 = "http://10.100.34.108:8080/help_child_t1/childservice";
                                // String url1 = "http://10.100.15.200:8080/help_child_t1/childservice";
                                 String url1 = "http://123.57.249.60:8080/help_child_t1/childservice";
                                try {
                                    is = NetTool.getInputStreamByPost(url1, params, "UTF-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                byte[] data = new byte[0];
                                try {
                                    data = NetTool.readStream(is);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.i("打印信息", new String(data));
                                if (NetTool.ifcodetrue) {
                                    myhandle.sendEmptyMessage(0x1211);
                                }
                            }
                        }.start();
                    }
                    else {
                        Toast.makeText(getActivity(), "您填写的信息不完整！请重新填写！", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "网络不通！请检查网络！", Toast.LENGTH_SHORT).show();
                }

                // 上传成功之后把控件中的内容清空
                str_edt_feature = "";
                edt_child.setText("");
                edt_parent.setText("");
                edt_address.setText("");
                edt_location.setText("");
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.uploadimage_record));

            }
        });

    }


    // 重写onActivityResult方法，用来接收回传的数据：Activity.RESULT_OK = - 1
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    if (data != null)
                        startPhotoZoom(data.getData(), 150);
                    break;
                case 2:
                    if (data != null)
                        setPicToView(data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            byte[] pic_data = null;
            Bitmap photo = bundle.getParcelable("data");
            pic_data = getBitmapByte(photo);
            //Drawable drawable = new BitmapDrawable(photo);
            imageView.setImageBitmap(photo);
            picName_png = str_picName_DataBase + ".png";
            try {
                SaveToSDCard(pic_data, picName_png);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, 2);

    }

    public static void SaveToSDCard(byte[] data,String file) throws IOException
    {
        File fileFolder = new File(Environment.getExternalStorageDirectory()
                + "/new_finger/");

        // 如果目录不存在，则创建一个名为"finger"的目录
        if (!fileFolder.exists()) {
            fileFolder.mkdir();
        }
        final File jpgFile = new File(fileFolder, file);
        // 文件输出流
        FileOutputStream outputStream = new FileOutputStream(jpgFile);
        // 写入sd卡中
        outputStream.write(data);
        // 关闭输出流
        outputStream.close();

    }

    // 该方法用于将Bitmap格式转成二进制流
    private byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    private void alert() {
        Dialog dialog = new AlertDialog.Builder(this.getActivity())
                .setTitle("提示")
                .setMessage("您选择的不是有效的图片")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                picPath = null;
                            }
                        }).create();
        dialog.show();
    }
    private void showShare(String imgPath,String msgText) {
        ShareSDK.initSDK(this.getContext());
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
        oks.setTitleUrl("http://123.57.249.60:8080/helpchildshare/index.html?img="+imgPath+"&msg="+msg);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(msgText);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://123.57.249.60:8080/helpchildshare/index.html?img="+imgPath+"&msg="+msg);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://123.57.249.60:8080/helpchildshare/index.html?img="+imgPath+"&msg="+msg);
        oks.setImageUrl(imgPath);
        // 启动分享GUI
        oks.show(this.getContext());
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        File fileFolder = new File(Environment.getExternalStorageDirectory()
                + "/new_finger/");

        // 如果目录不存在，则创建一个名为"finger"的目录
        if (fileFolder.exists()) {
            deleteFile(fileFolder);
        }

        super.onDestroyView();
    }

    // 删除指定路径的文件
    public static void  deleteFile(File file)
    {
        if((Environment.getExternalStorageState()).equals(Environment.MEDIA_MOUNTED))
        {
            if (file.exists())
            {
                if (file.isFile())
                {
                    file.delete();
                }
                // 如果它是一个目录
                else if (file.isDirectory())
                {
                    // 声明目录下所有的文件 files[];
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++)
                    { // 遍历目录下所有的文件
                        deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
                file.delete();
            }}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // 日期选择器类
    public class DateTimePickDialogUtil implements OnDateChangedListener {
        public DatePicker datePicker;
        public AlertDialog ad;
        public String initDateTime;

        public DateTimePickDialogUtil( String initDateTime) {
            this.initDateTime = initDateTime;
        }

        public void init(DatePicker datePicker) {
            Calendar calendar = Calendar.getInstance();
            if (!(null == initDateTime || "".equals(initDateTime))) {
                calendar = this.getCalendarByInintData(initDateTime);
            } else {
                initDateTime = calendar.get(Calendar.YEAR) + "年"
                        + calendar.get(Calendar.MONTH) + "月"
                        + calendar.get(Calendar.DAY_OF_MONTH);
            }

            datePicker.init(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), this);
        }

        public AlertDialog dateTimePicKDialog(final TextView inputDate) {
            LinearLayout dateTimeLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.common_datetime, null);
            datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);

            init(datePicker);

            ad = new AlertDialog.Builder(getActivity())
                    .setTitle(initDateTime)
                    .setView(dateTimeLayout)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            inputDate.setText(dateTime);
                            inputDate.setTextColor(getResources().getColor(R.color.black));
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            inputDate.setText("输入丢失时间");
                            inputDate.setTextColor(getResources().getColor(R.color.inputData));
                        }
                    }).show();

            onDateChanged(null, 0, 0, 0);
            return ad;
        }

        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // 获得日历实例
            Calendar calendar = Calendar.getInstance();
            calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            SimpleDateFormat sdf_upload = new SimpleDateFormat("yyyyMMdd");
            dateTime = sdf.format(calendar.getTime());
            // 获取登记界面用户手动选择的丢失时间
            str_LossTime = sdf_upload.format(calendar.getTime());
            ad.setTitle(dateTime);
        }

        private Calendar getCalendarByInintData(String initDateTime) {
            Calendar calendar = Calendar.getInstance();
            String date = spliteString(initDateTime, "日", "index", "front"); // 日期
            String yearStr = spliteString(date, "年", "index", "front"); // 年份
            String monthAndDay = spliteString(date, "年", "index", "back"); // 月日
            String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
            String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日
            int currentYear = Integer.valueOf(yearStr.trim()).intValue();
            int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
            int currentDay = Integer.valueOf(dayStr.trim()).intValue();
            calendar.set(currentYear, currentMonth, currentDay);
            return calendar;
        }

        public String spliteString(String srcStr, String pattern, String indexOrLast, String frontOrBack)
        {
            String result = "";
            int loc = -1;
            if (indexOrLast.equalsIgnoreCase("index")) {
                loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
            } else {
                loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
            }
            if (frontOrBack.equalsIgnoreCase("front")) {
                if (loc != -1)
                    result = srcStr.substring(0, loc); // 截取子串
            } else {
                if (loc != -1)
                    result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
            }
            return result;
        }


    }

}
