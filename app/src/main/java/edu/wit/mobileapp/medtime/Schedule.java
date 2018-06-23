package edu.wit.mobileapp.medtime;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Schedule extends AppCompatActivity implements View.OnClickListener{

    private String displayFormat = "yyyy-MM-dd";
    private SimpleDateFormat sdf = new SimpleDateFormat(displayFormat);
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        date = new Date(); // Get today's date

        updateDate(0);    // Display today's date

        Button back = (Button)findViewById(R.id.back);
        Button forward = (Button)findViewById(R.id.forward);

        back.setOnClickListener(this);
        forward.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        buttonClicked(view);
    }

    public void buttonClicked(View view) {
        if (view.getId() == R.id.back) {
            updateDate(-1);
        } else if (view.getId() == R.id.forward) {
            updateDate(1);
        }
    }

    /*
    * Update date displayed
    * i: days to add to date
    * */
    private void updateDate(int i){
        Calendar c = Calendar.getInstance();

        c.setTime(date); // Take date from dt
        c.add(Calendar.DATE, i); // Adding i days
        date = c.getTime();

        String str = sdf.format(date); // Convert date to String

        TextView displayDate = (TextView) findViewById(R.id.display_date);
        displayDate.setText(str);
    }


}
