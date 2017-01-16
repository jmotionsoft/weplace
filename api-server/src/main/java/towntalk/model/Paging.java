package towntalk.model;

public class Paging {
	private Integer last_index_no;
	private Integer total_count = 20;
	private String sort;

	public Integer getLast_index_no() {
		return last_index_no;
	}

	public void setLast_index_no(Integer last_index_no) {
		this.last_index_no = last_index_no;
	}

	public Integer getTotal_count() {
		return total_count;
	}

	public void setTotal_count(Integer total_count) {
		this.total_count = total_count;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
}
