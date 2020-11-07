package com.web.mundo.sir.po;

import java.util.Date;

public class SirTs {
	private long id;
	private String v_id;
	private String oss;
	private String count;
	private Date create_time;
	private Date update_time;
	private String url;
	private String is_down;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getV_id() {
		return v_id;
	}
	public void setV_id(String v_id) {
		this.v_id = v_id;
	}
	public String getOss() {
		return oss;
	}
	public void setOss(String oss) {
		this.oss = oss;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIs_down() {
		return is_down;
	}
	public void setIs_down(String is_down) {
		this.is_down = is_down;
	}

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
}
