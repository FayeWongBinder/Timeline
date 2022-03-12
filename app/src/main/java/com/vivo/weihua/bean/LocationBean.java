package com.vivo.weihua.bean;

public class LocationBean {
    private String day;
    private int time;
    private String address;
    private Double latitude;
    private Double longitude;

    public LocationBean(String day, int time, String address, Double latitude, Double longitude) {
        this.day = day;
        this.time = time;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "LocationBean{" +
                "day='" + day + '\'' +
                ", time='" + time + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
