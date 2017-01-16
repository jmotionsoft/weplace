package towntalk.model;

/**
 * Created by sin31 on 2016-06-14.
 */
public class Setting {
    private int user_no;
    private String alarm_comment_yn;
    private String alarm_message_yn;
    private String alarm_mention_yn;

    public int getUser_no() {
        return user_no;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
    }

    public String getAlarm_comment_yn() {
        return alarm_comment_yn;
    }

    public void setAlarm_comment_yn(String alarm_comment_yn) {
        this.alarm_comment_yn = alarm_comment_yn;
    }

    public String getAlarm_message_yn() {
        return alarm_message_yn;
    }

    public void setAlarm_message_yn(String alarm_message_yn) {
        this.alarm_message_yn = alarm_message_yn;
    }

    public String getAlarm_mention_yn() {
        return alarm_mention_yn;
    }

    public void setAlarm_mention_yn(String alarm_mention_yn) {
        this.alarm_mention_yn = alarm_mention_yn;
    }
}
