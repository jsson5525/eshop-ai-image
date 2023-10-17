package com.fyp.eshop.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.fyp.eshop.Parameter;
import com.fyp.eshop.ProductInfoActivity;
import com.fyp.eshop.R;
import com.fyp.eshop.adapter.ItemCatAdapter;
import com.fyp.eshop.adapter.ProductCatAdapter;
import com.fyp.eshop.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.denzcoskun.imageslider.models.SlideModel;
import com.fyp.eshop.model.Item;
import com.fyp.eshop.model.ItemCategory;
import com.fyp.eshop.model.Product;
import com.fyp.eshop.model.ProductCategory;
import com.fyp.eshop.service.FirebaseManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName()+"My";
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private FirebaseFirestore firebase;
    private FirestoreRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    ItemCatAdapter myAdapter;
    ProductCatAdapter productCatAdapter;
    View root;
    List<ItemCategory> data = new ArrayList<>();
    List<ProductCategory> productData = new ArrayList<>();
    Map<String,List<Product>> productMap = new HashMap<>();
    ImageSlider slider;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        slider =  binding.imgSlider;

//        slideModels.add(new SlideModel("https://storage.googleapis.com/kw_fyp_storage/light_sample_1.jpg",null));


        firebase = FirebaseFirestore.getInstance();

        MobileAds.initialize(getActivity(), initializationStatus -> {
            Map<String, AdapterStatus> map = initializationStatus.getAdapterStatusMap();
            for (Map.Entry<String, AdapterStatus> entry : map.entrySet()) {
                AdapterStatus adapterStatus = entry.getValue();
                AdapterStatus.State state = adapterStatus.getInitializationState();
                Log.i(TAG, "key = " + entry.getKey()
                        + ", state = " + state.name());
            }
        });
        AdView adview = binding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        adview.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.d(TAG, "onAdClosed: Ads are turned off (close your browser) ");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.d(TAG, "onAdFailedToLoad: Ad failed to load due to: "+loadAdError.getResponseInfo());
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.d(TAG, "onAdOpened: Ads are turned on (open browser)");
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: ad loading");
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.d(TAG, "onAdClicked: ad is clicked");
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.d(TAG, "onAdImpression: ad shown");
            }
        });

//        initData();
        initDateNew();
        /**set up RecyclerView*/

        swipeRefreshLayout = binding.refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.purple_500));
        swipeRefreshLayout.setOnRefreshListener(()->{
//            data.clear();
//            data = makeData();
//            setItemCatAdapter();
//            myAdapter.notifyDataSetChanged();
//            initData();
            initDateNew();
            swipeRefreshLayout.setRefreshing(false);

        });

        return root;
    }

    private Product createProduct(QueryDocumentSnapshot document) {
        Product product = new Product();
        product.setDocId(document.getId());
        product.setProductId(document.getString("id"));
        product.setProductName(document.getString("productName"));
        product.setProductThumb(document.getString("productThumb"));
        product.setSelling_price(document.getLong("selling_price").intValue());
        product.setProductCategory(document.getString("productCategory"));

        return  product;
    }

    private void initDateNew() {
        List<SlideModel> slideModels = new ArrayList();
        slideModels.add(new SlideModel(R.drawable.welcome, ScaleTypes.FIT));

        List<ProductCategory> data = new ArrayList<>();
        List<Product> newAvaList = new ArrayList<>();
        List<Product> popularList = new ArrayList<>();
        List<Product> hotSaleList = new ArrayList<>();
        List<Product> cheapList = new ArrayList<>();

        //Handle New Available
        Task t1 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                .orderBy(Parameter.CREATE_DT_FIELD, Query.Direction.DESCENDING)
                .limit(4)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            newAvaList.add(createProduct(document));
                        }
                        data.add(new ProductCategory(Parameter.NEW_AVAILABLE,Parameter.NEW_AVAILABLE,newAvaList));

                        if(newAvaList.size() > 0) {
                            slideModels.add(new SlideModel(newAvaList.get(0).getProductThumb(),Parameter.NEW_AVAILABLE, ScaleTypes.CENTER_INSIDE));
                        }

                    }
                });

        //Most Popular
        Task t2 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                .orderBy(Parameter.VIEW_FIELD, Query.Direction.DESCENDING)
                .limit(4)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            popularList.add(createProduct(document));
                        }
                        data.add(new ProductCategory(Parameter.MOST_POPULAR,Parameter.MOST_POPULAR,popularList));
                        if(newAvaList.size() > 0) {
                            slideModels.add(new SlideModel(popularList.get(0).getProductThumb(),Parameter.MOST_POPULAR, ScaleTypes.CENTER_INSIDE));
                        }
                    }
                });

        //Hot Sale Item
        Task t3 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                .orderBy(Parameter.SOLD_FIELD, Query.Direction.DESCENDING)
                .limit(4)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            hotSaleList.add(createProduct(document));
                        }
                        data.add(new ProductCategory(Parameter.HOT_SALE_ITEM,Parameter.HOT_SALE_ITEM,hotSaleList));
                        if(newAvaList.size() > 0) {
                            slideModels.add(new SlideModel(hotSaleList.get(0).getProductThumb(),Parameter.HOT_SALE_ITEM, ScaleTypes.CENTER_INSIDE));
                        }
                    }
                });

        //Most Cheap
        Task t4 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                .orderBy(Parameter.SELLING_PRICE_FIELD, Query.Direction.ASCENDING)
                .limit(4)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cheapList.add(createProduct(document));
                        }
                        data.add(new ProductCategory(Parameter.MOST_CHEAP,Parameter.MOST_CHEAP,cheapList));
                        if(newAvaList.size() > 0) {
                            slideModels.add(new SlideModel(cheapList.get(0).getProductThumb(),Parameter.MOST_CHEAP, ScaleTypes.CENTER_INSIDE));
                        }
                    }
                });




        Tasks.whenAllSuccess(t1,t2,t3,t4).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                nestedTaskList(data);
//                slider.notify();
                slider.setImageList(slideModels);

                slider.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onItemSelected(int i) {
                        Log.i(TAG,"slider selected " + i);
                    }
                });
            }
        });

    }

    private void nestedTaskList( List<ProductCategory> data) {

        List<Product> clothesList = new ArrayList<>();
        List<Product> toysList = new ArrayList<>();
        List<Product> homeList = new ArrayList<>();
        List<Product> electDevList = new ArrayList<>();

        //Handle Clothes
        Task q1 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                .whereEqualTo(Parameter.CATEGORY_ID_FIELD,Parameter.CLOTHES_ID)
                .orderBy(Parameter.CREATE_DT_FIELD, Query.Direction.DESCENDING)
                .limit(4)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            clothesList.add(createProduct(document));
                        }
                        data.add(new ProductCategory(Parameter.CLOTHES,Parameter.CLOTHES_ID,clothesList));
                    }
                });

        //Handle Toys
        Task q2 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                .whereEqualTo(Parameter.CATEGORY_ID_FIELD,Parameter.TOYS_ID)
                .orderBy(Parameter.CREATE_DT_FIELD, Query.Direction.DESCENDING)
                .limit(4)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            toysList.add(createProduct(document));
                        }
                        data.add(new ProductCategory(Parameter.TOYS,Parameter.TOYS_ID,toysList));
                    }
                });

        //Handle Home Appliance
        Task q3 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                .whereEqualTo(Parameter.CATEGORY_ID_FIELD,Parameter.HOME_APPLIANCE_ID)
                .orderBy(Parameter.CREATE_DT_FIELD, Query.Direction.DESCENDING)
                .limit(4)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            homeList.add(createProduct(document));
                        }
                        data.add(new ProductCategory(Parameter.HOME_APPLIANCE,Parameter.HOME_APPLIANCE_ID,homeList));
                    }
                });

        //Handle Electronic Dev
        Task q4 = firebase.collection(Parameter.PRODUCTS_COLLECTION)
                .whereEqualTo(Parameter.CATEGORY_ID_FIELD,Parameter.ELECT_DEVICE_ID)
                .orderBy(Parameter.CREATE_DT_FIELD, Query.Direction.DESCENDING)
                .limit(4)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            electDevList.add(createProduct(document));
                        }
                        data.add(new ProductCategory(Parameter.ELECT_DEVICE,Parameter.ELECT_DEVICE_ID,electDevList));
                    }
                });

        Tasks.whenAllSuccess(q1,q2,q3,q4).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                setProductCatAdapter(data);
            }
        });

    }

    private void setProductCatAdapter(List<ProductCategory> data) {
        RecyclerView recyclerView = binding.homeRecycleview;
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        productCatAdapter = new ProductCatAdapter(data, new ProductCatAdapter.OnItemClick() {
            @Override
            public void onItemClick(Product itemData, ProductCategory catData, ImageView view) {
                FirebaseManager.getInstance().clickView(itemData.getDocId());
                Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
                intent.putExtra("productId",itemData.getDocId());
                ActivityOptionsCompat compat = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(),view, ViewCompat.getTransitionName(view));
                startActivity(intent,compat.toBundle());


            }
        }, new ProductCatAdapter.OnMoreClick() {
                    @Override
                    public void onMoreClick(String catTitle,String catId) {
                        Bundle bundle = new Bundle();
                        bundle.putString("category_name", catTitle);
                        bundle.putString("category_id",catId);
                        Navigation.findNavController(root).navigate(R.id.navigation_itemList,bundle);
                    }
                }
        );
        recyclerView.setAdapter(productCatAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showProductsList() {
        Query query = firebase.collection("products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, new SnapshotParser<Product>() {
                    @NonNull
                    @Override
                    public Product parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        Product product = new Product();
                        product.setProductId(snapshot.getString("id"));
                        product.setProductName(snapshot.getString("productName"));
                        product.setProductThumb(snapshot.getString("productThumb"));
                        product.setSelling_price(snapshot.getLong("selling_price").intValue());
                        return product;
                    }
                })
                .build();

        adapter = new FirestoreRecyclerAdapter<Product, ProductCatAdapter.CatViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ProductCatAdapter.CatViewHolder holder, int position, @NonNull Product model) {

            }

            @NonNull
            @Override
            public ProductCatAdapter.CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }



        };

    }

    private void initData() {
         List<Product> products = new ArrayList<>();
         List<Product> lightList = new ArrayList<>();


        Task q1 =  firebase.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult() ) {

                        if(("Light").equalsIgnoreCase(document.getString("productCategory"))) {
                            lightList.add(createProduct(document));
                        } else {
                            products.add(createProduct(document));
                        }

                    }

                    productMap.put("All",products);
                    productMap.put(Parameter.HOME_APPLIANCE,lightList);

//                    List<ItemCategory> data = makeProductData(products);
//                    setItemCatAdapter(data);
//                    productData = makeProductData2(products);
//                    setProductCatAdapter(productData);



                } else {
                    Log.i("Data", "Failed");
                }
            }
        });


        Task q2 = firebase.collection("products").
                whereEqualTo("productCategory","light").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    }
                });

        Tasks.whenAllSuccess(q1).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                Log.i("Object", objects.toString());
                productData = makeProductData3();
                setProductCatAdapter(productData);
            }
        });

        Tasks.whenAllComplete(q1).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                if(task.isSuccessful()) {
                    for (Task<?> document: task.getResult() ) {

                    }
                }
            }
        });

        }

    private void setItemCatAdapter() {
        RecyclerView recyclerView = binding.homeRecycleview;
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        //設置Adapter以及點擊回饋
        myAdapter = new ItemCatAdapter(data, new ItemCatAdapter.OnItemClick() {
            @Override
            public void onItemClick(Item data, ItemCategory myData) {
                Toast.makeText(getContext()
                        , "click "+myData.getCatTitle()+"  "+data.getItemTitle()
                        , Toast.LENGTH_SHORT).show();
            }

        },
                new ItemCatAdapter.OnMoreClick() {
                    @Override
                    public void onMoreClick(String str) {

                        Toast.makeText(getContext()
                                , "click "+str+" "
                                , Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(root).navigate(R.id.navigation_itemList);
                    }
                }

        );
        recyclerView.setAdapter(myAdapter);
    }

    private void setItemCatAdapter(List<ItemCategory> data) {
        RecyclerView recyclerView = binding.homeRecycleview;
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        myAdapter = new ItemCatAdapter(data, new ItemCatAdapter.OnItemClick() {
            @Override
            public void onItemClick(Item data, ItemCategory myData) {
                Toast.makeText(getContext()
                        , "click "+myData.getCatTitle()+" "+data.getItemTitle()
                        , Toast.LENGTH_SHORT).show();
            }

        },
                new ItemCatAdapter.OnMoreClick() {
                    @Override
                    public void onMoreClick(String str) {

                        Toast.makeText(getContext()
                                , "click "+str+" "
                                , Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(root).navigate(R.id.navigation_itemList);
                    }
                }

        );
        recyclerView.setAdapter(myAdapter);
    }

    private List<ProductCategory> makeProductData3(){
        List<ProductCategory> data = new ArrayList<>();

        productMap.forEach((key,value) -> {
            data.add(new ProductCategory(key,value));
        });

        return data;
    }

}