package com.example.listviewtype;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private List<People> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv);
        list = new ArrayList<>();
        for (int i = 0;i<20;i++){
            People p = new People(i,"name"+i,"habit:"+i);
            list.add(p);
        }
        MyAdapter adapter = new MyAdapter(this,list);
        lv.setAdapter(adapter);

    }
}
