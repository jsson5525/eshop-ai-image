package com.fyp.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.fyp.eshop.databinding.ActivityPaymentMethodBinding;
import com.fyp.eshop.model.CreditCard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Map;

public class PaymentMethodActivity extends AppCompatActivity {
    private ActivityPaymentMethodBinding binding;
    private final FirebaseFirestore firebase = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CreditCard card;
    CardForm cardForm;
    TextView tv_payment_amount_holder,tv_payment_amount
            ,tv_card_preview_name,tv_card_preview_number,tv_card_preview_cvc,tv_card_preview_expiry,tv_card_preview_type;
    EditText et_card_name,et_card_number,et_cvc,et_expiry_date;
    Button btn_pay;
    Boolean cardIsExisted = false;
    String userId;

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
        tv_payment_amount_holder.setText("");
        tv_payment_amount.setText("");
        btn_pay.setText("");
        et_cvc.setHint("CVC/CCV");




         userId = firebaseAuth.getUid();

        Task q1 = firebase.collection("payments").document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                             cardIsExisted = true;
                             card = documentSnapshot.toObject(CreditCard.class);
                             String cardNum = getCardNum(card.getCardNum());
                             String expiryDate = getExipryDate(card.getExpiryMon(),card.getExpiryYear());

                             tv_card_preview_name.setText(card.getCardHolderName());
                             tv_card_preview_number.setText(cardNum);
                             tv_card_preview_cvc.setText("***");
                             tv_card_preview_expiry.setText(expiryDate);
                             tv_card_preview_type.setText(card.getCardType());

                             et_card_name.setText(card.getCardHolderName());
                             et_card_number.setText(cardNum);
                             et_expiry_date.setText(expiryDate);
                             et_cvc.setText("***");
                             btn_pay.setText("Update Card");

                        } else {
                            cardIsExisted = false;
                            btn_pay.setText("Add Card");

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
                String msg = "";
                String errorMsg = "";

                if(Parameter.CARD_TYPE_UNKNOWN.equals(card.getBrand())) {
                    setToast("Invalid Card " + card.getBrand());
                    return;
                }

                if(cardIsExisted) {
                    msg = "Card is Updated !";
                    errorMsg = "Card Update Failed !";
                } else {
                    msg = "Card is Added !";
                    errorMsg = "Card Add Failed !";
                }

                CreditCard newCard = new CreditCard();
                newCard.setCardHolderName(card.getName().toUpperCase(Locale.ROOT));
                newCard.setCardNum(card.getNumber());
                newCard.setCardType(card.getBrand());
                newCard.setExpiryMon(card.getExpMonth());
                newCard.setExpiryYear(card.getExpYear());
                newCard.setCvc(card.getCVC());
                String finalMsg = msg;
                String finalErrorMsg = errorMsg;

                firebase.collection("payments").document(userId)
                        .set(newCard)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), finalMsg,Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), finalErrorMsg,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    private void setToast(String input){
        Toast.makeText(getApplicationContext(),input,Toast.LENGTH_SHORT).show();
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
}