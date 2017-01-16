package towntalk.model;

/**
 * Created by dooseon on 2016. 6. 6..
 */
public class Board {
    private Integer board_no;
    private Integer group_no;
    private String name;
    private String type;
    private String image_yn;
    private String comment_yn;
    private String reply_yn;
    private Integer order_no;
    private String state;
    private String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getBoard_no() {
        return board_no;
    }

    public void setBoard_no(Integer board_no) {
        this.board_no = board_no;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage_yn() {
        return image_yn;
    }

    public void setImage_yn(String image_yn) {
        this.image_yn = image_yn;
    }

    public String getComment_yn() {
        return comment_yn;
    }

    public void setComment_yn(String comment_yn) {
        this.comment_yn = comment_yn;
    }

    public String getReply_yn() {
        return reply_yn;
    }

    public void setReply_yn(String reply_yn) {
        this.reply_yn = reply_yn;
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
