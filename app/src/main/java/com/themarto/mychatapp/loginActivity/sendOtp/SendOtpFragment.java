package com.themarto.mychatapp.loginActivity.sendOtp;

import static com.themarto.mychatapp.Constants.EMPTY_NUMBER;
import static com.themarto.mychatapp.Constants.INVALID_NUMBER;
import static com.themarto.mychatapp.Constants.VERIFICATION_FAILED;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.themarto.mychatapp.R;
import com.themarto.mychatapp.databinding.FragmentSendOtpBinding;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SendOtpFragment extends Fragment {

    private FragmentSendOtpBinding binding;
    private SendOtpViewModel viewModel;

    FirebaseAuth firebaseAuth;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneVerificationStateCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(SendOtpViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSendOtpBinding.inflate(inflater, container, false);

        saveCountryCode();
        setCountryCodeListener();
        setSendOtpButtonListener();

        setPhoneVerificationStateCallback();

        setupObservers();

        return binding.getRoot();
    }

    private void setCountryCodeListener() {
        binding.countryCodePicker.setOnCountryChangeListener(this::saveCountryCode);
    }

    private void saveCountryCode () {
        String code = binding.countryCodePicker.getSelectedCountryCodeWithPlus();
        viewModel.setCountryCode(code);
    }

    private void setSendOtpButtonListener () {
        binding.sendMessageBtn.setOnClickListener(v -> {
            String number = binding.phoneNumber.getText().toString();
            viewModel.setPhoneNumber(number);
            viewModel.onSendOtpClicked();
        });
    }

    private void setupObservers () {
        viewModel.showSnackBarMessage().observe(getViewLifecycleOwner(), this::showSnackBarMessage);

        viewModel.showProgressBar().observe(getViewLifecycleOwner(), this::showProgressBar);

        viewModel.verifyPhoneNumber().observe(getViewLifecycleOwner(), unused -> verifyPhoneNumber());

        viewModel.goToEnterOtp().observe(getViewLifecycleOwner(), unused -> {
            goToEnterOtpFragment(viewModel.getVerificationId());
        });
    }

    private void showSnackBarMessage(int messageCode) {
        switch (messageCode) {
            case EMPTY_NUMBER:
                Snackbar.make(binding.getRoot(), R.string.enter_phone_number_message, Snackbar.LENGTH_SHORT).show();
                break;
            case INVALID_NUMBER:
                Snackbar.make(binding.getRoot(), R.string.invalid_phone_number_message, Snackbar.LENGTH_SHORT).show();
                break;
            case VERIFICATION_FAILED:
                Snackbar.make(binding.getRoot(), R.string.verification_failed_message, Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private void showProgressBar (boolean show) {
        if (show) binding.sendingOtpProgress.setVisibility(View.VISIBLE);
        else binding.sendingOtpProgress.setVisibility(View.INVISIBLE);
    }

    private void verifyPhoneNumber () {
        viewModel.showProgressBar();
        String phoneNumber = viewModel.getFullPhoneNumber();

        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(phoneVerificationStateCallback)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    private void setPhoneVerificationStateCallback () {
        phoneVerificationStateCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @org.jetbrains.annotations.NotNull PhoneAuthCredential phoneAuthCredential) {
                // automatically fetch the OTP code here
            }

            @Override
            public void onVerificationFailed(@NonNull @org.jetbrains.annotations.NotNull FirebaseException e) {
                viewModel.verificationFailed();
            }

            // The code was successfully sent to the user
            @Override
            public void onCodeSent(@NonNull @NotNull String verificationId, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                viewModel.onCodeSent(verificationId);
            }
        };
    }

    private void goToEnterOtpFragment(String code) {
        SendOtpFragmentDirections.ActionSendOtpFragmentToEnterOtpFragment action =
                SendOtpFragmentDirections.actionSendOtpFragmentToEnterOtpFragment(code);
        Navigation.findNavController(binding.getRoot()).navigate(action);
    }
}