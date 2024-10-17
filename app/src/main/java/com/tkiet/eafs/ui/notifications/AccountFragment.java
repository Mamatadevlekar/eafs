package com.tkiet.eafs.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.tkiet.eafs.Add_productActivity;
import com.tkiet.eafs.Edit_account;
import com.tkiet.eafs.My_ProductActivity;
import com.tkiet.eafs.R;
import com.tkiet.eafs.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private TextView editProfileBtn, my_products, add_product, account_sign_out;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup the Toolbar
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
