package com.fyp.eshop.model;

import lombok.Data;

@Data
public class CreditCard {

    private String cardType;
    private String cardHolderName;
    private String cardNum;
    private int expiryMon;
    private int expiryYear;
    private String cvc;

}
