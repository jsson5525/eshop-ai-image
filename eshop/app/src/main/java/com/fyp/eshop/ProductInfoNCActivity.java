package com.fyp.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.fyp.eshop.databinding.ActivityProductInfoNcBinding;
import com.fyp.eshop.model.ProductCart;
import com.fyp.eshop.model.ProductEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductInfoNCActivity extends AppCompatActivity {

    private ActivityProductInfoNcBinding binding;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebase = FirebaseFirestore.getInstance();
    ImageView img_thumb,img_arrow;
    TextView tv_name,tv_market_price,tv_selling_price,tv_cat,tv_desc,tv_total_price,tv_sold,tv_view_no;
    String productId,docId;
    int unit_price;
    int qty;
    //    double unit_price;
    ElegantNumberButton btn_eleg;
    Button btn_addToCart;
    ProductEntity entity;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProductInfoNcBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        //Init
        img_thumb = binding.imgProductThumb;
        img_arrow = binding.imgArrow;
        tv_name = binding.tvProductName;
        tv_sold = binding.tvSold;
        tv_market_price = binding.tvMarketPrice;
        tv_selling_price = binding.tvSellingPrice;
        tv_cat = binding.tvProductCat;
        tv_desc = binding.tvProductDesc;
        tv_view_no = binding.tvViewNo;

        productId = getIntent().getStringExtra("productId");

//        Log.i("Product ID",productId);


//        List<Task> taskList = getProductByDocId(productId);
        Task q3 = getProductByDocId(productId);

        Tasks.whenAllComplete(q3).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
//                setProductInfo();
            }
        });

//        firebase.collection("products").document(productId).get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if(documentSnapshot.exists()) {
//                            Log.i("thumb",documentSnapshot.getString("productThumb"));
//                            Glide.with(ProductInfoActivity.this).load(documentSnapshot.getString("productThumb")).placeholder(R.drawable.loading_shape)
//                                    .dontAnimate().into(img_thumb);
//                            docId = documentSnapshot.getId();
//                            tv_name.setText(documentSnapshot.getString("productName"));
//                            unit_price = documentSnapshot.getLong("selling_price").intValue();
//                            tv_selling_price.setText("$" + String.valueOf(unit_price));
//                            tv_cat.setText("Category : " + documentSnapshot.getString("productCategory"));
//                            tv_desc.setText(documentSnapshot.getString("productDesc"));
//                        }
//                    }
//                });




    }

    private void setProductInfo() {
        Glide.with(ProductInfoNCActivity.this).load(entity.getProductThumb()).placeholder(R.drawable.loading_shape)
                .dontAnimate().into(img_thumb);
        tv_name.setText(entity.getProductName());
        unit_price = entity.getSelling_price();
        tv_sold.setText(new StringBuffer().append(entity.getSold()).append("+"));
        tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tv_market_price.setText(new StringBuffer().append("$ ").append(entity.getMarked_price()));
        tv_selling_price.setText(new StringBuffer().append("$ ").append(unit_price));
        tv_cat.setText(entity.getCategory_desc());
        tv_desc.setText(entity.getProductDesc());
        tv_view_no.setText(new StringBuffer().append(entity.getView()));

        if(entity.getSelling_price() < entity.getMarked_price()) {
            img_arrow.setImageResource(R.drawable.ic_arrow_downward_24);
        } else if (entity.getSelling_price() > entity.getMarked_price()) {
            img_arrow.setImageResource(R.drawable.ic_arrow_upward_24);
        } else {
            tv_market_price.setVisibility(View.GONE);
            img_arrow.setVisibility(View.GONE);
        }
    }

    private Task getProductByDocId(String productId) {
        List<Task> taskList = new ArrayList<>();

        Task q1 = firebase.collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        docId = documentSnapshot.getId();
                        entity = documentSnapshot.toObject(ProductEntity.class);
                    }
                });

        Task q2 = Tasks.whenAllSuccess(q1)
                .addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Object>> task) {
                        if (task.isSuccessful()) {
                            firebase.collection("category")
                                    .document(entity.getCategory_id())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            entity.setCategory_desc(documentSnapshot.getString("category_name"));
                                            setProductInfo();
                                            Log.i("Test",entity.getCategory_desc());
                                        }
                                    });
                        }
                    }
                });


        return q2;
    }


}