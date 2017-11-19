package com.le.help_child.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.le.help_child.R;
import com.le.help_child.util.UploadFileTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@SuppressWarnings("ALL")
public class CameraActivity extends Fragment {
    private Camera camera;
    private Camera.Parameters parameters = null;
    Bundle bundle = null;
    ImageButton bnt_takecamera;
    Button bnt_sure;
    Button bnt_cancel;
    int IS_TOOK = 0; //是否拍照
    String time;
    String imgN;
    String loc;
    public double latitude = 0.0;//纬度
    public double longitude = 0.0;//经度

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
        return inflater.inflate(R.layout.activity_camera, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getl();
        bnt_takecamera = (ImageButton) this.getView().findViewById(R.id.take_camera);
        bnt_sure = (Button) this.getView().findViewById(R.id.sure);
        bnt_cancel = (Button) this.getView().findViewById(R.id.cancel);

        bnt_takecamera.setVisibility(View.VISIBLE);
        bnt_sure.setVisibility(View.INVISIBLE);
        bnt_cancel.setVisibility(View.INVISIBLE);

        final SurfaceView surfaceView = (SurfaceView) this.getView().findViewById(R.id.surfaceView);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setFixedSize(176, 144);    //设置Surface分辨率
        surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.autoFocus(null);
            }
        });

        surfaceView.getHolder().addCallback(new SurfaceCallback());//为SurfaceView的句柄添加一个回调函数
        bnt_takecamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (camera != null) {
                    camera.takePicture(null, null, new MyPictureCallback());
                }
            }
        });

        bnt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    StringBuffer buffer1 = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                    StringBuffer sb1 = new StringBuffer();
                    Random r = new Random();
                    int range = buffer1.length();
                    for (int i = 0; i < 5; i++) {
                        sb1.append(buffer1.charAt(r.nextInt(range)));
                    }
                    String randStr = sb1.toString();
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
                    time = format.format(date);
                    imgN = time + randStr + "HELP"; // 存到数据库中的照片名：当前时间 + 随机数（不带后缀）+H（H表示上传自help）
                    loc = longitude + "," + latitude;
                    String filename = imgN + ".png";
                    saveToSDCard(bundle.getByteArray("bytes"), filename);
                    File fileFolder = new File(Environment.getExternalStorageDirectory()
                            + "/finger/");
                    if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
                        fileFolder.mkdir();
                    }
                    File jpgFile = new File(fileFolder, filename);
                    String path = jpgFile.getAbsolutePath();
                    UploadFileTask uploadFileTask = new UploadFileTask(getActivity(), "http://123.57.249.60:8080/help_child_t1/FileImageUploadServlet");
                    uploadFileTask.execute(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final EditText ett = new EditText((TabActivity) getActivity());
                ett.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);

                new AlertDialog.Builder((TabActivity) getActivity())

                        .setTitle("可以选择留下您的联系方式")
                        //.setIcon(android.R.drawable.ic_dialog_info)
                        .setView(ett)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String phoneNum = ett.getText().toString();
                                new Thread() {
                                    @Override
                                    public void run() {
                                        String re = postparas(loc, imgN, time, phoneNum);
                                    }
                                }.start();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        String re1 = postparas(loc, imgN, time, "");
                                    }
                                }.start();
                            }
                        })
                        .show();
                Toast.makeText((TabActivity) getActivity(), "您的照片已经上传", Toast.LENGTH_SHORT).show();
                bnt_takecamera.setVisibility(View.VISIBLE);
                bnt_sure.setVisibility(View.INVISIBLE);
                bnt_cancel.setVisibility(View.INVISIBLE);
                camera.startPreview();
            }
        });

        bnt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bnt_takecamera.setVisibility(View.VISIBLE);
                bnt_sure.setVisibility(View.INVISIBLE);
                bnt_cancel.setVisibility(View.INVISIBLE);
                if (camera != null) {
                    IS_TOOK = 0;
                    camera.startPreview();
                }
            }
        });

    }

    //获取位置
    private void getl() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } else {
                LocationListener locationListener = new LocationListener() {

                    // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                    @Override
                    public void onStatusChanged(String provider, int status,
                                                Bundle extras) {

                    }

                    // Provider被enable时触发此函数，比如GPS被打开
                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    // Provider被disable时触发此函数，比如GPS被关闭
                    @Override
                    public void onProviderDisabled(String provider) {

                    }

                    // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            Log.e("Map",
                                    "Location changed : Lat: "
                                            + location.getLatitude() + " Lng: "
                                            + location.getLongitude());
                            latitude = location.getLatitude(); // 经度
                            longitude = location.getLongitude(); // 纬度
                        }
                    }
                };
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 1000, 0,
                        locationListener);
                Location location1 = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location1 != null) {
                    latitude = location1.getLatitude(); // 经度
                    longitude = location1.getLongitude(); // 纬度
                }
            }
        }
    }

    // 上传相机参数
    private String postparas(String loc, String imgN, String time, String phoneNum) {
        String url = "http://123.57.249.60:8080/help_child_t1/childservice?method=camera&r=android&hl=" + loc + "&ha=" + phoneNum + "&hi=" + imgN + "&ht=" + time;
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
            result = line;

        } catch (Exception e) {
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
                + "/finger/");

        // 如果目录不存在，则创建一个名为"finger"的目录
        if (fileFolder.exists()) {
            deleteFile(fileFolder);
        }
        super.onDestroyView();
    }

    public static void deleteFile(File file) {
        if ((Environment.getExternalStorageState()).equals(Environment.MEDIA_MOUNTED)) {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                }
                // 如果它是一个目录
                else if (file.isDirectory()) {
                    // 声明目录下所有的文件 files[];
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
                file.delete();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                bundle = new Bundle();
                bundle.putByteArray("bytes", data);    //将图片字节数据保存在bundle当中，实现数据交换
                camera.stopPreview();  //拍完照后，停止预览
                bnt_takecamera.setVisibility(View.INVISIBLE);
                bnt_sure.setVisibility(View.VISIBLE);
                bnt_cancel.setVisibility(View.VISIBLE);
                IS_TOOK = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveToSDCard(byte[] data, String file) throws IOException {
        final String[] result = new String[1];
        File fileFolder = new File(Environment.getExternalStorageDirectory()
                + "/finger/");
        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
            fileFolder.mkdir();
        }
        final File jpgFile = new File(fileFolder, file);

        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
        outputStream.write(data); // 写入sd卡中
        outputStream.close(); // 关闭输出流
    }

    private final class SurfaceCallback implements Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            TabActivity activity = ((TabActivity) getActivity());
            try {
                camera = Camera.open(); // 打开摄像头
                camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象
                camera.setDisplayOrientation(getPreviewDegree(activity));
                camera.startPreview(); // 开始预览
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            PackageManager pm = getActivity().getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED ==
                    pm.checkPermission("android.permission.CAMERA", "com.le.help_child"));
            if (permission) {
                try {
                    parameters = camera.getParameters(); // 获取各项参数
                    parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
                    parameters.setPreviewSize(width, height); // 设置预览大小
                    parameters.setPreviewFrameRate(5);    //设置每秒显示4帧
                    parameters.setPictureSize(width, height); // 设置保存的图片尺寸
                    parameters.setJpegQuality(80); // 设置照片质量
                } catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), "您没有打开相机权限！", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "您没有打开相机权限！", Toast.LENGTH_LONG).show();
                getActivity().finish();
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            if (camera != null) {
                camera.release(); // 释放照相机
                camera = null;
            }

        }
    }

    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }
}


