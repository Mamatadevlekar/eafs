package com.tkiet.eafs;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistrationActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonSignup;
    private TextView textViewLogin;
    private SwitchMaterial switchTermsAndConditions;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextName = findViewById(R.id.signup_name_edit_text);
        editTextEmail = findViewById(R.id.signup_email_edit_text);
        editTextPassword = findViewById(R.id.signup_password_edit_text);
        editTextConfirmPassword = findViewById(R.id.signup_cnf_password_edit_text);
        buttonSignup = findViewById(R.id.signup_signup_btn);
        textViewLogin = findViewById(R.id.signup_login_text_view);
        switchTermsAndConditions = findViewById(R.id.signup_policy_switch);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonSignup.setOnClickListener(view -> registerUser());

        textViewLogin.setOnClickListener(view -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return;
        }

        if (!isPasswordValid(password)) {
            editTextPassword.setError("Password must be 8-12 characters, include at least one uppercase letter, one lowercase letter, and one special character.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            return;
        }

        if (!switchTermsAndConditions.isChecked()) {
            Toast.makeText(RegistrationActivity.this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            user.sendEmailVerification().addOnCompleteListener(verificationTask -> {
                                                if (verificationTask.isSuccessful()) {
                                                    Toast.makeText(RegistrationActivity.this, "Registration successful. Please check your email for verification.", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Add this helper method to validate the password
    private boolean isPasswordValid(String password) {
        // Regular expression for the password validation
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,12}$";
        return password.matches(passwordPattern);
    }

}
