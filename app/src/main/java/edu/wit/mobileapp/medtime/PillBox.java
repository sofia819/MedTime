package edu.wit.mobileapp.medtime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PillBox extends AppCompatActivity{

    private DataStorage db = new DataStorage(this);
    private ArrayList<Pill> pills;
    private String logTag = "myApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill_box);

        pills = db.getAllPills();

        LinearLayout layout = (LinearLayout) findViewById(R.id.pill_display);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        for(int i = 0; i < pills.size(); i++){
            View pillInfo = inflater.inflate(R.layout.pill_item, null);
            Pill cur = pills.get(i);
            String name = cur.getName();
            int dosage = cur.getDosage();
            String food = cur.getFood() == 1? "Yes" : "No";
            int daysIntv = cur.getDaysIntv();

            TextView text = (TextView)pillInfo.findViewById(R.id.med_name);
            text.setText(name);

            text = (TextView)pillInfo.findViewById(R.id.dosage);
            text.setText("Dosage: " + dosage);


            text = (TextView)pillInfo.findViewById(R.id.food);
            text.setText("Food: " + food);


            text = (TextView)pillInfo.findViewById(R.id.daysIntv);
            text.setText("Interval: " + daysIntv);

            text = (TextView)pillInfo.findViewById(R.id.pill_time);
            text.setText(cur.getTime());

            layout.addView(pillInfo, i);
        }

    }

    public void toAddNewPill(View v){
        Intent intent = new Intent();
        intent.setClass(PillBox.this, AddPill.class);
        startActivity(intent);
    }

    public void toSchedule(View v){
        Intent intent = new Intent();
        intent.setClass(PillBox.this, Schedule.class);
        startActivity(intent);
    }
/*
    public void deletePill(View v){
        final LinearLayout layout = (LinearLayout)findViewById(R.id.pill_display);
        final LinearLayout box = (LinearLayout)v.getParent().getParent();
        new AlertDialog.Builder(this)
                .setTitle("Caution")
                .setMessage(R.string.delete_warning)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    // confirm clear
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // set up alert box for delete confirmation
                        TextView name = (TextView)findViewById(R.id.med_name);
                        String medName = name.getText().toString();
                        Log.v(logTag, String.format("Name: %s", medName));
                        db.deletePill(medName);
                        layout.removeView(box);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }*/
}
