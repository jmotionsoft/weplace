package com.jmotionsoft.towntalk.model;

/**
 * Created by sin31 on 2016-06-27.
 */
public class Comment extends Paging{
    private Integer comment_no;
    private Integer contents_no;
    private Integer p_comment_no;
    private String comment;
    private String state;
    private Integer user_no;
    private String reg_date;

    private String nickname;

    public Integer getComment_no() {
        return comment_no;
    }

    public void setComment_no(Integer comment_no) {
        this.comment_no = comment_no;
    }

    public Integer getContents_no() {
        return contents_no;
    }

    public void setContents_no(Integer contents_no) {
        this.contents_no = contents_no;
    }

    public Integer getP_comment_no() {
        return p_comment_no;
    }

    public void setP_comment_no(Integer p_comment_no) {
        this.p_comment_no = p_comment_no;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getUser_no() {
        return user_no;
    }

    public void setUser_no(Integer user_no) {
        this.user_no = user_no;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
