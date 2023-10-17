package com.fyp.eshop.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp.eshop.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder>{
    private ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();

    public ItemListAdapter(ArrayList<HashMap<String, String>> arrayList) {
        this.arrayList = arrayList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvId,tvSub1,tvSub2,tvAvg;
        private View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.textView_Id);
            tvSub1 = itemView.findViewById(R.id.textView_sub1);
            tvSub2 = itemView.findViewById(R.id.textView_sub2);
            tvAvg  = itemView.findViewById(R.id.textView_avg);
            mView  = itemView;
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvId.setText(arrayList.get(position).get("Id"));
        holder.tvSub1.setText(arrayList.get(position).get("Sub1"));
        holder.tvSub2.setText(arrayList.get(position).get("Sub2"));
        holder.tvAvg.setText(arrayList.get(position).get("Avg"));

        holder.mView.setOnClickListener((v)->{
            Log.i("Click",holder.tvAvg.getText().toString());
            Toast.makeText(v.getContext(), holder.tvAvg.getText(),Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
         return arrayList.size();
    }



}
