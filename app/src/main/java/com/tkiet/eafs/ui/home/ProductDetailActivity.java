package com.tkiet.eafs.ui.home;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.tkiet.eafs.R;
import com.tkiet.eafs.classes.Product;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productTitle, productCategory, productPrice, productDescription;
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

        // Get the productId from the intent
        productId = getIntent().getStringExtra("productId");

        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference("products");

        // Load product details
        loadProductDetails();

        // Set up the back button
        findViewById(R.id.topAppBar).setOnClickListener(v -> onBackPressed());
    }

    private void loadProductDetails() {
        databaseRef.child(productId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    // Populate views with product data
                    Picasso.get().load(product.getImageUrl()).into(productImage);
                    productTitle.setText(product.getTitle());
                    productCategory.setText("Category: " + product.getCategory());
                    productPrice.setText("Price: ₹" + product.getPrice());
                    productDescription.setText(product.getDescription());
                }
            } else {
                // Handle failure (e.g., show a toast or error message)
            }
        });
    }
}
