package com.tkiet.eafs.ui.notifications;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.tkiet.eafs.LoginActivity;
import com.tkiet.eafs.My_ProductActivity;
import com.tkiet.eafs.R;
import com.tkiet.eafs.databinding.FragmentAccountBinding;
import com.tkiet.eafs.LoadingFragment;  // Import the LoadingFragment

public class AccountFragment extends Fragment {
    private ImageView profileImageView;
    private FragmentAccountBinding binding;
    private TextView editProfileBtn, my_products, add_product, account_sign_out, profile_name;
    private DatabaseReference userDatabaseRef;
    private StorageReference storageReference;
    private FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        // Initialize views
        profile_name = root.findViewById(R.id.profile_name);
        profileImageView = root.findViewById(R.id.text_account);
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        // Initialize other buttons
        editProfileBtn = root.findViewById(R.id.account_profile_tv);
        editProfileBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), Edit_account.class)));

        my_products = root.findViewById(R.id.my_products);
        my_products.setOnClickListener(v -> startActivity(new Intent(getActivity(), My_ProductActivity.class)));

        add_product = root.findViewById(R.id.add_product);
        add_product.setOnClickListener(v -> startActivity(new Intent(getActivity(), Add_productActivity.class)));

        account_sign_out = root.findViewById(R.id.account_sign_out);
        account_sign_out.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // Clears all preferences
            editor.apply();  // Save the changes

            // Redirect to the LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        loadUserData();  // Load user data with loading animation

        return root;
    }



    // Method to load user data from Firebase
    private void loadUserData() {

        userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String imageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                    profile_name.setText(name);

                    // Load profile image using Glide
                    if (imageUrl != null) {
                        Glide.with(AccountFragment.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.baseline_shopping_cart_24)
                                .error(R.drawable.cloud_download)
                                .into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AccountFragment", "Database Error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
