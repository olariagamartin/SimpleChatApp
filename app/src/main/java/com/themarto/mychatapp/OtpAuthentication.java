package com.themarto.mychatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.udacity.mychatapp.databinding.ActivityOtpAuthenticationBinding;

public class OtpAuthentication extends AppCompatActivity {

    private ActivityOtpAuthenticationBinding binding;
    String enterOtp;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        setChangeNumberTextListener();
        setVerifyOtpButtonListener();

    }

    private void setChangeNumberTextListener() {
        binding.changeNumber.setOnClickListener(v -> {
            Intent intent = new Intent(OtpAuthentication.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }


    private void setVerifyOtpButtonListener() {
        binding.verifyOtpBtn.setOnClickListener(v -> {
            enterOtp = binding.otpEditText.getText().toString();
            if (enterOtp.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Introduce the you received", Toast.LENGTH_SHORT).show();
            } else {
                binding.verifingOtpProgress.setVisibility(View.VISIBLE);
                String codeReceived = getIntent().getStringExtra("otp");
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeReceived, enterOtp);
                signInWithPhoneCredential(credential);
            }
        });
    }

    private void signInWithPhoneCredential (PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.verifingOtpProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                goToSetProfileActivity();
            } else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    binding.verifingOtpProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToSetProfileActivity() {
        Intent intent = new Intent(OtpAuthentication.this, SetProfile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}