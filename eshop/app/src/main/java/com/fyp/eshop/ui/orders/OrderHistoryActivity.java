package com.fyp.eshop.ui.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fyp.eshop.R;
import com.fyp.eshop.databinding.ActivityOrderHistoryBinding;
import com.fyp.eshop.model.OrderEntity;
import com.fyp.eshop.model.OrderProductEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private ActivityOrderHistoryBinding binding;
    private View root;
    private FirebaseFirestore firebase =FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String userId;
    RecyclerView recyclerView;
    List<OrderEntity> orderEntityList = new ArrayList<>();
    OrderListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);



        //init
        userId = firebaseAuth.getUid();
        showOrderList(userId);
        adapter = new OrderListAdapter(orderEntityList);

    }

    private void showOrderList(String userId) {
        Task q1 = firebase.collection("orders")
                .whereEqualTo("userId",userId)
                .orderBy("orderDt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot : documentSnapshots) {
                            OrderEntity orderEntity = snapshot.toObject(OrderEntity.class);
                            orderEntityList.add(orderEntity);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        Tasks.whenAllComplete(q1)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> task) {
                        if(task.isSuccessful()) {
                            recyclerView = binding.orderRecycleview;
                            recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(),DividerItemDecoration.VERTICAL));
                            recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
                            recyclerView.setAdapter(adapter);
                        }
                    }
                });

    }


    private class OrderListAdapter extends RecyclerView.Adapter<ViewHolder> {
        List<OrderEntity> orderEntityList;

        public  OrderListAdapter(List<OrderEntity> orderEntityList) {
            this.orderEntityList = orderEntityList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_list,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OrderEntity orderEntity = orderEntityList.get(position);

            OrderEntity passEntity = new OrderEntity();
//            passEntity.setOrderDt(orderEntity.getOrderDt());
            passEntity.setOrder_no(orderEntity.getOrder_no());
            passEntity.setStatus(orderEntity.getStatus());
            passEntity.setCardType(orderEntity.getCardType());
            passEntity.setCardNum(orderEntity.getCardNum());
            passEntity.setProductList(orderEntity.getProductList());
            passEntity.setOrder_total_price(orderEntity.getOrder_total_price());
            passEntity.setOrigin_price(orderEntity.getOrigin_price());
            passEntity.setDiscount(orderEntity.getDiscount());

            List<OrderProductEntity> productList = orderEntity.getProductList();
            holder.tv_order_list_more.setVisibility(View.GONE);
            holder.tv_order_list_no.setText(orderEntity.getOrder_no());
            holder.tv_order_list_status.setText(orderEntity.getStatus());
            holder.tv_order_list_price.setText(new StringBuffer().append("$").append(orderEntity.getOrder_total_price()));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = simpleDateFormat.format(orderEntity.getOrderDt().toDate());
            holder.tv_order_list_date.setText(date);

            if(productList.size()>3) {
                holder.tv_order_list_more.setVisibility(View.VISIBLE);
            }

            for (int i = 0; i<productList.size(); i++){
                if(i>2) {
                    break;
                }
                OrderProductEntity productEntity = productList.get(i);
                switch (i) {
                    case 0: loadImage(holder,productEntity.getProductThumb(),holder.img_order_0);
                            break;
                    case 1: loadImage(holder,productEntity.getProductThumb(),holder.img_order_1);
                        break;
                    case 2: loadImage(holder,productEntity.getProductThumb(),holder.img_order_2);
                        break;
                }

            }

            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OrderHistoryActivity.this,OrderDetailsActivity.class);
                    intent.putExtra("date",date);
                    intent.putExtra("orderEntity", (Serializable) passEntity);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderEntityList.size();
        }

        private void loadImage(ViewHolder holder,String url,ImageView view) {
            Glide.with(holder.itemView.getContext()).load(url)
                    .placeholder(R.drawable.loading_shape).dontAnimate().into(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View parent;
        TextView tv_order_list_no,tv_order_list_date,tv_order_list_status,tv_order_list_price,tv_order_list_more;
        ImageView img_order_0,img_order_1,img_order_2;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView;
            tv_order_list_no = itemView.findViewById(R.id.tv_order_list_no);
            tv_order_list_date = itemView.findViewById(R.id.tv_order_list_date);
            tv_order_list_status = itemView.findViewById(R.id.tv_order_list_status);
            tv_order_list_price = itemView.findViewById(R.id.tv_order_list_price);
            tv_order_list_more = itemView.findViewById(R.id.tv_order_list_more);
            img_order_0 = itemView.findViewById(R.id.img_order_0);
            img_order_1 = itemView.findViewById(R.id.img_order_1);
            img_order_2 = itemView.findViewById(R.id.img_order_2);
            constraintLayout = itemView.findViewById(R.id.constOrderList);
        }
    }
}