package com.tkiet.eafs.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tkiet.eafs.R;
import com.tkiet.eafs.classes.Product;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage, userPhoto;
    private TextView productTitle, productCategory, productPrice, productDescription, userNameTextView;
    private DatabaseReference databaseRef;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize views
        productImage = findViewById(R.id.productImage);
        productTitle = findViewById(R.id.productTitle);
        productCategory = findViewById(R.id.productCategory);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        userPhoto = findViewById(R.id.userPhoto);  // Initialize userPhoto here
        userNameTextView = findViewById(R.id.userName);  // Initialize userNameTextView here

        // Get the productId from the intent
        productId = getIntent().getStringExtra("productId");

        if (productId == null || productId.isEmpty()) {
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
            finish();  // Close activity if productId is invalid
        } else {
            Log.d("ProductDetail", "Received productId: " + productId);
        }

        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("products");

        // Load product details
        loadProductDetails();

        // Set up the back button
        findViewById(R.id.topAppBar).setOnClickListener(v -> onBackPressed());
    }

    private void loadProductDetails() {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String title = snapshot.child("title").getValue(String.class);
                    String category = snapshot.child("category").getValue(String.class);
                    String price = snapshot.child("price").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    String userId = snapshot.child("addedBy").getValue(String.class);

                    // Set product details
                    productTitle.setText(title);
                    productCategory.setText(category);
                    productPrice.setText(price);
                    productDescription.setText(description);
                    Picasso.get().load(imageUrl).into(productImage);

                    // Load user data
                    loadUserDetails(userId);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserDetails(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("name").getValue(String.class);
                    String userPhotoUrl = snapshot.child("imageUrl").getValue(String.class);

                    // Set user data
                    userNameTextView.setText(userName);
                    Picasso.get().load(userPhotoUrl).into(userPhoto);

                    // Set up contact button to open chat
                    Button contactButton = findViewById(R.id.contactButton);
                    contactButton.setOnClickListener(v -> {
                        Intent intent = new Intent(ProductDetailActivity.this, ChatActivity.class);
                        intent.putExtra("userId", userId);
                        startActivity(intent);
                    });
                } else {
                    Toast.makeText(ProductDetailActivity.this, "User details not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProductDetailActivity.this, "Error loading user details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
