package com.example.zhangheng1.camera.adapter;

import android.content.Context;
import android.widget.SimpleAdapter;

import com.example.zhangheng1.camera.R;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by yuyidong on 14-12-23.
 */
public class AwbAdapter {

    public static SimpleAdapter getAdapter(Context context) {
        ArrayList<HashMap<String, String>> listItem = getSenseList();
        SimpleAdapter listItemAdapter = new SimpleAdapter(context, listItem, R.layout.item_lv_menu, new String[]{"text"}, new int[]{R.id.txt_item_menu});
        return listItemAdapter;
    }

    private static ArrayList<HashMap<String, String>> getSenseList() {
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("text", "自动");
        listItem.add(map1);
        HashMap<String, String> map2 = new HashMap<String, String>();
        map2.put("text", "多云");
        listItem.add(map2);
        HashMap<String, String> map3 = new HashMap<String, String>();
        map3.put("text", "白天");
        listItem.add(map3);
        HashMap<String, String> map4 = new HashMap<String, String>();
        map4.put("text", "日关灯");
        listItem.add(map4);
        HashMap<String, String> map5 = new HashMap<String, String>();
        map5.put("text", "白炽灯");
        listItem.add(map5);
        HashMap<String, String> map6 = new HashMap<String, String>();
        map6.put("text", "阴影");
        listItem.add(map6);
        HashMap<String, String> map7 = new HashMap<String, String>();
        map7.put("text", "黄昏");
        listItem.add(map7);
        HashMap<String, String> map8 = new HashMap<String, String>();
        map8.put("text", "暖光");
        listItem.add(map8);


        return listItem;
    }
}
