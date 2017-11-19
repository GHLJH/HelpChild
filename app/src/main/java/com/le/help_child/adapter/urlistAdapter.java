package com.le.help_child.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.le.help_child.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class urlistAdapter extends BaseAdapter {

    private Activity activity;
    private JSONArray data;
    private int p;
    private static LayoutInflater inflater = null;

    public urlistAdapter(Activity a, JSONArray d, int q) {
        activity = a;
        data = d;
        p = q;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length();
        //return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        String url_i;
        View vi = view;
        if (view == null)
            vi = inflater.inflate(R.layout.ru_list, null);
        TextView position1 = (TextView) vi.findViewById(R.id.title); // 地点
        TextView time1 = (TextView) vi.findViewById(R.id.artist); // 时间
        ImageView img = (ImageView) vi.findViewById(R.id.list_image); // 图片
        //解析data的json数组，显示在列表中
        try {
            JSONObject arr = (JSONObject) data.opt(position);
            String po_arr = arr.getString("position");
            String time_arr = arr.getString("time");
            String img_arr = arr.getString("img");
            if (p == 1) {
                url_i = "http://123.57.249.60:8080/help_child_t1/help_image/";
            } else {
                url_i = "http://123.57.249.60:8080/help_child_t1/record_image/";
            }
            position1.setText(po_arr);
            time1.setText(time_arr);
            Picasso.with(activity)
                    .load(url_i + img_arr + ".png")
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(img);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vi;
    }
}
