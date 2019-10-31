package com.androiddeft.loginandregistration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class TimeTable extends AppCompatActivity {

    //private EditText timetable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        /*Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<Object> object = (ArrayList<Object>) args.getSerializable("TimeTable");
        timetable = (EditText) findViewById(R.id.timetable_show);
        timetable.setText((CharSequence) object);*/
    }
}
