package com.tkiet.eafs.ui.notifications;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tkiet.eafs.R;
import com.tkiet.eafs.classes.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference productsRef;
    private List<Product> productList = new ArrayList<>();
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productsRef = FirebaseDatabase.getInstance().getReference("products");

        loadProducts();

        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);
    }

    private void loadProducts() {
        productsRef.orderByChild("isVerified").equalTo(false) // Fetch only unverified products
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            Product product = productSnapshot.getValue(Product.class);
                            if (product != null) {
                                productList.add(product);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("AdminActivity", "Database Error: " + error.getMessage());
                    }
                });
    }


    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

        private List<Product> productList;

        public ProductAdapter(List<Product> productList) {
            this.productList = productList;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = productList.get(position);
            holder.bind(product);
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView title, description, price;
            ImageView productImage;
            Button verifyButton, deleteButton;

            public ProductViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.product_title);
                description = itemView.findViewById(R.id.product_description);
                price = itemView.findViewById(R.id.product_price);
                productImage = itemView.findViewById(R.id.product_image);
                verifyButton = itemView.findViewById(R.id.btn_verify);
                deleteButton = itemView.findViewById(R.id.btn_delete);
            }

            public void bind(Product product) {
                title.setText(product.getTitle());
                description.setText(product.getDescription());
                price.setText("Price: " + product.getPrice());

                Glide.with(AdminActivity.this)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.loading)
                        .into(productImage);

                verifyButton.setOnClickListener(v -> verifyProduct(product));
                deleteButton.setOnClickListener(v -> deleteProduct(product));
            }
        }
    }

    private void verifyProduct(Product product) {
        String productId = product.getProductId();
        String adminId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Use a single write operation
        productsRef.child(productId).updateChildren(new HashMap<String, Object>() {{
            put("isVerified", true);
            put("verifiedBy", adminId);
        }}).addOnSuccessListener(aVoid -> {
            Toast.makeText(AdminActivity.this, "Product Verified", Toast.LENGTH_SHORT).show();
            // Remove from the current list
            productList.remove(product);
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show());
    }

    private void deleteProduct(Product product) {
        String productId = product.getProductId();
        productsRef.child(productId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminActivity.this, "Product Deleted", Toast.LENGTH_SHORT).show();
                    // Remove from the current list
                    productList.remove(product);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Deletion Failed", Toast.LENGTH_SHORT).show());
    }
}
