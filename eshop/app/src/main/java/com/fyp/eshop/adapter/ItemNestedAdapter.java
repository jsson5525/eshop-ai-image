package com.fyp.eshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.eshop.R;
import com.fyp.eshop.model.Item;

import java.util.List;

public class ItemNestedAdapter extends RecyclerView.Adapter<ItemNestedAdapter.NesHolder>{
    private List<Item> nestedData;
    private OnChildClick childClick;
    private int parentPosition;

    public ItemNestedAdapter(List<Item> nestedData, int parentPosition,  OnChildClick childClick) {
        this.nestedData = nestedData;
        this.childClick = childClick;
        this.parentPosition = parentPosition;
    }

    public class NesHolder extends RecyclerView.ViewHolder{
        TextView tvNesItemTitle;

        public NesHolder(@NonNull View itemView) {
            super(itemView);
            tvNesItemTitle = itemView.findViewById(R.id.tv_nesItemTitle);
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
        Item item = nestedData.get(position);
        holder.tvNesItemTitle.setText(item.getItemTitle());

        holder.itemView.setOnClickListener(v -> {
            childClick.onChildClick(item,parentPosition);
        });
    }

    @Override
    public int getItemCount() {
        return nestedData.size();
    }

    public interface OnChildClick {
        void onChildClick(Item data,int parentPosition);
    }


}
