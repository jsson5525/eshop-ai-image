package com.fyp.eshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.fyp.eshop.databinding.ActivityPaymentMethodBinding;
import com.fyp.eshop.model.CreditCard;
import com.fyp.eshop.model.Discount;
import com.fyp.eshop.model.OrderEntity;
import com.fyp.eshop.model.OrderProductEntity;
import com.fyp.eshop.model.Product;
import com.fyp.eshop.model.ProductCart;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Empty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PaymentMethodPayActivity extends AppCompatActivity {
    private ActivityPaymentMethodBinding binding;
    private final FirebaseFirestore firebase = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CreditCard card;
    CardForm cardForm;
    TextView tv_payment_amount_holder,tv_payment_amount
            ,tv_card_preview_name,tv_card_preview_number,tv_card_preview_cvc,tv_card_preview_expiry,tv_card_preview_type;
    EditText et_card_name,et_card_number,et_cvc,et_expiry_date;
    Button btn_pay;
    int orderTotalPrice,origin_price;
    String userId,uuid;
    List<ProductCart> productCartList = new ArrayList<>();
    Discount discount;
    boolean discounted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentMethodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Declare
        cardForm = (CardForm) findViewById(R.id.card_form);
        btn_pay = (Button) findViewById(R.id.btn_pay);
        tv_payment_amount_holder = (TextView) findViewById(R.id.payment_amount_holder);
        tv_payment_amount = (TextView) findViewById(R.id.payment_amount);

        tv_card_preview_name = (TextView) findViewById(R.id.card_preview_name);
        tv_card_preview_number = (TextView) findViewById(R.id.card_preview_number);
        tv_card_preview_cvc = (TextView) findViewById(R.id.card_preview_cvc);
        tv_card_preview_expiry = (TextView) findViewById(R.id.card_preview_expiry);
        tv_card_preview_type = (TextView) findViewById(R.id.card_preview_type);

        et_card_name = (EditText) findViewById(R.id.card_name);
        et_card_number = (EditText) findViewById(R.id.card_number);
        et_cvc = (EditText) findViewById(R.id.cvc);
        et_expiry_date = (EditText) findViewById(R.id.expiry_date);

        //init
//        orderTotalPrice = getIntent().getIntExtra("totalPrice",0);
//        productCartList = (List<ProductCart>) getIntent().getSerializableExtra("productList");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            orderTotalPrice = extras.getInt("totalPrice");
            origin_price = extras.getInt("totalPrice");;
            productCartList = (List<ProductCart>) extras.getSerializable("productList");
            discount = (Discount) extras.getSerializable("discount");

        }

        if(discount != null) {
            discounted = true;
            setToast("You get "+discount.getDiscount_percentage() +"% off !");
            Log.i("cal",String.valueOf(orderTotalPrice));
            Log.i("cal",String.valueOf(10/100));
            orderTotalPrice = (int) ((int) orderTotalPrice * ((1-(discount.getDiscount_percentage())/100.0)));
        }

//        tv_payment_amount_holder.setText("");
        tv_payment_amount.setText(new StringBuffer().append("$ ").append(orderTotalPrice).toString());
        btn_pay.setText(new StringBuffer().append("PAY $").append(orderTotalPrice).toString());
        et_cvc.setHint("CVC/CCV");

        Log.i("Test",String.valueOf(productCartList.size()));

        cardForm.setAmount(String.valueOf(orderTotalPrice));

        userId = firebaseAuth.getUid();

        firebase.collection("payments").document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    card = documentSnapshot.toObject(CreditCard.class);
                    String cardNum = getCardNum(card.getCardNum());
                    String expiryDate = getExipryDate(card.getExpiryMon(),card.getExpiryYear());

                    tv_card_preview_name.setText(card.getCardHolderName());
                    tv_card_preview_number.setText(cardNum);
                    tv_card_preview_cvc.setText(card.getCvc());
                    tv_card_preview_expiry.setText(expiryDate);
                    tv_card_preview_type.setText(card.getCardType());

                    et_card_name.setText(card.getCardHolderName());
                    et_card_number.setText(cardNum);
                    et_expiry_date.setText(expiryDate);
                    et_cvc.setText(card.getCvc());

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Error:", e.getLocalizedMessage());
            }
        });


        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {

                if(Parameter.CARD_TYPE_UNKNOWN.equals(card.getBrand())) {
                    setToast("Invalid Card " + card.getBrand());
                    return;
                }

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PaymentMethodPayActivity.this);
                alertDialog.setTitle("Confirm Payment");
                alertDialog.setMessage(getAlertMsg(card.getBrand(),card.getNumber(), orderTotalPrice,origin_price));

                alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Task q1 = cleanCart();
                        Task q2 = orderProducts();
                        List<Task> taskList = updateStock();
                        taskList.add(q1);
                        taskList.add(q2);


                        Tasks.whenAllSuccess(taskList).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<Object>> task) {
                                if(task.isSuccessful()) {
                                    setToast("Order Successful ! " + uuid);
                                    finish();
                                }
                            }
                        });

                    }
                });

                alertDialog.setNeutralButton("Cancel",(dialog, which) -> {
                    setToast("Cancel");
                });

                alertDialog.setCancelable(false);
                alertDialog.show();

            }
        });


    }

//    @Override
//    public void onBackPressed() {
//        FragmentManager fm = getSupportFragmentManager();
//        if(fm.getBackStackEntryCount() > 0) {
//            fm.popBackStack();
//        } else {
//            super.onBackPressed();
//        }
//    }

    private List<Task> updateStock() {

        List<Task> taskList = new ArrayList<>();

        for (ProductCart cart : productCartList) {
            int negativeVal = -cart.getQty();
           Task task = firebase.collection("products")
                    .document(cart.getProductDocId())
                    .update("stock",FieldValue.increment(negativeVal)
                    ,"sold",FieldValue.increment(cart.getQty()));
             taskList.add(task);

        }
        return  taskList;
    }

    private Task cleanCart() {
       Task q1 = firebase.collection("carts").whereEqualTo("userId",userId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot documentSnapshot : documentSnapshots) {
                    firebase.collection("carts").document(documentSnapshot.getId())
                            .delete();
                }
            }
        });
    return q1;
    }

    private Task orderProducts() {

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(userId);
        uuid = String.valueOf(System.currentTimeMillis());
        orderEntity.setOrder_no(uuid);
        orderEntity.setOrigin_price(origin_price);
        orderEntity.setOrder_total_price(orderTotalPrice);
        orderEntity.setOrderDt(new Timestamp(new Date()));
        orderEntity.setCardNum(card.getCardNum());
        orderEntity.setCardType(card.getCardType());
        orderEntity.setStatus("Completed");
        orderEntity.setDiscount(discount);
        List<OrderProductEntity> orderProductEntityList = new ArrayList<>();
        for (ProductCart cart : productCartList) {
            OrderProductEntity orderProductEntity = new OrderProductEntity();
            orderProductEntity.setProductDocId(cart.getProductDocId());
            orderProductEntity.setProductId(cart.getProductId());
            orderProductEntity.setProductName(cart.getProduct().getProductName());
            orderProductEntity.setProductThumb(cart.getProduct().getProductThumb());
            orderProductEntity.setSelling_price(cart.getProduct().getSelling_price());
            orderProductEntity.setQty(cart.getQty());
            orderProductEntity.setTotal_price(cart.getTotalPrice());
            orderProductEntityList.add(orderProductEntity);
        }
        orderEntity.setProductList(orderProductEntityList);

        Task q1 = firebase.collection("orders")
                .add(orderEntity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("WriteDoc","DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("WriteDoc","Error adding document", e);
                    }
                });

        return q1;
    }


    private void orderProductsOld() {
        OrderProductEntity orderProductEntity = new OrderProductEntity();

        firebase.collection("carts").
                whereEqualTo("userId",userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                ProductCart cart = new ProductCart();
                                String docId = (String) document.get("productDocId");
                                cart.setDocId(document.getId());
                                cart.setProductDocId(docId);
                                cart.setProductId((String) document.get("productId"));
                                cart.setQty(Math.toIntExact((Long) document.get("qty")));
                                cart.setCreatedDt((Timestamp) document.get("createdDt"));

                                Task q2 = firebase.collection("products").document(docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot snapshot) {

                                        Product product = new Product();
                                        product.setDocId(snapshot.getId());
                                        product.setProductId(snapshot.getString("id"));
                                        product.setProductName(snapshot.getString("productName"));
                                        product.setProductThumb(snapshot.getString("productThumb"));
                                        product.setSelling_price(snapshot.getLong("selling_price").intValue());
                                        product.setProductCategory(snapshot.getString("productCategory"));
                                        cart.setProduct(product);
                                        cart.setTotalPrice(cart.getQty() * cart.getProduct().getSelling_price());
                                        productCartList.add(cart);



                                    }
                                });

                            }
                        } else {

                        }

                    }

                });

    }

    private String getAlertMsg(String cardType,String cardNum, int totalPrice, int origin_price) {
        String disMsg = "";
        if (discounted) {
            disMsg = new StringBuffer().append(discount.getDiscount_percentage())
                    .append("% off : $ ")
                    .append((origin_price-totalPrice))
                    .toString();
        }

        return  new StringBuffer()
                .append(cardType + " Card \n ")
                .append(getCardNum(cardNum) + "\n")
                .append("--------------------------------------------------------------- \n")
                .append("Price :     $ ").append(origin_price).append("\n")
                .append(disMsg).append("\n")
                .append("--------------------------------------------------------------- \n")
                .append("Total :     $ ")
                .append(totalPrice)
                .toString();
    }

    private  String getCardNum(String cardNum) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cardNum.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                result.append(" ");
            }

            result.append(cardNum.charAt(i));
        }
        return  result.toString();
    }

    private String getExipryDate(int expMon, int expYear) {
        return new StringBuffer()
                .append(expMon)
                .append("/")
                .append(Integer.parseInt(String.valueOf(expYear).substring(2)))
                .toString();
    }
    private void setToast(String input){
        Toast.makeText(getApplicationContext(),input,Toast.LENGTH_SHORT).show();
    }
}