/**
 * Sofia Lee
 */
package edu.wit.mobileapp.medtime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DataStorage extends SQLiteOpenHelper {

    private String logTag = "myApp";
    private static final String dbName = "MedDB";
    private static final int dbVersion = 1;
    private static final String medInfo = "medInfo", medTime = "medTime";
    private String[] medInfoCol = {"name", "dosage", "food", "daysIntv", "elapsed"};
    private String[] medTimeCol = {"name", "hour", "minute"};

    public DataStorage(Context context){
        super(context, dbName, null, dbVersion);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String medInfo = "Create Table medInfo(name VARCHAR PRIMARY KEY, " +
                         "dosage INTEGER, food INTEGER, daysIntv INTEGER, elapsed INTEGER)";
        String medTime = "Create Table medTime(name VARCHAR, hour INTEGER, minute INTEGER, " +
                         "FOREIGN KEY(name) REFERENCES medInfo(name))";

        sqLiteDatabase.execSQL(medInfo);
        sqLiteDatabase.execSQL(medTime);
    }

    public boolean addMed(String name, int dosage, int food, int daysIntv, int elapsed, int[] hour, int[] minute){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        // insert med info and create id
        // returns primary key
        contentValues.put("name", name);
        contentValues.put("dosage", dosage);
        contentValues.put("food", food);
        contentValues.put("daysIntv", daysIntv);
        contentValues.put("elapsed", elapsed);

        db.insert(medInfo, null, contentValues);
        contentValues.clear();

        // insert med times to table
        contentValues.put("name", name);
        for(int i = 0; i < hour.length; i++){
            contentValues.put("hour", hour[i]);
            contentValues.put("minute", minute[i]);
            db.insert(medTime, null, contentValues);
        }

        db.close();
        return true;
    }

    public ArrayList<Pill> getAllPills(){
        String query, timeQuery;
        query = String.format("Select * from %s Order by name", medInfo);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Cursor timeCursor;

        ArrayList<Pill> pills = new ArrayList<>();

        Log.v("myApp", "# Rows: " + cursor.getCount());
        if(cursor != null && cursor.moveToFirst()) {
            pills = new ArrayList<>();
            int id, dosage, food, daysIntv, elapsed;
            String name;
            do{
                name = cursor.getString(cursor.getColumnIndex(medInfoCol[0]));
                dosage = Integer.parseInt(cursor.getString(cursor.getColumnIndex(medInfoCol[1])));
                food = Integer.parseInt(cursor.getString(cursor.getColumnIndex(medInfoCol[2])));
                daysIntv = Integer.parseInt(cursor.getString(cursor.getColumnIndex(medInfoCol[3])));
                elapsed = Integer.parseInt(cursor.getString(cursor.getColumnIndex(medInfoCol[4])));

                timeQuery = String.format("Select * from %s where name = \"%s\"", medTime, name);
                timeCursor = db.rawQuery(timeQuery, null);
                timeCursor.moveToFirst();
                int[] hour = new int[timeCursor.getCount()];
                int[] minute = new int[timeCursor.getCount()];
                for(int i = 0; i < hour.length; i++){
                    hour[i] = Integer.parseInt(timeCursor.getString(timeCursor.getColumnIndex(medTimeCol[1])));
                    minute[i] = Integer.parseInt(timeCursor.getString(timeCursor.getColumnIndex(medTimeCol[2])));
                    Log.v(logTag, String.format("%02d:%02d", hour[i], minute[i]));
                    timeCursor.moveToNext();
                }

                pills.add(new Pill(name, dosage, food, daysIntv, elapsed, hour, minute));

            } while(cursor.moveToNext());
        }
        return pills;
    }

    public boolean isNameAvailable(String n){
        String name = String.format("\"%s\"", n);
        String query = "";
        query = String.format("Select * from %s where name = %s", medInfo, name);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        boolean result =  cursor.getCount() == 0; // if (count == 0) is true, then it's available.

        cursor.close();

        return result;
    }

    public void reset(){
        SQLiteDatabase db = getWritableDatabase();

        String drop;

        drop = String.format("Drop Table if EXISTS %s", medInfo);
        db.execSQL(drop);

        drop = String.format("Drop Table if EXISTS %s", medTime);
        db.execSQL(drop);

        onCreate(db);

        Log.v("myApp", "DB reset");
    }

    public ArrayList<PillTime> getSchedule(){

        String query = "";
        query = String.format("Select * from %s Order by hour, minute, name", medTime);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<PillTime> schedule = new ArrayList<>();
        ArrayList<Pill> pills = getAllPills();

        Log.v("myApp", "# Rows: " + cursor.getCount());
        if(cursor != null && cursor.moveToFirst()) {
            schedule = new ArrayList<>();
            int lastIndex, hour, minute;
            String name;

            do{
                lastIndex =  schedule.size() - 1;

                name = cursor.getString(cursor.getColumnIndex(medTimeCol[0]));
                hour = Integer.parseInt(cursor.getString(cursor.getColumnIndex(medTimeCol[1])));
                minute = Integer.parseInt(cursor.getString(cursor.getColumnIndex(medTimeCol[2])));

                if(schedule.size() == 0 || !schedule.get(lastIndex).isTimeEqual(hour, minute)){
                    schedule.add(new PillTime(hour, minute));
                }

                lastIndex =  schedule.size() - 1;

                for(int i = 0; i < pills.size(); i++){
                    if(pills.get(i).getName().equals(name)){
                        schedule.get(lastIndex).addPill(pills.get(i));
                    }
                }

            } while(cursor.moveToNext());
        }
        return schedule;
    }

    public void deletePill(String n){
        SQLiteDatabase db = getReadableDatabase();

        String name = String.format("\"%s\"", n);
        // delete from medTime
        String table = medTime;
        db.delete(table, "name = " + name, null);

        // delete from medInfo
        table = medInfo;
        db.delete(table, "name = " + name, null);

        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String medList = "Drop Table if EXISTS medList";
        String medInfo = "Drop Table if EXISTS medInfo";
        String medTime = "Drop Table if EXISTS medTime";

        sqLiteDatabase.execSQL(medList);
        sqLiteDatabase.execSQL(medInfo);
        sqLiteDatabase.execSQL(medTime);

        onCreate(sqLiteDatabase);
    }
}
