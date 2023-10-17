package com.fyp.eshop.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;


import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class OrderEntity implements Serializable {

    public OrderEntity() {
    }

    private String userId;
    private String order_no;
    @ServerTimestamp
    private Timestamp orderDt;
    private String cardNum;
    private String cardType;
    private List<OrderProductEntity> productList;
    private int order_total_price;
    private int origin_price;
    private String status;
    private Discount discount;

}
