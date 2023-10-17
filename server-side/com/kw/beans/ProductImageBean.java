package com.kw.beans;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class ProductImageBean {
	private String id;
	private Integer status;
	private String file_id;
	private String file_name;
	private String product_name;
	private String product_desc;
	private Double marked_price;
	private Double selling_price;
	private Integer stocks;
	private String category_id;
	private String category_name;
	private InputStream fis;
	private MultipartFile file;
	private String object_name;
	private String filePathStr;
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getFile_id() {
		return file_id;
	}

	public void setFile_id(String file_id) {
		this.file_id = file_id;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public InputStream getFis() {
		return fis;
	}

	public void setFis(InputStream fis) {
		this.fis = fis;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getProduct_desc() {
		return product_desc;
	}

	public void setProduct_desc(String product_desc) {
		this.product_desc = product_desc;
	}

	public Double getMarked_price() {
		return marked_price;
	}

	public void setMarked_price(Double marked_price) {
		this.marked_price = marked_price;
	}

	public Double getSelling_price() {
		return selling_price;
	}

	public void setSelling_price(Double selling_price) {
		this.selling_price = selling_price;
	}

	public Integer getStocks() {
		return stocks;
	}

	public void setStocks(Integer stocks) {
		this.stocks = stocks;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getObject_name() {
		return object_name;
	}

	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}

	public String getFilePathStr() {
		return filePathStr;
	}

	public void setFilePathStr(String filePathStr) {
		this.filePathStr = filePathStr;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	
	
	
}
