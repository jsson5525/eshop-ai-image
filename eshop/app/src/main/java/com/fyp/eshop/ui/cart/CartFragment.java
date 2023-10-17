package com.fyp.eshop.ui.cart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.fyp.eshop.Parameter;
import com.fyp.eshop.PaymentMethodPayActivity;
import com.fyp.eshop.ProductInfoActivity;
import com.fyp.eshop.ProductInfoNCActivity;
import com.fyp.eshop.R;
import com.fyp.eshop.databinding.FragmentCartBinding;
import com.fyp.eshop.model.Discount;
import com.fyp.eshop.model.Product;
import com.fyp.eshop.model.ProductCart;
import com.fyp.eshop.service.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private View root;
    private FirebaseFirestore firebase =FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CartViewModel cartViewModel;
    String userId;
    RecyclerView recyclerView;
    TextView tv_total_price;
    EditText et_discount;
    Button btn_buy;
//    CartAdapter adapter;
    int total = 0;
    Boolean viewClicked =false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater,container,false);
        root = binding.getRoot();

        Fragment fragment = this.getParentFragment();

        List<ProductCart> productCartList = new ArrayList<>();

        cartViewModel.setTotalPrice(total);

        userId = firebaseAuth.getUid();
        btn_buy = binding.btnBuy;
        tv_total_price = binding.tvCartTotalPrice;
        et_discount = binding.etDiscount;

        tv_total_price.setText(String.valueOf(total));

        cartViewModel.getTotalPrice().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tv_total_price.setText(s);
            }
        });


        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productCartList == null || productCartList.size() <= 0) {
                    setToast("No Item in Cart");
                    return;
                }
                viewClicked =true;
                Discount discount = null;
                String code = et_discount.getText().toString().trim();
                if(!TextUtils.isEmpty(code)) {
                    List<Discount> discountList = new ArrayList<>();
                    firebase.collection(Parameter.DISCOUNT_COLLECTION)
                            .whereEqualTo(Parameter.PROMO_CODE_FIELD,code)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.isEmpty()) {
                                setToast("Invalid Code ");
                                return;
                            }
                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                            Discount discount = new Discount();
                            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                                discount = documentSnapshot.toObject(Discount.class);
                                discount.setDocId(documentSnapshot.getId());
                                discountList.add(discount);
                            }

                            if(!discountList.get(0).isActv_ind()) {
                                setToast("Invalid Code ");
                                return;
                            }

                            Bundle bundle = new Bundle();
                            bundle.putInt("totalPrice",total);
                            bundle.putSerializable("productList", (Serializable) productCartList);
                            bundle.putSerializable("discount", (Serializable) discountList.get(0));
                            Navigation.findNavController(root).navigate(R.id.action_navigation_cart_to_paymentMethodPayActivity,bundle);
                        }

                    });

                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("totalPrice",total);
                    bundle.putSerializable("productList", (Serializable) productCartList);
                    bundle.putSerializable("discount", (Serializable) discount);
                    Navigation.findNavController(root).navigate(R.id.action_navigation_cart_to_paymentMethodPayActivity,bundle);
                }


            }
        });

        showShoppingCartNew(userId,productCartList);

        return root;
    }

    private void checkDiscount(String code) {
        List<Discount> discountList = new ArrayList<>();
        Task q1 = firebase.collection(Parameter.DISCOUNT_COLLECTION)
                .whereEqualTo(Parameter.PROMO_CODE_FIELD,code)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                Discount discount = new Discount();
                for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                    discount = documentSnapshot.toObject(Discount.class);
                    discount.setDocId(documentSnapshot.getId());
                }
                discountList.add(discount);

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
//        setToast(viewClicked.toString());
        if (viewClicked) {
//            setToast("Run");
            cartViewModel.setTotalPrice(0);
            et_discount.setText("");
            total = 0;
//            productCartList.removeAll(productCartList);
            List<ProductCart> productCartList = new ArrayList<>();
            showShoppingCartNew(userId,productCartList);
            viewClicked = false;
            //        adapter.notifyDataSetChanged();
        }


    }

    public void showShoppingCartNew(String userId,List<ProductCart> productCartList ) {
        CartAdapter adapter = new CartAdapter(productCartList);
        firebase.collection("carts").
                whereEqualTo("userId",userId)
                .orderBy("createDt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                ProductCart cart = new ProductCart();
                                String docId = (String) document.get("productDocId");
                                cart.setDocId(document.getId());
                                cart.setProductDocId(docId);
                                cart.setProductId((String) document.get("productId"));
                                cart.setQty(Math.toIntExact((Long) document.get("qty")));
                                cart.setCreatedDt((Timestamp) document.get("createdDt"));

                                Task q2 = firebase.collection("products").document(docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot snapshot) {

                                        Product product = new Product();
                                        product.setDocId(snapshot.getId());
                                        product.setProductId(snapshot.getString("id"));
                                        product.setProductName(snapshot.getString("productName"));
                                        product.setProductThumb(snapshot.getString("productThumb"));
                                        product.setSelling_price(snapshot.getLong("selling_price").intValue());
                                        product.setProductCategory(snapshot.getString("productCategory"));
                                        cart.setProduct(product);
                                        cart.setTotalPrice(cart.getQty() * cart.getProduct().getSelling_price());
                                        productCartList.add(cart);
                                        adapter.notifyDataSetChanged();
                                        total = total + cart.getTotalPrice();

                                        cartViewModel.setTotalPrice(total);

                                        Log.i("productList", product.getProductName() + " / index ");

                                    }
                                });

                            }
                        } else {

                        }

                        recyclerView = binding.cartRecycleview;
                        recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(),DividerItemDecoration.VERTICAL));
                        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
//                    CartAdapter adapter = new CartAdapter(productCartList);
                        recyclerView.setAdapter(adapter);
                    }

                });
    }

    public void showShoppingCart(String userId) {
        DocumentReference docRef = firebase.collection("cart").document(userId);
        List<ProductCart> productCartList = new ArrayList<>();
        CartAdapter adapter = new CartAdapter(productCartList);


        Task q1 = firebase.collection("cart").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
             List<Map<String,Object>> maps = (List<Map<String, Object>>) documentSnapshot.get("productList");
             int index = 0;
             for(Map<String,Object> map: maps) {
                 ProductCart cart = new ProductCart();
                 String docId = (String) map.get("productDocId");
                 cart.setIndex(index++);
                 cart.setProductDocId(docId);
                 cart.setProductId((String) map.get("productId"));
                 cart.setQty(Math.toIntExact((Long) map.get("qty")));
                 cart.setCreatedDt((Timestamp) map.get("createdDt"));
                 Log.i("product", "docId "+ docId + " / index " + index);

                Task q2 = firebase.collection("products").document(docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                     @Override
                     public void onSuccess(DocumentSnapshot snapshot) {


                         Product product = new Product();
                         product.setDocId(snapshot.getId());
                         product.setProductId(snapshot.getString("id"));
                         product.setProductName(snapshot.getString("productName"));
                         product.setProductThumb(snapshot.getString("productThumb"));
                         product.setSelling_price(snapshot.getLong("selling_price").intValue());
                         product.setProductCategory(snapshot.getString("productCategory"));
                         cart.setProduct(product);
                         cart.setTotalPrice(cart.getQty() * cart.getProduct().getSelling_price());
                         productCartList.add(cart);
                         adapter.notifyDataSetChanged();
                         total = total + cart.getTotalPrice();

                         cartViewModel.setTotalPrice(total);

                         Log.i("productList", product.getProductName() + " / index ");

                     }
                 });


             }


            }
        });

        Tasks.whenAllComplete(q1).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                if(task.isSuccessful()) {
                    recyclerView = binding.cartRecycleview;
                    recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(),DividerItemDecoration.VERTICAL));
                    recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
//                    CartAdapter adapter = new CartAdapter(productCartList);
                    recyclerView.setAdapter(adapter);

                }
            }
        });

    }

    private void setToast(String input){
        Toast.makeText(getContext(),input,Toast.LENGTH_SHORT).show();
    }

    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
        private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
        private List<ProductCart> productCartList;

        public CartAdapter(List<ProductCart> productCartList) {
            this.productCartList = productCartList;
        }


        public class ViewHolder extends  RecyclerView.ViewHolder {
            View parent;
            ImageView img_thumb;
            TextView tv_name,tv_qty,tv_price,tv_total;
            Button btn_edit,btn_del;
            SwipeRevealLayout swipeRevealLayout;
            ConstraintLayout constraintLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                parent = itemView;
                img_thumb = itemView.findViewById(R.id.img_cart_thumb);
                tv_name = itemView.findViewById(R.id.tv_cart_product_name);
                tv_qty = itemView.findViewById(R.id.tv_cart_qty);
                tv_price = itemView.findViewById(R.id.tv_cart_price);
                tv_total = itemView.findViewById(R.id.tv_cart_total);
                btn_edit = itemView.findViewById(R.id.btn_swipe_edit);
                btn_del = itemView.findViewById(R.id.btn_swipe_delete);
                swipeRevealLayout = itemView.findViewById(R.id.swipeLayout);
                constraintLayout = itemView.findViewById(R.id.cart_layout);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_cart_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ProductCart cart = productCartList.get(position);
            viewBinderHelper.setOpenOnlyOne(true);
            viewBinderHelper.bind(holder.swipeRevealLayout,String.valueOf(position));
            holder.tv_name.setText(cart.getProduct().getProductName());
            holder.tv_qty.setText(String.valueOf(cart.getQty()));
            holder.tv_price.setText("$"+String.valueOf(cart.getProduct().getSelling_price()));
            holder.tv_total.setText("$"+String.valueOf(cart.getTotalPrice()));
            Glide.with(holder.itemView.getContext()).load(cart.getProduct().getProductThumb())
                    .placeholder(R.drawable.loading_shape).dontAnimate().into(holder.img_thumb);

            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ProductInfoNCActivity.class);
                    intent.putExtra("productId",cart.getProductDocId());
                    ActivityOptionsCompat compat = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(getActivity(),holder.img_thumb, ViewCompat.getTransitionName(holder.img_thumb));
                    startActivity(intent,compat.toBundle());
                }
            });

            holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(holder.itemView.getContext());
                    View view = getLayoutInflater().inflate(R.layout.custom_dialog,null);
                    alertDialog.setTitle("Edit");
                    alertDialog.setView(view);

                    ElegantNumberButton numberButton = view.findViewById(R.id.btn_dialog_eleg);
                    numberButton.setNumber(String.valueOf(cart.getQty()));
                    numberButton.setRange(1,10);

                    alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int qty = Integer.parseInt(numberButton.getNumber());
                            if(qty <= 0 ){
                                Toast.makeText(holder.itemView.getContext(),"Please select Quantity > 0",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            firebase.collection("carts").document(cart.getDocId())
                                    .update("qty",qty)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            total = total - cart.getTotalPrice();
                                            cart.setQty(qty);
                                            cart.setTotalPrice(qty * cart.getProduct().getSelling_price());
                                            holder.tv_qty.setText(String.valueOf(cart.getQty()));
                                            holder.tv_total.setText("$"+String.valueOf(cart.getTotalPrice()));
                                            total = total + cart.getTotalPrice();
                                            cartViewModel.setTotalPrice(total);
                                            holder.tv_qty.setText(numberButton.getNumber());
                                            holder.swipeRevealLayout.close(true);
                                            Toast.makeText(holder.itemView.getContext(),"Update Qty " + qty +" " +
                                                    cart.getProduct().getProductName(),Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    });

                    alertDialog.setNeutralButton("Cancel",(dialog, which) -> {
                        setToast("Cancel");
                    });
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                }
            });

            holder.btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Map<String,Object> data = new HashMap<>();
                    data.put("productDocId",cart.getProductDocId());
                    data.put("productId",cart.getProductId());
                    data.put("qty",cart.getQty());

                    firebase.collection("carts").document(cart.getDocId()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    holder.swipeRevealLayout.close(true);
                                    productCartList.remove(holder.getBindingAdapterPosition());
                                    notifyItemRemoved(holder.getBindingAdapterPosition());
                                    notifyItemRangeChanged(holder.getBindingAdapterPosition(),productCartList.size());
                                    total = total - cart.getTotalPrice();
                                    cartViewModel.setTotalPrice(total);
                                    Toast.makeText(holder.itemView.getContext(),"Remove " +
                                            cart.getProduct().getProductName(),Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


                }
            });


        }

        @Override
        public int getItemCount() {
            return productCartList.size();
        }
    }


}