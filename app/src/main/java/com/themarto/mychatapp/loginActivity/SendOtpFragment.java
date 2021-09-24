package com.themarto.mychatapp.loginActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.themarto.mychatapp.databinding.FragmentSendOtpBinding;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SendOtpFragment extends Fragment {

    private static final int PHONE_NUMBER_LENGTH = 10;
    private FragmentSendOtpBinding binding;
    String countryCode;
    String phoneNumber;

    FirebaseAuth firebaseAuth;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneVerificationStateCallback;
    String codeSent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSendOtpBinding.inflate(inflater, container, false);

        countryCode = binding.countryCodePicker.getSelectedCountryCodeWithPlus();
        setCountryCodeListener();
        setSendOtpButtonListener();
        setPhoneVerificationStateCallback();

        return binding.getRoot();
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
                Toast.makeText(requireContext(),
                        "Please enter your number",
                        Toast.LENGTH_SHORT).show();
            }
            else if (number.length() < PHONE_NUMBER_LENGTH) {
                Toast.makeText(requireContext(),
                        "Please enter a valid number",
                        Toast.LENGTH_SHORT).show();
            } else {
                binding.sendingOtpProgress.setVisibility(View.VISIBLE);
                phoneNumber = countryCode + number;

                PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(requireActivity()) //todo: test, MainActivity.this changed to requireActivity
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
                    Toast.makeText(requireContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Snackbar.make(binding.phoneNumber, "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

                    e.printStackTrace();
                }
            }

            // The code was successfully sent to the user
            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(requireContext(), "Code was sent to your phone", Toast.LENGTH_SHORT).show();
                binding.sendingOtpProgress.setVisibility(View.INVISIBLE);
                codeSent = s; // save the code that was sent to the user for verification
                goToEnterOtpFragment(codeSent);
            }
        };
    }

    private void goToEnterOtpFragment(String code) {
        SendOtpFragmentDirections.ActionSendOtpFragmentToEnterOtpFragment action =
                SendOtpFragmentDirections.actionSendOtpFragmentToEnterOtpFragment(code);
        Navigation.findNavController(binding.getRoot()).navigate(action);
    }
}