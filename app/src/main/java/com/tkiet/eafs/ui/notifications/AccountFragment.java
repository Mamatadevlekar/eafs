package com.tkiet.eafs.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tkiet.eafs.Add_productActivity;
import com.tkiet.eafs.Edit_account;
import com.tkiet.eafs.My_ProductActivity;
import com.tkiet.eafs.R;
import com.tkiet.eafs.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {
    private ImageView profileImageView;
    private FragmentAccountBinding binding;
    private TextView editProfileBtn, my_products, add_product, account_sign_out ,profile_name;
    private DatabaseReference userDatabaseRef;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");
        loadUserData();
        profile_name = root.findViewById(R.id.profile_name);
        profileImageView = root.findViewById(R.id.text_account);
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            // Handle back button click
            requireActivity().onBackPressed();
        });

        // Initialize other buttons
        editProfileBtn = root.findViewById(R.id.account_profile_tv);
        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Edit_account.class);
            startActivity(intent);
        });

        my_products = root.findViewById(R.id.my_products);
        my_products.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), My_ProductActivity.class);
            startActivity(intent);
        });

        add_product = root.findViewById(R.id.add_product);
        add_product.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Add_productActivity.class);
            startActivity(intent);
        });

        account_sign_out = root.findViewById(R.id.account_sign_out);
        account_sign_out.setOnClickListener(v -> {
            // Sign out functionality can be added here
        });

        return root;
    }
    private void loadUserData() {
        userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                    // Set data to EditTexts
                    profile_name.setText(name);


                    // Load image if available using Glide
                    if (imageUrl != null) {
                        Glide.with(AccountFragment.this)
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
