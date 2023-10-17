package com.fyp.eshop.model;

import java.util.List;

import lombok.Data;

@Data
public class ProductSearchResultBean {

    private List<ProductSearchBean> search_results;
    private List<ProductSearchBean> suggested_results;

}
