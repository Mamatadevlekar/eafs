package com.tkiet.eafs;


import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference to the LottieAnimationView
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);

        // Add an animator listener to detect when the animation ends
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // Optional: Code to run when the animation starts
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Navigate to SignUpActivity when the animation ends
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish(); // Close the splash screen activity
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // Optional: Code to run when the animation is canceled
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // Optional: Code to run when the animation repeats
            }
        });
    }
}
