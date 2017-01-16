package towntalk.model;

/**
 * Created by dooseon on 2016. 6. 6..
 */
public class Contents extends Paging{
    public final static String STATE_ACTIVE = "ACTIVE";
    public final static String STATE_INACTIVE = "INACTIVE";

    private Integer contents_no;
    private Integer board_no;
    private String title;
    private String body;
    private String images_no;
    private Integer read_count;
    private Double latitude;
    private Double longitude;
    private String notice_yn;
    private String state;
    private Integer user_no;
    private String reg_date;

    private String crypt_key;
    private Double latitude_range;
    private Double longitude_range;
    private String nickname;
    private Integer comment_count;

    public String getImages_no() {
        return images_no;
    }

    public void setImages_no(String images_no) {
        this.images_no = images_no;
    }

    public Integer getContents_no() {
        return contents_no;
    }

    public void setContents_no(Integer contents_no) {
        this.contents_no = contents_no;
    }

    public Integer getBoard_no() {
        return board_no;
    }

    public void setBoard_no(Integer board_no) {
        this.board_no = board_no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getRead_count() {
        return read_count;
    }

    public void setRead_count(Integer read_count) {
        this.read_count = read_count;
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

    public String getNotice_yn() {
        return notice_yn;
    }

    public void setNotice_yn(String notice_yn) {
        this.notice_yn = notice_yn;
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

    public String getCrypt_key() {
        return crypt_key;
    }

    public void setCrypt_key(String crypt_key) {
        this.crypt_key = crypt_key;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getComment_count() {
        return comment_count;
    }

    public void setComment_count(Integer comment_count) {
        this.comment_count = comment_count;
    }
}
