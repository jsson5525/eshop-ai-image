package com.fyp.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fyp.eshop.databinding.ActivityUserProfileBinding;
import com.fyp.eshop.model.User;
import com.fyp.eshop.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity {
    public static final String TAG = UserProfileActivity.class.getSimpleName();
    private ActivityUserProfileBinding binding;
    private View root;
    private final FirebaseFirestore firebase = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser;
    User user;
    TextView tv_info_email,tv_info_pwd,tv_info_family,tv_info_given,tv_info_phone;
    ImageButton imbBtn_rest_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);

        //Declare
        firebaseUser =  firebaseAuth.getCurrentUser();
        tv_info_email = binding.tvInfoEmail;
        tv_info_pwd = binding.tvInfoPwd;
        tv_info_family = binding.tvInfoFamily;
        tv_info_given = binding.tvInfoGiven;
        tv_info_phone = binding.tvInfoPhone;
        imbBtn_rest_password = binding.imgBtnResetPwd;

        //init
        tv_info_email.setText(firebaseUser.getEmail());
        tv_info_pwd.setText("********");

        Task q1 = firebase.collection("users")
                .document(firebaseUser.getUid())
                .get().addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                    }
                });
        Log.i(TAG,"user email " +  firebaseUser.getEmail());
        Log.i(TAG,"user name " +  firebaseUser.getDisplayName());
        Log.i(TAG,"user phone " +  firebaseUser.getPhoneNumber());


        imbBtn_rest_password.setOnClickListener(v -> {
            Log.d(TAG, "Rest Clicked");
            firebaseAuth.sendPasswordResetEmail(Parameter.TEST_EMAIL)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Log.d("Reset PWD", "Email sent");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, e.getLocalizedMessage());
                        }
                    });
        });


        Tasks.whenAllComplete(q1)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        tv_info_family.setText(user.getFamily_name());
                        tv_info_given.setText(user.getGiven_name());
                        tv_info_phone.setText(user.getPhone_no());
                    }
                });
    }
}