package com.malaab.ya.action.actionyamalaab.owner.data.model;

public class CalendarDay {

    public String monthName;
    public int day;
    public String dayName;
    public int year;

    public boolean isSelectedByUser;


    public CalendarDay(String monthName, int day, String dayName, int year, boolean isSelectedByUser) {
        this.monthName = monthName;
        this.day = day;
        this.dayName = dayName;
        this.year = year;
        this.isSelectedByUser = isSelectedByUser;
    }
}
