/**
 * Sofia Lee
 */
package edu.wit.mobileapp.medtime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;

public class AddPill extends AppCompatActivity {

    private String logTag = "myApp";
    private EditText medNameText;
    private CheckBox foodOptionCheck;
    private Button minusDosageBtn, plusDosageBtn, minusDayBtn, plusDayBtn;
    private TextView numDayIntvText, numDosageText, dosagePrompt, dayPrompt, startDate, nextDate;
    private int minDosage = 1, maxDosage = 10, minDay = 1, maxDay = 7;
    private DataStorage db = new DataStorage(this);
    private DateUtil today, startDtInput;
    private ArrayList<PillTime> schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill);

        medNameText = (EditText) findViewById(R.id.med_name);

        foodOptionCheck = (CheckBox) findViewById(R.id.take_with_food);

        minusDayBtn = (Button)findViewById(R.id.minusDay);
        plusDayBtn = (Button) findViewById(R.id.plusDay);
        numDayIntvText = (TextView)findViewById(R.id.numDay);
        dayPrompt = (TextView)findViewById(R.id.interval_prompt);
        dayPrompt.setText(String.format("Interval (%d - %d)", minDay, maxDay));

        minusDosageBtn = (Button)findViewById(R.id.minusDosage);
        plusDosageBtn = (Button)findViewById(R.id.plusDosage);
        numDosageText = (TextView)findViewById(R.id.numDosage);
        dosagePrompt = (TextView)findViewById(R.id.dosage_prompt);
        dosagePrompt.setText(String.format("Dosage (%d - %d)", minDosage, maxDosage));

        startDate = (TextView)findViewById(R.id.start_date);
        nextDate = (TextView)findViewById(R.id.next_date);

        today = new DateUtil();
        startDtInput = new DateUtil();
        updateDates(1);

        // db.reset();
    }

    /**
     * Resets activity.
     */
    private void resetActivity(){
        medNameText.setText("");
        numDayIntvText.setText("1");
        numDosageText.setText("1");
        foodOptionCheck.setChecked(false);

        today = new DateUtil();
        startDtInput = new DateUtil();
        updateDates(1);

        LinearLayout timeLayout = (LinearLayout)findViewById(R.id.more_time);
        if(timeLayout != null)
            timeLayout.removeAllViews();
    }

    /**
     * Reset start and next dates.
     */
    private void updateDates(int i){
        setStartDate();
        setNextDate(i);
    }

    /**
     * Lets user pick start date.
     */
    public void onSetStart(View v){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                startDtInput.setDate(y, m, d);
                updateDates(Integer.parseInt(numDayIntvText.getText().toString()));
            }
        },
                startDtInput.getYear(), startDtInput.getMonth(), startDtInput.getDate());
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setStartDate(){
        String text = String.format("Start: \t %s", startDtInput.toString());
        startDate.setText(text);
    }

    private void setNextDate(int intv){
        String text = String.format("Next: \t %s", startDtInput.addDays(intv));
        nextDate.setText(text);
    }

    public void onMinusDay(View v){
        int intv = minus(numDayIntvText, minDay);
        setNextDate(intv);
    }

    public void onPlusDay(View v) {
        int intv = plus(numDayIntvText, maxDay);
        setNextDate(intv);
    }

    public void onMinusDosage(View v){
        minus(numDosageText, minDosage);
    }

    public void onPlusDosage(View v) {
        plus(numDosageText, maxDosage);
    }

    public int minus(TextView t, int min){
        int current = Integer.parseInt(t.getText().toString());
        if (current > min) {
            current--;
            t.setText("" + current);
        }
        return current;
    }

    public int plus(TextView t, int max){
        int current = Integer.parseInt(t.getText().toString());
        if (current < max) {
            current++;
            t.setText("" + current);
        }
        return current;
    }

    /**
     * Removes corresponding time input field.
     */
    public void onRemove(View v){
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.more_time);;
        mainLayout.removeView((View)v.getParent());
    }

    /**
     * Saves the info to the database.
     * Includes checking of information.
     */
    public void onSave(View v){
        // get name, dosage, with food, and days interval
        final String name = medNameText.getText().toString();
        final int dosage = Integer.parseInt(numDosageText.getText().toString());
        final int food = foodOptionCheck.isChecked()? 1 : 0; // true = 1, false = 0
        final int daysIntv = Integer.parseInt(numDayIntvText.getText().toString());
        final int elapsed = today.calcDiff(startDtInput);

        // get med time
        LinearLayout mainLayout = (LinearLayout)findViewById(R.id.more_time);
        int lenTime = mainLayout.getChildCount();
        final int[] hour = new int[lenTime];
        final int[] minute = new int[lenTime];

        // go through every TextView in each LinearLayout in mainLayout
        // to get hour and minute
        for(int i = 0; i < lenTime; i++) {
            View layout = mainLayout.getChildAt(i);
            if (layout instanceof LinearLayout) {
                View text = ((LinearLayout)layout).getChildAt(0);
                text = (TextView)layout.findViewById(R.id.time_added);

                String str = ((TextView)text).getText().toString();
                String[] time = str.split(":");
                hour[i] = Integer.parseInt(time[0]);
                minute[i] = Integer.parseInt(time[1]);
            }
        }

        // check info before saving
        // name is not empty, nor repeated
        if(name == null || name.equals("") || !db.isNameAvailable(name)) {
            Toast.makeText(this, "Please choose a new med name.", Toast.LENGTH_LONG).show();
            Log.v(logTag, "Bad name");
            Log.v(logTag, "" + elapsed);
        }
        else{
            // at least one time field must exist
            if(lenTime < 1){
                Toast.makeText(this, "Please add a time.", Toast.LENGTH_LONG).show();
                Log.v(logTag, "Bad time");
            }
            else{
                // confirm save
                new AlertDialog.Builder(this)
                    .setTitle("Caution")
                    .setMessage(R.string.save_warning)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            // save info to DB
                            Log.v(logTag, name);
                            Log.v(logTag, "dosage: " + dosage);
                            Log.v(logTag, "food: " + food);
                            Log.v(logTag, "interval: " + daysIntv);
                            printTime(hour, minute);
                            db.addMed(name, dosage, food, daysIntv, elapsed, hour, minute);

                            Toast.makeText(getApplicationContext(), name + " saved", Toast.LENGTH_LONG).show();
                            Log.v(logTag, "Name: " + name);

                            toPillBox();
                        }
                    })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        }
    }

    /**
     * Prints to log, the hour and time entered.
     */
    private void printTime(int[] hour, int[] minute){
        for(int i = 0; i < hour.length; i++)
            Log.v(logTag, hour[i] + ":" + minute[i]);
    }

    /**
     * Discards all info entered and return to Pill Box.
     */
    public void onCancel(View v){
        new AlertDialog.Builder(this)
            .setTitle("Caution")
            .setMessage(R.string.cancel_warning)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            // confirm cancel
            public void onClick(DialogInterface dialog, int whichButton) {
                toPillBox();
            }
        })
                .setNegativeButton(android.R.string.no, null).show();
    }

    /**
     * Clears all input fields.
     */
    public void onClear(View v){
        new AlertDialog.Builder(this)
                .setTitle("Caution")
                .setMessage(R.string.clear_warning)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    // confirm clear
                    public void onClick(DialogInterface dialog, int whichButton) {
                        resetActivity();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    /**
     * Starts Activity PillBox.java
     */
    private void toPillBox(){
        Intent intent = new Intent();
        intent.setClass(AddPill.this, PillBox.class);
        startActivity(intent);
    }

    /**
     * https://www.androidtutorialpoint.com/basics/dynamically-add-and-remove-views-in-android/#Layout_for_the_App
     * Adds a new time field when "Add New Time" is clicked
     */
    public void onAdd(View v){
        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.more_time);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View timeView = inflater.inflate(R.layout.add_time_layout, null);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, 2,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        TextView txtTime = timeView.findViewById(R.id.time_added);
                        String time = String.format("%02d:%02d", hourOfDay, minute);
                                txtTime.setText(time);
                        mainLayout.addView(timeView, 0);
                    }
                }, 0, 0, true);
        timePickerDialog.show();
    }
}