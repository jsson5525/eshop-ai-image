package com.fyp.eshop.ui.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fyp.eshop.R;
import com.fyp.eshop.databinding.ActivityOrderDetailsBinding;
import com.fyp.eshop.model.OrderEntity;
import com.fyp.eshop.model.OrderProductEntity;
import com.fyp.eshop.ui.cart.CartFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {

    private ActivityOrderDetailsBinding binding;
    private View root;
    OrderEntity orderEntity;
    TextView tv_order_date,tv_order_number,tv_order_status,
            tv_payment,tv_order_pay_method,tv_card_no,
            tv_paid,
            tv_origin_price,tv_discount_desc,tv_discount,tv_total;
    OrderDetailsAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailsBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);

        //Declare
        tv_order_date = binding.tvOrderDate;
        tv_order_number = binding.tvOrderNumber;
        tv_order_status = binding.tvOrderStatus;
        tv_payment = binding.tvPayment;
        tv_order_pay_method = binding.tvOrderPayMethod;
        tv_card_no = binding.tvCardNo;
        tv_paid = binding.tvPaid;
        tv_origin_price = binding.tvOriginPrice;
        tv_discount_desc = binding.tvDiscountDesc;
        tv_discount = binding.tvDiscount;
        tv_total = binding.tvTotal;
        recyclerView = binding.recycleviewOrderDetails;
        recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        //Init
        orderEntity = (OrderEntity) getIntent().getSerializableExtra("orderEntity");
        String date = getIntent().getStringExtra("date");
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String date = simpleDateFormat.format(orderEntity.getOrderDt().toDate());
        tv_order_date.setText(date);
        tv_order_number.setText(orderEntity.getOrder_no());
        tv_order_status.setText(orderEntity.getStatus());

        tv_origin_price.setText(new StringBuffer().append("$").append(orderEntity.getOrigin_price()));

        if(orderEntity.getDiscount() != null) {
            int discount = orderEntity.getOrigin_price() - orderEntity.getOrder_total_price();
            tv_discount.setText(new StringBuffer().append("$").append(discount));
            tv_discount_desc.setText(new StringBuffer().append(orderEntity.getDiscount().getDiscount_percentage()).append("% discount"));
        } else {
            tv_discount.setVisibility(View.GONE);
            tv_discount_desc.setVisibility(View.GONE);
        }
        tv_total.setText(new StringBuffer().append("$").append(orderEntity.getOrder_total_price()));

        tv_order_pay_method.setText(new StringBuffer().append(orderEntity.getCardType()).append(" Card Payment "));
        tv_card_no.setText(getCardNum(orderEntity.getCardNum()));


        tv_paid.setText(new StringBuffer().append("$").append(orderEntity.getOrder_total_price()));


        adapter = new OrderDetailsAdapter(orderEntity.getProductList());
        recyclerView.setAdapter(adapter);



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

    private class OrderDetailsAdapter extends RecyclerView.Adapter<ViewHolder> {
        List<OrderProductEntity> productEntityList = new ArrayList();

        public OrderDetailsAdapter(List<OrderProductEntity> productList) {
            this.productEntityList = productList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_cart,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OrderProductEntity productEntity = productEntityList.get(position);
            holder.tv_name.setText(productEntity.getProductName());
            holder.tv_qty.setText(String.valueOf(productEntity.getQty()));
            holder.tv_price.setText("$"+String.valueOf(productEntity.getSelling_price()));
            holder.tv_total.setText("$"+String.valueOf(productEntity.getTotal_price()));
            Glide.with(holder.itemView.getContext()).load(productEntity.getProductThumb())
                    .placeholder(R.drawable.loading_shape).dontAnimate().into(holder.img_thumb);
        }

        @Override
        public int getItemCount() {
            return productEntityList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View parent;
        ImageView img_thumb;
        TextView tv_name,tv_qty,tv_price,tv_total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView;
            img_thumb = itemView.findViewById(R.id.img_cart_thumb);
            tv_name = itemView.findViewById(R.id.tv_cart_product_name);
            tv_qty = itemView.findViewById(R.id.tv_cart_qty);
            tv_price = itemView.findViewById(R.id.tv_cart_price);
            tv_total = itemView.findViewById(R.id.tv_cart_total);
        }
    }
}