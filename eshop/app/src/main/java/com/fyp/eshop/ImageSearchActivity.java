package com.fyp.eshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fyp.eshop.databinding.ActivityImageSearchBinding;
import com.fyp.eshop.model.ProductSearchBean;
import com.fyp.eshop.model.ProductSearchResultBean;
import com.fyp.eshop.service.FirebaseManager;
import com.fyp.eshop.ui.search.SearchFragment;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageSearchActivity extends AppCompatActivity {

    private ActivityImageSearchBinding binding;
    private View root;
    private String[] mimeTypes = {"image/png","image/jpg","image/jpeg"};
    private final String URL = Parameter.SERVER_URL;
    private FirebaseFirestore firebase =FirebaseFirestore.getInstance();
    TextView tv_suggest_list;
    ImageView imageView;
    Button btn_search_img;
    RecyclerView recyclerView,suggestRecyclerView;
    ProductLstAdapter adapter,suggestAdapter;
    Uri uri;
    List<ProductSearchBean> searchBeanList = new ArrayList<>(); //v2 api

    List<ProductSearchBean> searchResultList = new ArrayList<>(); //v3 api
    List<ProductSearchBean> suggestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityImageSearchBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);

        imageView = binding.imgSelected;
        btn_search_img = binding.btnSearchImg;
        recyclerView = binding.imageSearchRecycleview;
        suggestRecyclerView = binding.suggestRecycleview;
        tv_suggest_list = binding.tvSuggestList;

//        tv_suggest_list.setVisibility(View.GONE);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(root.getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL));

        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(root.getContext(),2);
        suggestRecyclerView.setLayoutManager(gridLayoutManager2);
        suggestRecyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL));


//        adapter = new ProductLstAdapter(searchBeanList);   //v2 api


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(ImageSearchActivity.this)
                        .galleryMimeTypes(mimeTypes)
                        .crop()
                        .start(100);
            }
        });

        btn_search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri != null) {
                    Toast .makeText( getApplicationContext() , " Searching Product " , Toast . LENGTH_SHORT ).show();
//                    searchBeanList.removeAll(searchBeanList); // v2 api
                    searchResultList.removeAll(searchResultList);
                    suggestList.removeAll(suggestList);

                    if (adapter != null  &&  suggestAdapter != null ){
                        adapter.notifyDataSetChanged();
                        suggestAdapter.notifyDataSetChanged();
                    }

                    String img_src = uri.getPath();
                    File file = new File(img_src);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file",file.getName(),
                                    RequestBody.create(MediaType.parse("multipart/form-data"),file))
                            .build();

                    Request request = new Request.Builder()
//                            .url(new StringBuffer().append(URL).append(Parameter.SEARCH_API_V2).toString()) //v2
                            .url(new StringBuffer().append(URL).append(Parameter.SEARCH_API_V3).toString())   //v3
                            .post(requestBody)
                            .build();

                    OkHttpClient client = new OkHttpClient();

                    // v3 Api call
                      client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i ("CWD", "upload failed " + e.getLocalizedMessage());
                            setToast("Can't Find Similar Product");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            if(response.isSuccessful()) {
                                String json = response.body().string();
//                                ProductSearchResultBean bean = new Gson().fromJson(json, new TypeToken<ProductSearchResultBean>(){}.getType());
                                ProductSearchResultBean bean = new Gson().fromJson(json, ProductSearchResultBean.class);
                                Log.i ("CWD", "upload successful \n"+ json);

                                searchResultList = (List<ProductSearchBean>) bean.getSearch_results();
                                suggestList = (List<ProductSearchBean>) bean.getSuggested_results();


                                if(searchResultList.size() == 0 && suggestList.size() == 0 )  {
                                    setToast("Can't Find Similar Product");
//                                    return;
                                }
                                Log.i ("CWD", "searchResultList :" + searchResultList.size()  +"/ suggestLis : " + suggestList.size());

                                new Thread(() -> {
                                    //do something takes long time in the work-thread
                                    runOnUiThread(() -> {

                                        adapter = new ProductLstAdapter(searchResultList); //v3 Api
                                        recyclerView.setAdapter(adapter);

                                        suggestAdapter = new ProductLstAdapter(suggestList);
                                        suggestRecyclerView.setAdapter(suggestAdapter);
//                                        adapter.notifyDataSetChanged();
//                                        recyclerView.setAdapter(adapter);
//                                        suggestAdapter.notifyDataSetChanged();
//                                        suggestRecyclerView.setAdapter(suggestAdapter);
                                        Log.i("Test", String.valueOf(searchResultList.size() + " / " + String.valueOf(suggestList.size())));
                                        Log.i("Test", searchResultList.toString());
                                    });
                                }).start();

                            }

                        }
                    });

                    // v2 Api call
/*                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i ("CWD", "upload failed " + e.getLocalizedMessage());
                            setToast("Can't Find Similar Product");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            if(response.isSuccessful()) {
                                String json = response.body().string();
                                List<ProductSearchBean> beanList = new Gson().fromJson(json, new TypeToken<List<ProductSearchBean>>(){}.getType());
                                Log.i ("CWD", "upload successful \n"+ json);

                                for (ProductSearchBean bean : beanList) {
                                    searchBeanList.add(bean);
                                }

                                if(searchBeanList.size() == 0) {
                                    setToast("Can't Find Similar Product");
                                    return;
                                }

                                new Thread(() -> {
                                    //do something takes long time in the work-thread
                                    runOnUiThread(() -> {
                                        adapter.notifyDataSetChanged();
                                        recyclerView.setAdapter(adapter);
                                        Log.i("Test", String.valueOf(searchBeanList.size()));
                                    });
                                }).start();

                            }

                        }
                    });*/



                } else {
                    Toast .makeText( getApplicationContext() , " Please Upload Image " , Toast . LENGTH_SHORT ).show();
                }
            }
        });


    }
    private void setToast(String input){
        Looper.prepare();
        Toast.makeText(getApplicationContext(),input,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    private String toPercentage(float n) {
        return String.format("%.2f",n*100)+"%";
    }



    private class ProductLstAdapter extends RecyclerView.Adapter<ViewHolder> {
        List<ProductSearchBean> searchBeanList = new ArrayList<>();

        public ProductLstAdapter(List<ProductSearchBean> searchBeanList) {
            this.searchBeanList = searchBeanList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_nested,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ProductSearchBean bean = searchBeanList.get(position);
            holder.tvNesItemTitle.setText(bean.getProductName());
            holder.tv_price.setText("$" + String.valueOf(bean.getSelling_price()));
            if(bean.getScore() == null) {
                holder.tv_similar.setVisibility(View.GONE);
            } else {
                holder.tv_similar.setText(toPercentage(bean.getScore()));
            }

            Glide.with(holder.itemView.getContext()).load(bean.getProductThumb())
                    .placeholder(R.drawable.loading_shape).dontAnimate().into(holder.img_thumb);

            holder.constraintLayout.setOnClickListener(v -> {
                firebase.collection("products")
                        .whereEqualTo("id",bean.getId())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                                String docId = "";
                                for (DocumentSnapshot documentSnapshot : documents) {
                                    docId = documentSnapshot.getId();
                                }
                                FirebaseManager.getInstance().clickView(docId);
                                Intent intent = new Intent(ImageSearchActivity.this,ProductInfoActivity.class);
                                intent.putExtra("productId",docId);
                                ActivityOptionsCompat compat = ActivityOptionsCompat.
                                        makeSceneTransitionAnimation(ImageSearchActivity.this,holder.img_thumb, ViewCompat.getTransitionName(holder.img_thumb));
                                startActivity(intent,compat.toBundle());
                            }

                        });
            });

        }

        @Override
        public int getItemCount() {
            return searchBeanList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvNesItemTitle,tv_price,tv_similar;
        ImageView img_thumb;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNesItemTitle = itemView.findViewById(R.id.tv_nesItemTitle);
            img_thumb = itemView.findViewById(R.id.img_thumb);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_similar = itemView.findViewById(R.id.tv_similar);
            constraintLayout = itemView.findViewById(R.id.const_image_search);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100) {
            if(resultCode == Activity.RESULT_OK) {
                 uri = data.getData();
                imageView.setImageURI(uri);
            } else  if (resultCode ==  ImagePicker . RESULT_ERROR ) {
                Toast .makeText( this , ImagePicker .getError(data), Toast . LENGTH_SHORT ).show();
            } else {
                Toast .makeText( this , " Task Cancelled " , Toast . LENGTH_SHORT ).show();
            }
        }


    }
}