package com.le.help_child.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

/**
 * Created by le on 2016/5/6.
 */
public class UploadFileTask extends AsyncTask<String, Void, String> {
    // public static final String requestURL="http://10.9.34.126:8080/help_child_t1/FileImageUploadServlet";
    /**
     * 可变长的输入参数，与AsyncTask.exucute()对应
     */
    private ProgressDialog pdialog;
    private Activity context = null;
    public String requestURL = null;

    public UploadFileTask(Activity ctx, String requestURL) {
        this.context = ctx;
        this.requestURL = requestURL;
        pdialog = ProgressDialog.show(context, "正在加载...", "系统正在处理您的请求");
    }

    @Override
    protected void onPostExecute(String result) {
        // 返回HTML页面的内容
        pdialog.dismiss();
        if (UploadUtil.SUCCESS.equalsIgnoreCase(result)) {
            Toast.makeText(context, "上传成功!", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(context, "上传失败!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected String doInBackground(String... strings) {
        File file = new File(strings[0]);
        return UploadUtil.uploadFile(file, requestURL);

    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
