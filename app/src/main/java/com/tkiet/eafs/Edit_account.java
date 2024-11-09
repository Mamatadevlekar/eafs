package com.tkiet.eafs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.material.textfield.TextInputEditText;

import com.google.android.material.textfield.TextInputLayout;
import com.tkiet.eafs.classes.User;

import java.io.IOException;

public class Edit_account extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextInputEditText editProfileName, editProfileAddress, editProfilePhone;
    private Button btnSaveProfile, selectProfilePhotoButton;
    private LottieAnimationView lottieAnimation;
    private ProgressBar progressBarSaveProfile;
    private ImageView profileImageView;
    private Uri imageUri;

    // Firebase references
    private DatabaseReference userDatabaseRef;
    private StorageReference storageReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_account);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Ensure user is signed in
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if user is not authenticated
            return;
        }

        // Initialize Firebase references
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        // Initialize views
        editProfileName = findViewById(R.id.edit_profile_name);
        editProfileAddress = findViewById(R.id.edit_profile_address);
        editProfilePhone = findViewById(R.id.edit_profile_phone);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        selectProfilePhotoButton = findViewById(R.id.select_profile_photo_button);
        lottieAnimation = findViewById(R.id.lottieAnimation);
        progressBarSaveProfile = findViewById(R.id.progress_bar_save_profile);
        profileImageView = findViewById(R.id.profile_image_view);

        // Load existing user data
        loadUserData();

        // Set button click listeners
        btnSaveProfile.setOnClickListener(v -> saveUserProfile());
        selectProfilePhotoButton.setOnClickListener(v -> openImageChooser());
    }

    private void loadUserData() {
        userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                    // Set data to EditTexts
                    editProfileName.setText(name);
                    editProfileAddress.setText(address);
                    editProfilePhone.setText(phone);

                    // Load image if available using Glide
                    if (imageUrl != null) {
                        Glide.with(Edit_account.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.baseline_shopping_cart_24) // Add a placeholder image
                                .error(R.drawable.cloud_download) // Add an error image
                                .into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("EditAccountActivity", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    private void saveUserProfile() {
        // Show loading animation
        lottieAnimation.setVisibility(View.VISIBLE);
        lottieAnimation.playAnimation();
        progressBarSaveProfile.setVisibility(View.VISIBLE);

        // Get user input
        String name = editProfileName.getText().toString().trim();
        String address = editProfileAddress.getText().toString().trim();
        String phone = editProfilePhone.getText().toString().trim();

        // Create a User object
        User user = new User(name, address, phone);

        // Save to database
        userDatabaseRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Upload image if selected
                if (imageUri != null) {
                    uploadImage();
                } else {
                    // Hide loading animation
                    lottieAnimation.setVisibility(View.GONE);
                    progressBarSaveProfile.setVisibility(View.GONE);
                    Toast.makeText(Edit_account.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Edit_account.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                lottieAnimation.setVisibility(View.GONE);
                progressBarSaveProfile.setVisibility(View.GONE);
            }
        });
    }

    private void uploadImage() {
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child("profileImage.jpg");

            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    userDatabaseRef.child("profileImage").setValue(uri.toString()).addOnCompleteListener(task -> {
                        // Hide loading animation
                        lottieAnimation.setVisibility(View.GONE);
                        progressBarSaveProfile.setVisibility(View.GONE);
                        Toast.makeText(Edit_account.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    });
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(Edit_account.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                lottieAnimation.setVisibility(View.GONE);
                progressBarSaveProfile.setVisibility(View.GONE);
            });
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Display the selected image in the ImageView
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                // Load the image into CircleImageView
                Glide.with(this).load(bitmap).into(profileImageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

