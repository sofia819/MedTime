package edu.wit.mobileapp.medtime;

public class Pill {
    private int dosage, food, daysIntv, elapsed;
    private String name;
    private int[] hour, minute;

    public Pill(String name, int dosage, int food, int daysIntv, int elapsed, int[] hour, int[] minute){
        this.name = name;
        this.dosage = dosage;
        this.food = food;
        this.daysIntv = daysIntv;
        this.elapsed = elapsed;
        this.hour = hour;
        this.minute = minute;
    }

    public String getName(){
        return name;
    }

    public int getDosage(){
        return dosage;
    }

    public int getFood(){
        return food;
    }

    public int getDaysIntv(){
        return  daysIntv;
    }

    public int getElapsed(){
        return elapsed;
    }

    public String getTime(){
        String ret = "";
        for(int i = 0; i < hour.length; i++){
            ret += String.format("%02d:%02d", hour[i], minute[i]);
            if(i < hour.length - 1){
                ret += ", ";
            }
        }
        return ret;
    }

    public String toString(){
        String ret = String.format("%s|%d|%d|%d|%d\n", name, dosage, food, daysIntv, elapsed);
        ret += getTime();
        return ret;
    }
}
