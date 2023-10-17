package com.fyp.eshop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.eshop.R;
import com.fyp.eshop.model.Item;
import com.fyp.eshop.model.ItemCategory;

import java.util.List;

public class ItemCatAdapter extends RecyclerView.Adapter<ItemCatAdapter.CatViewHolder> implements ItemNestedAdapter.OnChildClick{

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<ItemCategory> catData;
    private OnItemClick onItemClick;
    private OnMoreClick onMoreClick;

    public ItemCatAdapter(List<ItemCategory> catData, OnItemClick onItemClick, OnMoreClick onMoreClick) {
        this.catData = catData;
        this.onItemClick = onItemClick;
        this.onMoreClick = onMoreClick;
    }

    public class CatViewHolder extends RecyclerView.ViewHolder{
        private TextView tvCatTitle;
        private RecyclerView recyclerView;
        private TextView tvMore;

        public CatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCatTitle = itemView.findViewById(R.id.tv_catTitle);
            recyclerView = itemView.findViewById(R.id.recyclerview_Nested);
            tvMore = itemView.findViewById(R.id.tv_more);
        }
    }
    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CatViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_cat,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        holder.tvCatTitle.setText(catData.get(position).getCatTitle());
        holder.recyclerView.setAdapter(new ItemNestedAdapter(catData.get(position).getNesData()
                ,position,this));
        holder.recyclerView.setRecycledViewPool(viewPool);

        holder.tvMore.setOnClickListener(v -> {
            onMoreClick.onMoreClick(holder.tvCatTitle.getText().toString());
        });
    }

    @Override
    public int getItemCount() {
        return catData.size();
    }


    @Override
    public void onChildClick(Item data, int parentPosition) {
        onItemClick.onItemClick(data,catData.get(parentPosition));
    }
    public interface OnItemClick{
        void onItemClick(Item itemData, ItemCategory catData);
    }
    public interface OnMoreClick {
        void onMoreClick(String str);
    }
}
