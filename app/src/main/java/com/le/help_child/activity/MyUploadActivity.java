package com.le.help_child.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.le.help_child.R;
import com.le.help_child.adapter.urlistAdapter;

import org.json.JSONArray;
import org.json.JSONException;

public class MyUploadActivity extends AppCompatActivity {
    ListView list;
    urlistAdapter adapter;
    String get_para = "";
    JSONArray upload_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_upload);
        //获取String
        Intent intent = getIntent();
        get_para = intent.getStringExtra("paras");
        try {
            upload_array = new JSONArray(get_para);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        list = (ListView) findViewById(R.id.LV_myupload);
        //第一个参数是Activity,第二个参数是JSONArray
        adapter = new urlistAdapter(this, upload_array, 1);
        list.setAdapter(adapter);
        //设置列表的点击时间
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

}
