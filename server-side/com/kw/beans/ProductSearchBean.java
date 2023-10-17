package com.kw.beans;

import java.util.Date;
import java.util.List;

public class ProductSearchBean {

	private String id;
	private Float score;

	private String ProductThumb;
	private String gsutil_uri;
	private Double marked_price;
	private Double selling_price;
	private Integer stock;
	private Integer sold;
	private String productDesc;
	private String productName;
	
	private String category_id;
	private String category_name;
	//private String refImgName;
	private String gcp_productname;
	private String object_name;
	private List<String> objLst;
	private Date createdDt;
	private Date updatedDt;
	private String ref_image_id;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public Float getScore() {
		return score;
	}
	public void setScore(Float score) {
		this.score = score;
	}
	public String getProductThumb() {
		return ProductThumb;
	}
	public void setProductThumb(String productThumb) {
		ProductThumb = productThumb;
	}
	public String getGsutil_uri() {
		return gsutil_uri;
	}
	public void setGsutil_uri(String gsutil_uri) {
		this.gsutil_uri = gsutil_uri;
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
	public Integer getStock() {
		return stock;
	}
	public void setStock(Integer stock) {
		this.stock = stock;
	}
	public Integer getSold() {
		return sold;
	}
	public void setSold(Integer sold) {
		this.sold = sold;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Date getCreatedDt() {
		return createdDt;
	}
	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}
	public Date getUpdatedDt() {
		return updatedDt;
	}
	public void setUpdatedDt(Date updatedDt) {
		this.updatedDt = updatedDt;
	}
//	public String getRefImgName() {
//		return refImgName;
//	}
//	public void setRefImgName(String refImgName) {
//		this.refImgName = refImgName;
//	}
	public String getGcp_productname() {
		return gcp_productname;
	}
	public void setGcp_productname(String gcp_productname) {
		this.gcp_productname = gcp_productname;
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
	public List<String> getObjLst() {
		return objLst;
	}
	public void setObjLst(List<String> objLst) {
		this.objLst = objLst;
	}
	public String getRef_image_id() {
		return ref_image_id;
	}
	public void setRef_image_id(String ref_image_id) {
		this.ref_image_id = ref_image_id;
	}
	
	
	
}
