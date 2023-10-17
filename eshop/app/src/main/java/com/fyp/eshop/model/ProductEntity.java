package com.fyp.eshop.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ProductEntity {

    private String id;
    private String docId;
    private String category_id;
    private String category_desc;
    private String ProductThumb;
    private String gsutil_uri;
    private int marked_price;
    private int selling_price;
    private int view;
    private Integer stock;
    private Integer sold;
    private String productDesc;
    private String productName;
    private Boolean is_upload2_cloud_storage;
    private Boolean is_created_product;
    private Boolean is_added_2_productset;
    private Boolean is_created_refer_img;
    private List<String> detectedObject;
    private Date createdDt;
    private Date updatedDt;

}
