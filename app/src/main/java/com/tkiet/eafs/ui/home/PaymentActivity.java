package com.tkiet.eafs.ui.home;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.tkiet.eafs.R;

public class PaymentActivity extends AppCompatActivity {

    private static final int UPI_PAYMENT_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Get payment data from intent
        String amount = getIntent().getStringExtra("amount");
        String name = getIntent().getStringExtra("name");

        if (amount == null || amount.isEmpty()) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Start UPI Payment
        payUsingUPI(amount, name, "mamatadevlekar2002@oksbi", "Product Payment");
    }

    private void payUsingUPI(String amount, String name, String upiId, String note) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        try {
            startActivityForResult(chooser, UPI_PAYMENT_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No UPI app found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPI_PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                String response = data.getStringExtra("response");
                if (response != null && response.toLowerCase().contains("status=success")) {
                    Toast.makeText(this, "Payment successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Payment failed or canceled", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Payment failed or canceled", Toast.LENGTH_LONG).show();
            }
            finish(); // Close PaymentActivity after payment attempt
        }
    }
}
