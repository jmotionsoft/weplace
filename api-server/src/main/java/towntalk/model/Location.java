package towntalk.model;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by dooseon on 2016. 10. 30..
 */
public class Location {
    private Integer location_no;
    private Integer user_no;
    @NotEmpty
    private String location_name;
    @NotEmpty
    private Double latitude;
    @NotEmpty
    private Double longitude;
    private String address;
    private Double latitude_range;
    private Double longitude_range;
    private String default_yn;
    private String reg_date;
    private String edt_date;

    private String crypt_key;

    public String getEdt_date() {
        return edt_date;
    }

    public void setEdt_date(String edt_date) {
        this.edt_date = edt_date;
    }

    public Double getLatitude_range() {
        return latitude_range;
    }

    public void setLatitude_range(Double latitude_range) {
        this.latitude_range = latitude_range;
    }

    public Double getLongitude_range() {
        return longitude_range;
    }

    public void setLongitude_range(Double longitude_range) {
        this.longitude_range = longitude_range;
    }

    public String getCrypt_key() {
        return crypt_key;
    }

    public void setCrypt_key(String crypt_key) {
        this.crypt_key = crypt_key;
    }

    public String getDefault_yn() {
        return default_yn;
    }

    public void setDefault_yn(String default_yn) {
        this.default_yn = default_yn;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getLocation_no() {
        return location_no;
    }

    public void setLocation_no(Integer location_no) {
        this.location_no = location_no;
    }

    public Integer getUser_no() {
        return user_no;
    }

    public void setUser_no(Integer user_no) {
        this.user_no = user_no;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }
}
