package com.tkiet.eafs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tkiet.eafs.classes.Product;

import java.util.ArrayList;
import java.util.List;

public class My_ProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_product);

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view_my_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference("products");

        // Load products
        loadProducts(currentUser.getUid());
    }

    private void loadProducts(String userId) {
        showLoadingFragment();
        productList = new ArrayList<>();
        databaseRef.orderByChild("addedBy").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                hideLoadingFragment();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                }
                productAdapter = new ProductAdapter(productList);
                recyclerView.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
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
            View view = getLayoutInflater().inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            Product product = productList.get(position);
            holder.productName.setText(product.getTitle());
            Picasso.get().load(product.getImageUrl()).into(holder.productImage);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(My_ProductActivity.this, EditProductActivity.class);
                intent.putExtra("productId", product.getProductId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        public class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView productName;
            ImageView productImage;

            public ProductViewHolder(@NonNull View itemView) {
                super(itemView);
                productName = itemView.findViewById(R.id.product_name);
                productImage = itemView.findViewById(R.id.product_image);
            }
        }
    }
    private void showLoadingFragment() {
        LoadingFragment loadingFragment = new LoadingFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, loadingFragment, "LOADING_FRAGMENT")
                .commit();
    }
    private void hideLoadingFragment() {
        Fragment loadingFragment = getSupportFragmentManager().findFragmentByTag("LOADING_FRAGMENT");
        if (loadingFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(loadingFragment)
                    .commit();
        }
    }

}
