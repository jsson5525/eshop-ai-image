package com.fyp.eshop.ui.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class CartViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<String> mtotalPrice;

    public CartViewModel() {
        mtotalPrice = new MutableLiveData<>();
    }

    public LiveData<String> getTotalPrice() {
        return mtotalPrice;
    }

    public void setTotalPrice(int total) {
        mtotalPrice.setValue(String.valueOf(total));
    }

    public int getPriceValue()
    {
        return Integer.parseInt(mtotalPrice.getValue());
    }

}