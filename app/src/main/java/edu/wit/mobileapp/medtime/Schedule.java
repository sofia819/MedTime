package edu.wit.mobileapp.medtime;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.Calendar;

public class Schedule extends AppCompatActivity {

    private DataStorage db = new DataStorage(this);
    private ExpandableListView expandedSchedule;
    private Calendar calendar;
    private ExpandableListAdapter myListAdapter;
    private String logTag = "myApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        expandedSchedule = (ExpandableListView)findViewById(R.id.schedule);
        myListAdapter = new ExpandableListAdapter(this, db.getSchedule());
        expandedSchedule.setAdapter(myListAdapter);

        for(int i = 0; i < myListAdapter.getGroupCount(); i++){
            Log.v(logTag, myListAdapter.getGroup(i) + " " + myListAdapter.shouldExpand(i));
            if(myListAdapter.shouldExpand(i)){
                Log.v("Expand: %s", (String)myListAdapter.getGroup(i));
                expandedSchedule.expandGroup(i, true);
            }
        }
    }

    public void toPillBox(View v){
        Intent intent = new Intent();
        intent.setClass(Schedule.this, PillBox.class);
        startActivity(intent);
    }

}
