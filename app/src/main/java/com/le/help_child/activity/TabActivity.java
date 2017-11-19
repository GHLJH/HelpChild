package com.le.help_child.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.le.help_child.MainActivity;
import com.le.help_child.R;
import com.le.help_child.adapter.FragmentTabAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AppCompatActivity {
    private RadioGroup rgs;
    public List<Fragment> fragments = new ArrayList<Fragment>();
    public int get_para;
    public String info;
    RadioButton camera_Rb, record_Rb, place_Rb, help_Rb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        //*****************************************************************************************************************//
        Intent intent = getIntent();
        // 如果没有取到"para"的值，则默认返回0
        get_para = intent.getIntExtra("para", 0);
        // 要传向反馈模块fragment的值
        info = intent.getStringExtra("help_result");

        // HelpActivity是一个Fragment类
        fragments.add(new CameraActivity());
        fragments.add(new RecordActivity());
        fragments.add(new PlaceActivity());
        fragments.add(new HelpActivity());

        rgs = (RadioGroup) findViewById(R.id.tabs_rg);

        // 底下四个核心按钮的初始化
        camera_Rb = (RadioButton) findViewById(R.id.bottom_camera);
        record_Rb = (RadioButton) findViewById(R.id.bottom_record);
        place_Rb = (RadioButton) findViewById(R.id.bottom_place);
        help_Rb = (RadioButton) findViewById(R.id.bottom_help);

        switch (get_para) {
            case 0:
                camera_Rb.setChecked(true);
                break;
            case 1:
                record_Rb.setChecked(true);
                break;
            case 2:
                place_Rb.setChecked(true);
                break;
            case 3:
                help_Rb.setChecked(true);
                break;
        }
        // 五个参数：上下文，集合容器，承放fragment的FrameLayout，按钮组件，从上一个Activity传进来的参数
        FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tab_content, rgs, get_para);

        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {

            }
        });

    }


}

