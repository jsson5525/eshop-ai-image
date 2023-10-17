package com.fyp.eshop.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class ProductSearchBean {

    private String id;
    private Float score;

    private String productThumb;
    private String gsutil_uri;
    private Double marked_price;
    private Double selling_price;
    private Integer stock;
    private Integer sold;
    private String productDesc;
    private String productName;

    private String category_id;
    private String category_name;
    private String refImgName;
    private String gcp_productname;
    private String object_name;
    private List<String> objLst;
    private Date createdDt;
    private Date updatedDt;

}