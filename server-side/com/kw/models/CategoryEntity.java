package com.kw.models;

import java.util.Date;

public class CategoryEntity {
	private String id;
	
	private Boolean actv_ind;
	
	private String category_name;
	
	private Date createdDt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getActv_ind() {
		return actv_ind;
	}

	public void setActv_ind(Boolean actv_ind) {
		this.actv_ind = actv_ind;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public Date getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}
	
	
}
