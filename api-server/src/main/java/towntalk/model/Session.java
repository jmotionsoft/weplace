package towntalk.model;

/**
 * Created by dooseon on 2016. 8. 7..
 */
public class Session {
    private Integer session_no;
    private Integer user_no;
    private String session_key;
    private String limit_date;
    private String reg_date;

    public Integer getSession_no() {
        return session_no;
    }

    public void setSession_no(Integer session_no) {
        this.session_no = session_no;
    }

    public Integer getUser_no() {
        return user_no;
    }

    public void setUser_no(Integer user_no) {
        this.user_no = user_no;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getLimit_date() {
        return limit_date;
    }

    public void setLimit_date(String limit_date) {
        this.limit_date = limit_date;
    }

    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }
}
