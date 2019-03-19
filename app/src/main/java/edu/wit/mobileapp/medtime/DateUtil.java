/**
 * Sofia Lee
 */
package edu.wit.mobileapp.medtime;

import java.util.Calendar;

public class DateUtil {
    private Calendar cal;

    public DateUtil(){
        cal = Calendar.getInstance();
    }

    /**
     * Returns this + days in String.
     * Calendar object does not change.
     */
    public String addDays(int days){
        cal.add(Calendar.DATE, days);
        String dt = toString();

        cal.add(Calendar.DATE, -days);
        return dt;
    }


    /**
     * The purpose of this method is to compare a smaller date to a bigger date.
     * This method assumes other is always bigger.
     * This will be used when user changes start date in input.
     * Neither this nor other's Calendar object will be changed,
     * @param other: date to compare this to.
     * @return the difference between DateUtil objects.
     */
    public int calcDiff(DateUtil other){
        int diff = 0;
        while(!addDays(diff).equals(other.toString())){
            diff++;
        }
        return -diff;
    }

    public void setDate(int y, int m, int d){
        cal.set(y, m, d);
    }

    public int getDate(){
        return cal.get(Calendar.DATE);
    }

    public int getMonth(){
        return cal.get(Calendar.MONTH);
    }

    public int getYear(){
        return cal.get(Calendar.YEAR);
    }

    public String toString(){
        int date = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        String dt = String.format("%d-%d-%d", year, month, date);
        return dt;
    }
}
