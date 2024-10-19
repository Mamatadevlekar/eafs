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
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import com.airbnb.lottie.LottieAnimationView;

public class Add_productActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView productImageView;
    private EditText productTitle, productDescription, productPrice;
    private AutoCompleteTextView categoryDropdown;
    private RadioGroup productTypeRadioGroup;
    private Button selectImageButton, submitProductButton;
    private LottieAnimationView lottieAnimationView;
    private Uri imageUri;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    private FirebaseAuth firebaseAuth;  // Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize views
        productTitle = findViewById(R.id.product_title);
        productDescription = findViewById(R.id.product_description);
        productPrice = findViewById(R.id.product_price);
        categoryDropdown = findViewById(R.id.category_dropdown);
        productTypeRadioGroup = findViewById(R.id.product_type_radio_group);
        selectImageButton = findViewById(R.id.select_image_button);
        submitProductButton = findViewById(R.id.submit_product_button);
        productImageView = findViewById(R.id.product_image);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.category_options));
        lottieAnimationView = findViewById(R.id.lottieAnimation);
        categoryDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDropdown.showDropDown(); // Show dropdown when clicked
            }
        });

        // Set the adapter to the AutoCompleteTextView
        categoryDropdown.setAdapter(adapter);

        // Initialize Firebase Storage and Realtime Database
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

            // Set the selected image to the CircleImageView
            productImageView.setImageURI(imageUri);

            // Hide the "Change Photo" button to avoid overlap
            selectImageButton.setVisibility(View.GONE);
        }
    }


    private void uploadProduct() {
        showLoadingFragment();
        final String title = productTitle.getText().toString();
        final String description = productDescription.getText().toString();
        final String price = productPrice.getText().toString();
        final String category = categoryDropdown.getText().toString();
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();

        // Get product type (Rent or Sell)
        int selectedTypeId = productTypeRadioGroup.getCheckedRadioButtonId();
        final String productType = (selectedTypeId == R.id.radio_rent) ? "Rent" : "Sell";

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(price) || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user UID
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        final String userId = currentUser.getUid();

        // Upload image to Firebase Storage
        final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        lottieAnimationView.setVisibility(View.GONE);
                        lottieAnimationView.cancelAnimation();

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
                        productData.put("addedBy", userId);  // Store the user's UID who added the product

                        databaseRef.child(productId).setValue(productData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hideLoadingFragment();
                                        Toast.makeText(Add_productActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                        finish();
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
