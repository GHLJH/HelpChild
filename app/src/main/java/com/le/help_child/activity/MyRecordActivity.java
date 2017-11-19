package com.le.help_child.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.le.help_child.R;
import com.le.help_child.adapter.urlistAdapter;

import org.json.JSONArray;
import org.json.JSONException;

public class MyRecordActivity extends AppCompatActivity {
    ListView list;
    urlistAdapter adapter;
    String get_para = "";
    JSONArray record_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_record);

        Intent intent = getIntent();
        get_para = intent.getStringExtra("paras");
        try {
            record_array = new JSONArray(get_para);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        list = (ListView) findViewById(R.id.LV_myRecord);
        //第一个参数是Activity,第二个参数是JSONArray
        adapter = new urlistAdapter(this, record_array, 2);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }
}
