package com.fyp.eshop.ui.search;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.fyp.eshop.ImageSearchActivity;
import com.fyp.eshop.ProductInfoActivity;
import com.fyp.eshop.R;
import com.fyp.eshop.adapter.ProductNestedAdapter;
import com.fyp.eshop.databinding.FragmentItemListBinding;
import com.fyp.eshop.databinding.FragmentSearchBinding;
import com.fyp.eshop.model.Product;
import com.fyp.eshop.service.FirebaseManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private View root;
    private FirebaseFirestore firebase =FirebaseFirestore.getInstance();

    ConstraintLayout camera_search_layout;
    ImageButton imgBtn_search_main,imgBtn_camera,imgBtn_hide;
    EditText et_search_main;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater,container,false);
        root = binding.getRoot();

        camera_search_layout = binding.cameraSearchLayout;
        et_search_main = binding.etSearchMain;
        imgBtn_search_main = binding.imgBtnSearchMain;
        imgBtn_camera = binding.imgBtnCamera;
        imgBtn_hide = binding.imgBtnHide;

        recyclerView = binding.searchRecycleview;


        camera_search_layout.setVisibility(View.GONE);

//        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(root.getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL));


        imgBtn_search_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_search_main.getText().toString().equals("")) {
                    Toast.makeText(getActivity(),"Please enter something to search",Toast.LENGTH_SHORT).show();
                } else {
                    setToast("Searching - "+et_search_main.getText().toString());
                    searchProductList(et_search_main.getText().toString());
                }
            }
        });

        imgBtn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                camera_search_layout.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getActivity(), ImageSearchActivity.class);
                startActivity(intent);
            }
        });

        imgBtn_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera_search_layout.setVisibility(View.GONE);
            }
        });


        return  root;
    }

    private void searchProductList(String input) {
        Query query = firebase.collection("products")
                .whereGreaterThanOrEqualTo("productName",input)
                .whereLessThan("productName",input+"\uf8ff");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, new SnapshotParser<Product>() {
                    @NonNull
                    @Override
                    public Product parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Product product = new Product();
                        product.setDocId(snapshot.getId());
                        product.setProductId(snapshot.getString("id"));
                        product.setProductName(snapshot.getString("productName"));
                        product.setProductThumb(snapshot.getString("productThumb"));
                        product.setSelling_price(snapshot.getLong("selling_price").intValue());
                        product.setProductCategory(snapshot.getString("productCategory"));
                        return product;
                    }
                }).build();

        FirestoreRecyclerAdapter adapter = new FirestoreRecyclerAdapter<Product,ProductListViewHolder>(options) {

            @NonNull
            @Override
            public ProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_nested,parent,false);
                return new ProductListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductListViewHolder holder, int position, @NonNull Product model) {

                holder.tvNesItemTitle.setText(model.getProductName());
                holder.tv_price.setText("$" + String.valueOf(model.getSelling_price()));
                Glide.with(holder.itemView.getContext()).load(model.getProductThumb())
                        .placeholder(R.drawable.loading_shape).dontAnimate().into(holder.img_thumb);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseManager.getInstance().clickView(model.getDocId());
                        Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
                        intent.putExtra("productId",model.getDocId());
                        ActivityOptionsCompat compat = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(getActivity(),holder.img_thumb, ViewCompat.getTransitionName(holder.img_thumb));
                        startActivity(intent,compat.toBundle());
//                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onDataChanged() {
                if(getItemCount() == 0) {
                    setToast("Can't Find Product");
                }
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private void setToast(String input){
        Toast.makeText(getActivity(),input,Toast.LENGTH_SHORT).show();
    }

    public static class ProductListViewHolder extends RecyclerView.ViewHolder {

        TextView tvNesItemTitle,tv_price;
        ImageView img_thumb;

        public ProductListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNesItemTitle = itemView.findViewById(R.id.tv_nesItemTitle);
            img_thumb = itemView.findViewById(R.id.img_thumb);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }

}