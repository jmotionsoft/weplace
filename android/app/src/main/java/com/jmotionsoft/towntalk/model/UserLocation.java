package com.jmotionsoft.towntalk.model;

/**
 * Created by dooseon on 2016. 11. 20..
 */

public class UserLocation {
    private Integer location_no;
    private String location_name;
    private Double latitude;
    private Double longitude;
    private String address;
    private String default_yn;

    public String getDefault_yn() {
        return default_yn;
    }

    public void setDefault_yn(String default_yn) {
        this.default_yn = default_yn;
    }

    public Integer getLocation_no() {
        return location_no;
    }

    public void setLocation_no(Integer location_no) {
        this.location_no = location_no;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
