package com.tkiet.eafs;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.tkiet.eafs.databinding.ActivityBottomBinding;

public class BottomActivity extends AppCompatActivity {

    private ActivityBottomBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the ActionBar if present
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = ActivityBottomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get reference to BottomNavigationView from binding
        BottomNavigationView navView = binding.navView;

        // Set up the NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
