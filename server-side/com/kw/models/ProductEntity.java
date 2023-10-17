package com.kw.models;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ProductEntity {

	private String id;
	private String productThumb;
	private String gsutil_uri;
	private Double marked_price;
	private Double selling_price;
	private Integer stock;
	private Integer sold;
	private String productDesc;
	private String productName;
	private String category_id;
	private Boolean is_upload2_cloud_storage;
	private Boolean is_created_product;
	private Boolean is_added_2_productset;
	private Boolean is_created_refer_img;
	private String ref_image_id;
	private List<String> detectedObject;
	private Date createdDt;
	private Date updatedDt;
	
	public String getProductThumb() {
		return productThumb;
	}
	public void setProductThumb(String productThumb) {
		this.productThumb = productThumb;
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getGsutil_uri() {
		return gsutil_uri;
	}
	public void setGsutil_uri(String gsutil_uri) {
		this.gsutil_uri = gsutil_uri;
	}
	public Boolean getIs_upload2_cloud_storage() {
		return is_upload2_cloud_storage;
	}
	public void setIs_upload2_cloud_storage(Boolean is_upload2_cloud_storage) {
		this.is_upload2_cloud_storage = is_upload2_cloud_storage;
	}
	public Boolean getIs_created_product() {
		return is_created_product;
	}
	public void setIs_created_product(Boolean is_created_product) {
		this.is_created_product = is_created_product;
	}
	public Boolean getIs_added_2_productset() {
		return is_added_2_productset;
	}
	public void setIs_added_2_productset(Boolean is_added_2_productset) {
		this.is_added_2_productset = is_added_2_productset;
	}
	public Boolean getIs_created_refer_img() {
		return is_created_refer_img;
	}
	public void setIs_created_refer_img(Boolean is_created_refer_img) {
		this.is_created_refer_img = is_created_refer_img;
	}
	public String getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}
	public List<String> getDetectedObject() {
		return detectedObject;
	}
	public void setDetectedObject(List<String> detectedObject) {
		this.detectedObject = detectedObject;
	}
	public String getRef_image_id() {
		return ref_image_id;
	}
	public void setRef_image_id(String ref_image_id) {
		this.ref_image_id = ref_image_id;
	}
	
	
}

