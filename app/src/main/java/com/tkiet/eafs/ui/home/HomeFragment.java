package com.tkiet.eafs.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tkiet.eafs.R;
import com.tkiet.eafs.ui.home.ProductAdapter;
import com.tkiet.eafs.classes.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private LinearLayout categoriesContainer;
    private DatabaseReference databaseRef;

    private final String[] categories = {"Books", "PYQ Question Papers", "Equipment", "Notes"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        categoriesContainer = root.findViewById(R.id.categoriesContainer);
        databaseRef = FirebaseDatabase.getInstance().getReference("products");

        loadProducts();

        return root;
    }

    private void loadProducts() {
        for (String category : categories) {
            // Inflate the category section layout
            View categoryView = getLayoutInflater().inflate(R.layout.category_section, categoriesContainer, false);
            TextView categoryTitle = categoryView.findViewById(R.id.categoryTitle);
            RecyclerView productRecyclerView = categoryView.findViewById(R.id.productRecyclerView);

            // Set category title
            categoryTitle.setText(category);

            // Configure RecyclerView
            productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<Product> productList = new ArrayList<>();
            ProductAdapter adapter = new ProductAdapter(getContext(), productList);
            productRecyclerView.setAdapter(adapter);

            // Fetch products by category
            databaseRef.orderByChild("category").equalTo(category).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                    adapter.notifyDataSetChanged(); // Notify adapter after data is loaded
                } else {
                    // Log errors
                    if (task.getException() != null) {
                        Log.e("HomeFragment", "Database error: " + task.getException().getMessage());
                    } else {
                        Log.e("HomeFragment", "No data found for category: " + category);
                    }
                }
            });

            // Add category section to the container
            categoriesContainer.addView(categoryView);
        }
    }

}
