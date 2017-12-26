package com.netcracker.ssau.restplacesearcher.model;

public class WeatherData {

    private int dateToday;
    private String weekDay;
    private String city;


    private int todayNow;
    private int todayDay;
    private int todayNight;

    private int averageTemperatureDay;
    private int averageTemperatureNight;
    private int averagePrecipitation;

    public WeatherData(int dateToday, String weekDay, String city, int todayNow, int todayDay, int todayNight, int averageTemperatureDay, int averageTemperatureNight, int averagePrecipitation) {
        this.dateToday = dateToday;
        this.weekDay = weekDay;
        this.city = city;
        this.todayNow = todayNow;
        this.todayDay = todayDay;
        this.todayNight = todayNight;
        this.averageTemperatureDay = averageTemperatureDay;
        this.averageTemperatureNight = averageTemperatureNight;
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

    public void setCity(String city) {
        this.city = city;
    }

    public int getTodayNow() {
        return todayNow;
    }

    public void setTodayNow(int todayNow) {
        this.todayNow = todayNow;
    }

    public int getTodayDay() {
        return todayDay;
    }

    public void setTodayDay(int todayDay) {
        this.todayDay = todayDay;
    }

    public int getTodayNight() {
        return todayNight;
    }

    public void setTodayNight(int todayNight) {
        this.todayNight = todayNight;
    }

    public int getAverageTemperatureDay() {
        return averageTemperatureDay;
    }

    public void setAverageTemperatureDay(int averageTemperatureDay) {
        this.averageTemperatureDay = averageTemperatureDay;
    }

    public int getAverageTemperatureNight() {
        return averageTemperatureNight;
    }

    public void setAverageTemperatureNight(int averageTemperatureNight) {
        this.averageTemperatureNight = averageTemperatureNight;
    }

    public int getAveragePrecipitation() {
        return averagePrecipitation;
    }

    public void setAveragePrecipitation(int averagePrecipitation) {
        this.averagePrecipitation = averagePrecipitation;
    }
}