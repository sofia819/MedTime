package edu.wit.mobileapp.medtime;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PillTime {
    private int hour, minute;
    private ArrayList<Pill> pills;

    public PillTime(int h, int m){
        this.hour = h;
        this.minute = m;
        pills = new ArrayList<>();
    }

    public void addPill(Pill p){
        pills.add(p);
    }

    public ArrayList<Pill> getPills(){
        return pills;
    }

    public int getHour(){
        return hour;
    }

    public int getMinute(){
        return minute;
    }

    public boolean isTimeEqual(int h, int m){
        return getHour() == h && getMinute() == m;
    }

    public String portionOfDay(){
        // morning 4-11:59
        // afternoon 12-17:59
        // evening 18-21:59
        // night 22-3:59

        if(isBetween(new PillTime(4, 0), new PillTime(11, 59)))
            return "Morning";

        if(isBetween(new PillTime(12, 0), new PillTime(17, 59)))
            return "Afternoon";

        if(isBetween(new PillTime(18, 0), new PillTime(21, 59)))
            return "Evening";

        return "Night";
    }

    public boolean beforeTime(PillTime p){
        return getHour() <= p.getHour();
    }

    public boolean afterTime(PillTime p){
        return getHour() >= p.getHour();
    }

    public boolean isBetween(PillTime lower, PillTime upper){
        return afterTime(lower) && beforeTime(upper);
    }

    public String getPillsName(){
        String ret = "";
        for(int i = 0; i < pills.size(); i++)
            ret += pills.get(i).getName() + "\t";
        return ret;
    }

    public String getTimeStamp(){
        String timeStamp = String.format("%02d:%02d", getHour(), getMinute());
        return timeStamp;
    }

    public String toString(){
        String ret = String.format("%s|%d|%d\n", portionOfDay(), getHour(), getMinute());
        ret += pills.toString();
        return ret;
    }
}
