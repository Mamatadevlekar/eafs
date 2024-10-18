package com.tkiet.eafs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Add_productActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText productTitle, productDescription, productPrice;
    private AutoCompleteTextView categoryDropdown;
    private RadioGroup productTypeRadioGroup;
    private Button selectImageButton, submitProductButton;

    private Uri imageUri;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize views
        productTitle = findViewById(R.id.product_title);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        categoryDropdown = findViewById(R.id.category_dropdown);
        productTypeRadioGroup = findViewById(R.id.product_type_radio_group);
        selectImageButton = findViewById(R.id.select_image_button);
        submitProductButton = findViewById(R.id.submit_product_button);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.category_options));

        categoryDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDropdown.showDropDown(); // Show dropdown when clicked
            }
        });

        // Set the adapter to the AutoCompleteTextView
        categoryDropdown.setAdapter(adapter);

        // Initialize Firebase
        storageRef = FirebaseStorage.getInstance().getReference("product_images");
        databaseRef = FirebaseDatabase.getInstance().getReference("products");

        // Handle image selection
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();  // Opens file chooser for image selection
            }
        });

        // Submit product details to Firebase
        submitProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProduct();
            }
        });
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
            imageUri = data.getData();  // Get image URI
        }
    }

    private void uploadProduct() {
        final String title = productTitle.getText().toString();
        final String description = productDescription.getText().toString();
        final String price = productPrice.getText().toString();
        final String category = categoryDropdown.getText().toString();

        // Get product type (Rent or Sell)
        int selectedTypeId = productTypeRadioGroup.getCheckedRadioButtonId();
        final String productType = (selectedTypeId == R.id.radio_rent) ? "Rent" : "Sell";

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(price) || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload image to Firebase Storage
        final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();

                        // Store product details in Firebase Realtime Database
                        String productId = databaseRef.push().getKey();
                        Map<String, Object> productData = new HashMap<>();
                        productData.put("productId", productId);
                        productData.put("title", title);
                        productData.put("description", description);
                        productData.put("price", price);
                        productData.put("category", category);
                        productData.put("productType", productType);
                        productData.put("imageUrl", imageUrl);

                        databaseRef.child(productId).setValue(productData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Add_productActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Add_productActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Add_productActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
