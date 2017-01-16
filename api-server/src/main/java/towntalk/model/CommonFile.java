package towntalk.model;

/**
 * Created by sin31 on 2016-10-04.
 */
public class CommonFile {
    public static final String SAVE_TYPE_S3 = "S3";
    public static final String SAVE_TYPE_LOCAL = "LOCAL";

    private Integer file_no;
    private String file_name;
    private String file_type;
    private Long file_length;
    private String save_type;
    private String save_path;
    private Integer user_no;
    private String reg_date;

    public Integer getFile_no() {
        return file_no;
    }

    public void setFile_no(Integer file_no) {
        this.file_no = file_no;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public Long getFile_length() {
        return file_length;
    }

    public void setFile_length(Long file_length) {
        this.file_length = file_length;
    }

    public String getSave_path() {
        return save_path;
    }

    public void setSave_path(String save_path) {
        this.save_path = save_path;
    }

    public String getSave_type() {
        return save_type;
    }

    public void setSave_type(String save_type) {
        this.save_type = save_type;
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
}
