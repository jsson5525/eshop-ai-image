package com.fyp.eshop.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.fyp.eshop.Parameter;
import com.fyp.eshop.model.Discount;
import com.fyp.eshop.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public  class FirebaseManager {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseManager instance;

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public static FirebaseFirestore getDb() {
        return db;
    }

    public List<Product> getProductList() {
        List<Product> products = new ArrayList<>();

        db.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if( task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult() ) {

                        Product product = new Product();
                        product.setProductId(document.getString("id"));
                        product.setProductName(document.getString("productName"));
                        product.setProductThumb(document.getString("productThumb"));
                        product.setSelling_price(document.getLong("selling_price").intValue());
                        products.add(product);
                        }
                    Log.i("Data", products.toString());

                    } else {
                    Log.i("Data", "Failed");
                }
            }
        });


        return  products;
    }

    public void clickView(String docId) {
        db.collection(Parameter.PRODUCTS_COLLECTION)
                .document(docId)
                .update("view", FieldValue.increment(1));
    }

    public List<Discount> checkDiscount(String code) {
        List<Discount> discountList = new ArrayList<>();
        db.collection(Parameter.DISCOUNT_COLLECTION)
                .whereEqualTo(Parameter.PROMO_CODE_FIELD,code)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                Discount discount = new Discount();
                for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                    discount = documentSnapshot.toObject(Discount.class);
                    discount.setDocId(documentSnapshot.getId());
                }
                discountList.add(discount);
            }

        });
        return  discountList;
    }

}
