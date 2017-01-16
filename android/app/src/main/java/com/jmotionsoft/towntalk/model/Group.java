package com.jmotionsoft.towntalk.model;

/**
 * Created by sin31 on 2016-06-27.
 */
public class Group {
    private Integer group_no;
    private String name;
    private Integer order_no;
    private String state;

    public Integer getGroup_no() {
        return group_no;
    }

    public void setGroup_no(Integer group_no) {
        this.group_no = group_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder_no() {
        return order_no;
    }

    public void setOrder_no(Integer order_no) {
        this.order_no = order_no;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
