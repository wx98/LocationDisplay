package com.wx1998.locationdisplay;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserList extends AppCompatActivity {

    public static List<String> girList;
    public static ListView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        textView = findViewById(R.id.tv1);
        girList = new ArrayList<>();
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        girList.add("test1");
        girList.add("test2");
        girList.add("test4");
        girList.add("test3");
        girList.add("123123");
        girList.add("4444444");
        textView.setAdapter(new MyAdapter());

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                ToastUtil.showShortToast(MainActivity.getInstance(),girList.get(arg2));
            }
        });
    }


    //适配器
    static class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return girList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView tv = new TextView(MainActivity.getInstance());

            tv.setTextSize(24);
            //获取集合中的元素
            String girl = girList.get(position);
            tv.setText(girl.toString());
            return tv;
        }

    }
}