package com.themarto.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.themarto.mychatapp.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int PHONE_NUMBER_LENGTH = 10;
    private ActivityMainBinding binding;
    String countryCode;
    String phoneNumber;

    FirebaseAuth firebaseAuth;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneVerificationStateCallback;
    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        countryCode = binding.countryCodePicker.getSelectedCountryCodeWithPlus();
        setCountryCodeListener();
        setSendOtpButtonListener();
        setPhoneVerificationStateCallback();
    }

    private void setCountryCodeListener() {
        binding.countryCodePicker.setOnCountryChangeListener(() -> {
            countryCode = binding.countryCodePicker.getSelectedCountryCodeWithPlus();
        });
    }

    private void setSendOtpButtonListener () {
        binding.sendMessageBtn.setOnClickListener(v -> {
            String number = binding.phoneNumber.getText().toString();
            if (number.isEmpty()) {
                Toast.makeText(getApplicationContext(),
                        "Please enter your number",
                        Toast.LENGTH_SHORT).show();
            }
            else if (number.length() < PHONE_NUMBER_LENGTH) {
                Toast.makeText(getApplicationContext(),
                        "Please enter a valid number",
                        Toast.LENGTH_SHORT).show();
            } else {
                binding.sendingOtpProgress.setVisibility(View.VISIBLE);
                phoneNumber = countryCode + number;

                PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(MainActivity.this)
                        .setCallbacks(phoneVerificationStateCallback)
                        .build();

                PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
            }
        });
    }

    private void setPhoneVerificationStateCallback () {
        phoneVerificationStateCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @org.jetbrains.annotations.NotNull PhoneAuthCredential phoneAuthCredential) {
                // automatically fetch the OTP code here
            }

            @Override
            public void onVerificationFailed(@NonNull @org.jetbrains.annotations.NotNull FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(getApplicationContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Snackbar.make(binding.phoneNumber, "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            // The code was successfully sent to the user
            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(), "Code was sent to your phone", Toast.LENGTH_SHORT).show();
                binding.sendingOtpProgress.setVisibility(View.INVISIBLE);
                codeSent = s; // save the code that was sent to the user for verification
                goToOtpAuthActivity(codeSent);
            }
        };
    }

    private void goToOtpAuthActivity(String code) {
        Intent intent = new Intent(MainActivity.this, OtpAuthentication.class);
        intent.putExtra("otp", code);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            goToChatActivity();
        }
    }

    private void goToChatActivity () {
        Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}