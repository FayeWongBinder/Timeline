package com.vivo.weihua.bean;

import java.util.List;

public class CountBean {
    private String day;
    private List<LocationBean> locationList;

    public CountBean() {

    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<LocationBean> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<LocationBean> locationList) {
        this.locationList = locationList;
    }

    @Override
    public String toString() {
        return "CountBean{" +
                "day='" + day + '\'' +
                ", locationList=" + locationList +
                '}';
    }
}
