package com.fyp.eshop.adapter;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fyp.eshop.R;
import com.fyp.eshop.model.Item;
import com.fyp.eshop.model.Product;

import java.util.List;

public class ProductNestedAdapter extends RecyclerView.Adapter<ProductNestedAdapter.NesHolder>{
    private List<Product> nestedData;
    private OnChildClick childClick;
    private int parentPosition;

    public ProductNestedAdapter(List<Product> nestedData, int parentPosition, OnChildClick childClick) {
        this.nestedData = nestedData;
        this.childClick = childClick;
        this.parentPosition = parentPosition;
    }

    public class NesHolder extends RecyclerView.ViewHolder{
        TextView tvNesItemTitle,tv_price;
        ImageView img_thumb;

        public NesHolder(@NonNull View itemView) {
            super(itemView);
            tvNesItemTitle = itemView.findViewById(R.id.tv_nesItemTitle);
            img_thumb = itemView.findViewById(R.id.img_thumb);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }

    @NonNull
    @Override
    public NesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NesHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nested,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NesHolder holder, int position) {
        Product item = nestedData.get(position);
        String productName = item.getProductName();

        if(productName.length() > 20) {
            productName = productName.substring(0,20) + "\n" + productName.substring(21);
        }

        holder.tvNesItemTitle.setText(productName);
        holder.tv_price.setText("$" + String.valueOf(item.getSelling_price()));
        Glide.with(holder.itemView.getContext()).load(item.getProductThumb())
                .placeholder(R.drawable.loading_shape).dontAnimate().into(holder.img_thumb);


        holder.itemView.setOnClickListener(v -> {
            childClick.onChildClick(item,parentPosition,holder.img_thumb);
        });
    }

    @Override
    public int getItemCount() {
        return nestedData.size();
    }

    public interface OnChildClick {
        void onChildClick(Product data,int parentPosition, ImageView view);
    }


}
