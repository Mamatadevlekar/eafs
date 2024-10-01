package com.tkiet.eafs;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.tkiet.eafs.databinding.ActivityBottomBinding;

public class BottomActivity extends AppCompatActivity {

    private ActivityBottomBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBottomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Set up the NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_bottom);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}