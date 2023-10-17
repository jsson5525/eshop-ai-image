package com.fyp.eshop.ui.itemlist;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fyp.eshop.Parameter;
import com.fyp.eshop.ProductInfoActivity;
import com.fyp.eshop.R;
import com.fyp.eshop.adapter.ItemListAdapter;
import com.fyp.eshop.databinding.FragmentItemListBinding;
import com.fyp.eshop.model.ProductEntity;
import com.fyp.eshop.service.FirebaseManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


public class ItemListFragment extends Fragment {
    private FragmentItemListBinding binding;
    private RecyclerView recyclerView;
    private ItemListAdapter recyclerViewAdapter;
    private ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();
    private FirebaseFirestore firebase =FirebaseFirestore.getInstance();
    TextView tv_itemList_cat_desc;
    String category_name,category_id;
    List<ProductEntity> productEntityList = new ArrayList<>();
    ProductLstAdapter adapter;
    ImageButton imgBtn_view,imgBtn_price,imgBtn_sell;
    LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentItemListBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        //declare
        tv_itemList_cat_desc = binding.tvItemlistCatDesc;
        imgBtn_view = binding.imgBtnView;
        imgBtn_price = binding.imgBtnPrice;
        imgBtn_sell = binding.imgBtnSell;
        linearLayout = binding.linearItemList;

        //init
        Bundle extras = getArguments();
        if (extras != null) {
            category_name = extras.getString("category_name");
            category_id = extras.getString("category_id");
        }
        tv_itemList_cat_desc.setText(category_name);
        //set recyclerView
        recyclerView = binding.itemlistRecycleview;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(root.getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL));

        if(Parameter.NEW_AVAILABLE.equals(category_id) || Parameter.MOST_POPULAR.equals(category_id)
                || Parameter.HOT_SALE_ITEM.equals(category_id) || Parameter.MOST_CHEAP.equals(category_id)) {
            linearLayout.setVisibility(View.GONE);
        }

        adapter = new ProductLstAdapter(productEntityList);
        Task task = getProductData(category_id);

        Tasks.whenAllSuccess(task).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }
        });

        imgBtn_view.setOnClickListener(v ->{
            imgBtn_view.setImageResource(R.drawable.ic_sort_view_focus_24);
            imgBtn_price.setImageResource(R.drawable.ic_sort_price_24);
            imgBtn_sell.setImageResource(R.drawable.ic_sort_sell_24);
            getProductDataBySort(category_id,Parameter.VIEW_FIELD, Query.Direction.DESCENDING);
            setToast("Most Popular");
        });
        AtomicBoolean firstClick = new AtomicBoolean(false);
        imgBtn_price.setOnClickListener(v ->{
            imgBtn_view.setImageResource(R.drawable.ic_sort_view_24);
            imgBtn_price.setImageResource(R.drawable.ic_sort_price_focus_24);
            imgBtn_sell.setImageResource(R.drawable.ic_sort_sell_24);

            if(!firstClick.get()) {
                getProductDataBySort(category_id,Parameter.SELLING_PRICE_FIELD, Query.Direction.ASCENDING);
                firstClick.set(true);
                setToast("Most Cheap");
            } else {
                getProductDataBySort(category_id,Parameter.SELLING_PRICE_FIELD, Query.Direction.DESCENDING);
                firstClick.set(false);
                setToast("Most Expensive");
            }


        });

        imgBtn_sell.setOnClickListener(v ->{
            imgBtn_view.setImageResource(R.drawable.ic_sort_view_24);
            imgBtn_price.setImageResource(R.drawable.ic_sort_price_24);
            imgBtn_sell.setImageResource(R.drawable.ic_sort_sell_focus_24);
            getProductDataBySort(category_id,Parameter.SOLD_FIELD, Query.Direction.DESCENDING);
            setToast("Hot Sale Item");
        });


        return root;
    }

    private void setToast(String input){
        Toast.makeText(getActivity(),input,Toast.LENGTH_SHORT).show();
    }

    private Task getProductData(String catId) {
        Task q1;

        if(catId.equalsIgnoreCase(Parameter.NEW_AVAILABLE)) {
            q1 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                    .orderBy(Parameter.CREATE_DT_FIELD, Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductEntity productEntity = document.toObject(ProductEntity.class);
                                productEntity.setDocId(document.getId());
                                productEntityList.add(productEntity);
                            }
                        }
                    });

        } else if (catId.equalsIgnoreCase(Parameter.MOST_POPULAR)) {
            q1 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                    .orderBy(Parameter.VIEW_FIELD, Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductEntity productEntity = document.toObject(ProductEntity.class);
                                productEntity.setDocId(document.getId());
                                productEntityList.add(productEntity);
                            }
                        }
                    });
        } else if (catId.equalsIgnoreCase(Parameter.HOT_SALE_ITEM)) {
            q1 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                    .orderBy(Parameter.SOLD_FIELD, Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductEntity productEntity = document.toObject(ProductEntity.class);
                                productEntity.setDocId(document.getId());
                                productEntityList.add(productEntity);
                            }
                        }
                    });
        }else if (catId.equalsIgnoreCase(Parameter.MOST_CHEAP)) {
            q1 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                    .orderBy(Parameter.SELLING_PRICE_FIELD, Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductEntity productEntity = document.toObject(ProductEntity.class);
                                productEntity.setDocId(document.getId());
                                productEntityList.add(productEntity);
                            }
                        }
                    });
        }else {
            q1 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                    .whereEqualTo(Parameter.CATEGORY_ID_FIELD,catId)
                    .orderBy(Parameter.CREATE_DT_FIELD, Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductEntity productEntity = document.toObject(ProductEntity.class);
                                productEntity.setDocId(document.getId());
                                productEntityList.add(productEntity);
                            }

                        }
                    });
        }

        return q1;
    }

    private void getProductDataBySort(String catId,String sortField, Query.Direction direction) {
        Task q1;
        List<ProductEntity> productEntitySortList = new ArrayList<>();
        ProductLstAdapter sortAdapter = new ProductLstAdapter(productEntitySortList);

            q1 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                    .whereEqualTo(Parameter.CATEGORY_ID_FIELD,catId)
                    .orderBy(sortField, direction)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ProductEntity productEntity = document.toObject(ProductEntity.class);
                                productEntity.setDocId(document.getId());
                                productEntitySortList.add(productEntity);
                            }

                        }
                    });

        Tasks.whenAllSuccess(q1).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                sortAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(sortAdapter);
            }
        });

    }

    private class ProductLstAdapter extends RecyclerView.Adapter<ViewHolder> {
        List<ProductEntity> entityList = new ArrayList<>();

        public ProductLstAdapter(List<ProductEntity> entityList) {
            this.entityList = entityList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_nested,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ProductEntity entity = entityList.get(position);
            holder.tvNesItemTitle.setText(entity.getProductName());
            holder.tv_price.setText("$" + String.valueOf(entity.getSelling_price()));
            Glide.with(holder.itemView.getContext()).load(entity.getProductThumb())
                    .placeholder(R.drawable.loading_shape).dontAnimate().into(holder.img_thumb);

            holder.constraintLayout.setOnClickListener(v -> {
                FirebaseManager.getInstance().clickView(entity.getDocId());
                Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
                intent.putExtra("productId",entity.getDocId());
                ActivityOptionsCompat compat = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(),holder.img_thumb, ViewCompat.getTransitionName(holder.img_thumb));
                startActivity(intent,compat.toBundle());

            });

        }

        @Override
        public int getItemCount() {
            return entityList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvNesItemTitle,tv_price;
        ImageView img_thumb;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNesItemTitle = itemView.findViewById(R.id.tv_nesItemTitle);
            img_thumb = itemView.findViewById(R.id.img_thumb);
            tv_price = itemView.findViewById(R.id.tv_price);
            constraintLayout = itemView.findViewById(R.id.const_nested);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void makeData() {
        for (int i = 0;i<30;i++){
            HashMap<String,String> hashMap = new HashMap<>();
//            hashMap.put("Id","座號："+String.format("%02d",i+1));
            hashMap.put("Id","座號："+getArguments().get("category"));
            hashMap.put("Sub1",String.valueOf(new Random().nextInt(80) + 20));
            hashMap.put("Sub2",String.valueOf(new Random().nextInt(80) + 20));
            hashMap.put("Avg",String.valueOf(
                    (Integer.parseInt(hashMap.get("Sub1"))
                            +Integer.parseInt(hashMap.get("Sub2")))/2));

            arrayList.add(hashMap);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}