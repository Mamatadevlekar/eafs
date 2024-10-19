package com.tkiet.eafs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tkiet.eafs.classes.Product;

import java.util.HashMap;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText productTitle, productDescription, productPrice;
    private RadioGroup productTypeRadioGroup;
    private ImageView productImageView;
    private Button saveButton, changePhotoButton;
    private Uri newImageUri;
    private String oldImageUrl;  // To store the old image URL

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productTitle = findViewById(R.id.product_title);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        productTypeRadioGroup = findViewById(R.id.product_type_radio_group);
        productImageView = findViewById(R.id.product_image);
        saveButton = findViewById(R.id.submit_product_button);
        changePhotoButton = findViewById(R.id.select_image_button);

        String productId = getIntent().getStringExtra("productId");

        databaseRef = FirebaseDatabase.getInstance().getReference("products").child(productId);
        storageRef = FirebaseStorage.getInstance().getReference("product_images");

        loadProductDetails(productId);

        saveButton.setOnClickListener(v -> saveProductChanges(productId));
        changePhotoButton.setOnClickListener(v -> openFileChooser());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            newImageUri = data.getData();  // Get the new image URI

            // Set the new image to the ImageView
            productImageView.setImageURI(newImageUri);
        }
    }

    private void loadProductDetails(String productId) {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    productTitle.setText(product.getTitle());
                    productDescription.setText(product.getDescription());
                    productPrice.setText(product.getPrice());
                    oldImageUrl = product.getImageUrl();  // Store the old image URL
                    Picasso.get().load(product.getImageUrl()).into(productImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void saveProductChanges(String productId) {
        String title = productTitle.getText().toString();
        String description = productDescription.getText().toString();
        String price = productPrice.getText().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(price)) {
            Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the new image if a new one is selected
        if (newImageUri != null) {
            uploadNewImage(productId);
        } else {
            updateProductDetails(productId, oldImageUrl);  // No new image, just update details
        }
    }

    private void uploadNewImage(String productId) {
        if (oldImageUrl != null) {
            // Delete the old image from Firebase Storage
            StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
            oldImageRef.delete().addOnSuccessListener(aVoid -> {
                // Proceed with uploading the new image
                final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
                fileRef.putFile(newImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String newImageUrl = uri.toString();
                            updateProductDetails(productId, newImageUrl);  // Update product details with new image URL
                        });
                    }
                }).addOnFailureListener(e -> Toast.makeText(EditProductActivity.this, "Failed to upload new image", Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e -> Toast.makeText(EditProductActivity.this, "Failed to delete old image", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateProductDetails(String productId, String imageUrl) {
        String title = productTitle.getText().toString();
        String description = productDescription.getText().toString();
        String price = productPrice.getText().toString();

        Map<String, Object> productUpdates = new HashMap<>();
        productUpdates.put("title", title);
        productUpdates.put("description", description);
        productUpdates.put("price", price);
        productUpdates.put("imageUrl", imageUrl);

        databaseRef.updateChildren(productUpdates).addOnSuccessListener(aVoid -> {
            Toast.makeText(EditProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> Toast.makeText(EditProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show());
    }
}
