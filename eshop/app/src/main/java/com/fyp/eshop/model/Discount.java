package com.fyp.eshop.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Discount implements Serializable {
    private String docId;
    private boolean actv_ind;
    private String promo_code;
    private int discount_percentage;
}
