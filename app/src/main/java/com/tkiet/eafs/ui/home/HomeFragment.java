package com.tkiet.eafs.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tkiet.eafs.R;
import com.tkiet.eafs.classes.Product;
import com.tkiet.eafs.ui.home.ProductAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private LinearLayout categoriesContainer;
    private DatabaseReference databaseRef;
    private TextInputEditText searchEditText;
    private String currentUserId;

    private final String[] categories = {"Books", "PYQ Question Papers", "Equipments", "Notes"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        categoriesContainer = root.findViewById(R.id.categoriesContainer);
        searchEditText = root.findViewById(R.id.home_search_edit_text);
        databaseRef = FirebaseDatabase.getInstance().getReference("products");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadProducts();
        setupSearch();
        return root;
    }

    private void loadProducts() {
        categoriesContainer.removeAllViews();
        for (String category : categories) {
            View categoryView = getLayoutInflater().inflate(R.layout.category_section, categoriesContainer, false);
            TextView categoryTitle = categoryView.findViewById(R.id.categoryTitle);
            RecyclerView productRecyclerView = categoryView.findViewById(R.id.productRecyclerView);
            TextView noDataTextView = categoryView.findViewById(R.id.noDataTextView); // Ensure this TextView is in category_section.xml

            categoryTitle.setText(category);
            productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            List<Product> productList = new ArrayList<>();
            ProductAdapter adapter = new ProductAdapter(getContext(), productList);
            productRecyclerView.setAdapter(adapter);

            databaseRef.orderByChild("category").equalTo(category).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    productList.clear();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        // Check for null values
                        if (product != null && product.getTitle() != null && product.getAddedBy() != null &&
                                !product.getAddedBy().equals(currentUserId)) {
                            productList.add(product);
                        }
                    }
                    noDataTextView.setVisibility(productList.isEmpty() ? View.VISIBLE : View.GONE);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("HomeFragment", "Database error: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                }
            });

            categoriesContainer.addView(categoryView);
        }
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                performSearch(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String query) {
        categoriesContainer.removeAllViews();
        if (query.isEmpty()) {
            loadProducts();
            return;
        }

        View searchView = getLayoutInflater().inflate(R.layout.category_section, categoriesContainer, false);
        TextView categoryTitle = searchView.findViewById(R.id.categoryTitle);
        RecyclerView productRecyclerView = searchView.findViewById(R.id.productRecyclerView);
        TextView noDataTextView = searchView.findViewById(R.id.noDataTextView);

        categoryTitle.setText("Search Results");
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<Product> productList = new ArrayList<>();
        ProductAdapter adapter = new ProductAdapter(getContext(), productList);
        productRecyclerView.setAdapter(adapter);

        databaseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                productList.clear();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null && product.getTitle() != null && query != null &&
                            product.getTitle().toLowerCase().contains(query.toLowerCase()) &&
                            product.getAddedBy() != null && !product.getAddedBy().equals(currentUserId)) {
                        productList.add(product);
                    }
                }
                noDataTextView.setVisibility(productList.isEmpty() ? View.VISIBLE : View.GONE);
                adapter.notifyDataSetChanged();
            } else {
                Log.e("HomeFragment", "Database error: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });

        categoriesContainer.addView(searchView);
    }
}
