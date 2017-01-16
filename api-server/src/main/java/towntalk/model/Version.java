package towntalk.model;

public class Version {
	private int version_no;
	private int version_code;
	private String required_yn;
	private String update_note;
	private String reg_date;

	public int getVersion_no() {
		return version_no;
	}
	public void setVersion_no(int version_no) {
		this.version_no = version_no;
	}
	public int getVersion_code() {
		return version_code;
	}
	public void setVersion_code(int version_code) {
		this.version_code = version_code;
	}
	public String getRequired_yn() {
		return required_yn;
	}
	public void setRequired_yn(String required_yn) {
		this.required_yn = required_yn;
	}
	public String getUpdate_note() {
		return update_note;
	}
	public void setUpdate_note(String update_note) {
		this.update_note = update_note;
	}
	public String getReg_date() {
		return reg_date;
	}
	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}	
}
