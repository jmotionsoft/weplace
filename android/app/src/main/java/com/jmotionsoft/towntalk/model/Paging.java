package com.jmotionsoft.towntalk.model;

/**
 * Created by sin31 on 2016-06-27.
 */
public class Paging {
    private int page = 0;
    private int count = 20;
    private String sort;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
