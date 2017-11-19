package com.le.help_child.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.le.help_child.R;
import com.le.help_child.util.DataCleanManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ToolsActivity extends AppCompatActivity {
    String get_para = "";
    String cache_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        Intent intent = getIntent();
        get_para = intent.getStringExtra("paras");
        final DataCleanManager cache = new DataCleanManager();
        try {
            cache_size = cache.getTotalCacheSize(ToolsActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView tv = (TextView) findViewById(R.id.name);
        tv.setText(get_para);
        Button btn_clear = (Button) findViewById(R.id.clear);
        assert btn_clear != null;
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView cache_t = new TextView(ToolsActivity.this);
                cache_t.setText("缓存" + cache_size);
                cache_t.setTextSize(20);
                cache_t.setGravity(Gravity.CENTER);
                new AlertDialog.Builder(ToolsActivity.this)
                        .setTitle("缓存")
                        .setView(cache_t)
                        .setPositiveButton("清除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cache.clearAllCache(ToolsActivity.this);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        Button back = (Button) findViewById(R.id.back_info);
        assert back != null;
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText back_et = new EditText(ToolsActivity.this);
                back_et.setLines(10);
                back_et.setTextSize(15);
                new AlertDialog.Builder(ToolsActivity.this)
                        .setTitle("您的建议")
                        .setView(back_et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        save(back_et.getText().toString());
                                    }
                                }.start();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        Button thanks = (Button) findViewById(R.id.thanks);
        assert thanks != null;
        thanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView thanks_tv = new TextView(ToolsActivity.this);
                thanks_tv.setText("特别感谢Face++,阿里云,极光推送,\n高德地图为我们提供开源接口！");
                thanks_tv.setTextSize(18);
                thanks_tv.setGravity(Gravity.CENTER);
                thanks_tv.setPadding(10, 10, 10, 0);
                new AlertDialog.Builder(ToolsActivity.this)
                        .setTitle("感谢")
                        .setView(thanks_tv)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
        Button about = (Button) findViewById(R.id.about);
        assert about != null;
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView about_tv = new TextView(ToolsActivity.this);
                about_tv.setText("本系统为完全公益性系统,\n致力于让更多的失踪儿童回家\n开发团队：北京建筑大学智慧城市研究所\n开发人员：姚远,龚晓东,冯亚飞,程昊,付乐\n指导老师：刘建华");
                about_tv.setTextSize(18);
                about_tv.setPadding(10, 10, 10, 0);
                new AlertDialog.Builder(ToolsActivity.this)
                        .setTitle("关于我们")
                        .setView(about_tv)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });

    }

    //将意见存储到服务器上
    private Boolean save(String s) {
        String url = "http://123.57.249.60:8080/help_child_t1/suggest?sug=" + Uri.encode(s);
        boolean stat = false;
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
            if (line.equals("success")) {
                stat = true;
            } else {
                stat = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat;
    }
}
