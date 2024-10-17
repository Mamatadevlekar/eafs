package com.tkiet.eafs;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

public class Add_productActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize the AutoCompleteTextView for the category dropdown
        AutoCompleteTextView categoryDropdown = findViewById(R.id.category_dropdown);

        // Create an ArrayAdapter using the category array from resources
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.category_options));

        // Set the adapter to the AutoCompleteTextView
        categoryDropdown.setAdapter(adapter);

        // Manually show the dropdown when the AutoCompleteTextView is clicked
        categoryDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDropdown.showDropDown(); // Show dropdown when clicked
            }
        });
    }
}
