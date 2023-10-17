package com.fyp.eshop.ui.userprofile;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fyp.eshop.HomeCust;
import com.fyp.eshop.PaymentMethodActivity;
import com.fyp.eshop.R;
import com.fyp.eshop.UserProfileActivity;
import com.fyp.eshop.databinding.FragmentProfileBinding;
import com.fyp.eshop.ui.orders.OrderDetailsActivity;
import com.fyp.eshop.ui.orders.OrderHistoryActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private FragmentProfileBinding binding;
    private final FirebaseFirestore firebase = FirebaseFirestore.getInstance();
    LinearLayout linearProfile,linearOrder,linearPayment,linearLogout;
    TextView tv_profile_name;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        linearProfile = binding.linearProfile;
        linearOrder = binding.linearOrder;
        linearPayment = binding.linearPayment;
        linearLogout = binding.linearLogout;
        tv_profile_name = binding.tvProfileName;

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser().getUid());
        firebase.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        String fullName  = documentSnapshot.getString("family_name") + " " + documentSnapshot.getString("given_name");
                        tv_profile_name.setText(fullName);
                    }
                });

        linearProfile.setOnClickListener(v->{
            Intent intent = new Intent(binding.getRoot().getContext(), UserProfileActivity.class);
            startActivity(intent);
        });

        linearOrder.setOnClickListener(v -> {
            Intent intent = new Intent(binding.getRoot().getContext(), OrderHistoryActivity.class);
            startActivity(intent);
        });

        linearPayment.setOnClickListener(v -> {
            Intent intent = new Intent(binding.getRoot().getContext(), PaymentMethodActivity.class);
            startActivity(intent);
        });

        linearLogout.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), HomeCust.class);
            startActivity(intent);
            requireActivity().finish();
        });




        return binding.getRoot();
    }


}