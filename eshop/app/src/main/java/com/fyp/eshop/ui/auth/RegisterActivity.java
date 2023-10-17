package com.fyp.eshop.ui.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fyp.eshop.databinding.ActivityRegisterBinding;
import com.fyp.eshop.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private User user;
    Button btn_register;
    EditText et_email,et_password,et_family,et_given,et_phone;
    String email,password,familyName,givenName,phoneNo,userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //declare
        btn_register = binding.btnRegRegister;
        et_email = binding.etRegEmail;
        et_password = binding.etRegPassword;
        et_family = binding.etRegFamily;
        et_given = binding.etRegGiven;
        et_phone = binding.etRegPhone;

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 email = et_email.getText().toString().trim();
                 password = et_password.getText().toString().trim();
                 familyName = et_family.getText().toString().trim();
                 givenName = et_given.getText().toString().trim();
                 phoneNo = et_phone.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    et_email.setError("Email Address is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    et_password.setError("Password is Required.");
                    return;
                }

                if (TextUtils.isEmpty(familyName)) {
                    et_family.setError("Family Name is Required.");
                    return;
                }

                if (TextUtils.isEmpty(givenName)) {
                    et_given.setError("Given Name is Required.");
                    return;
                }
                if (TextUtils.isEmpty(phoneNo)) {
                    et_phone.setError("Phone Number is Required.");
                    return;
                }

                if(phoneNo.length() != 8) {
                    et_phone.setError("Phone no must be 8 digits");
                    return;
                }

                mAuth = FirebaseAuth.getInstance();
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            userId =   Objects.requireNonNull(mAuth.getCurrentUser().getUid());
                            user = new User(userId,email,familyName,givenName,phoneNo,"Customer");

                            db.collection("users").document(userId).set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(getBaseContext(),"Registration Successful!",Toast.LENGTH_LONG).show();
//                                                FirebaseAuth.getInstance().signOut();
//                                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
//                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(getBaseContext(),"Registration Failed! " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });


                        } else {
                            Toast.makeText(getBaseContext(),"Registration Failed! " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });




    }
}