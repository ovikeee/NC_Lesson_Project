package com.netcracker.ssau.restplacesearcher.model;

public class WeatherData {

    private int dateToday;
    private String weekDay;
    private String city;


    private int todayNow;
    private int todayDay;
    private int todayNight;

    private int averageTemperature;
    private int averagePrecipitation;

    public WeatherData() {
    }

    /**
     * @param dateToday - сегодняшнее число
     */
    public WeatherData(int dateToday,
                       String weekDay,
                       String city,
                       int todayNow,
                       int todayDay,
                       int todayNight,
                       int averageTemperature,
                       int averagePrecipitation) {
        this.dateToday = dateToday;
        this.weekDay = weekDay;
        this.city = city;
        this.todayDay = todayDay;
        this.todayNow = todayNow;
        this.todayNight = todayNight;
        this.averageTemperature = averageTemperature;
        this.averagePrecipitation = averagePrecipitation;
    }

    public int getDateToday() {
        return dateToday;
    }

    public void setDateToday(int dateToday) {
        this.dateToday = dateToday;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String sity) {
        this.city = sity;
    }

    public int getTodayDay() {
        return todayDay;
    }

    public void setTodayDay(int todayDay) {
        this.todayDay = todayDay;
    }

    public int getTodayNow() {
        return todayNow;
    }

    public void setTodayNow(int todayNow) {
        this.todayNow = todayNow;
    }

    public int getTodayNight() {
        return todayNight;
    }

    public void setTodayNight(int todayNight) {
        this.todayNight = todayNight;
    }

    public int getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(int averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public int getAveragePrecipitation() {
        return averagePrecipitation;
    }

    public void setAveragePrecipitation(int averagePrecipitation) {
        this.averagePrecipitation = averagePrecipitation;
    }
}
