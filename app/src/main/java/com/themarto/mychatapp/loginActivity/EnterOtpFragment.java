package com.themarto.mychatapp.loginActivity;

import static com.themarto.mychatapp.Constants.LOGIN_FAILED;
import static com.themarto.mychatapp.Constants.VERIFICATION_CODE_EMPTY;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.themarto.mychatapp.databinding.FragmentEnterOtpBinding;

public class EnterOtpFragment extends Fragment {

    private EnterOtpViewModel viewModel;
    private FragmentEnterOtpBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEnterOtpBinding.inflate(inflater, container, false);

        String verificationId = EnterOtpFragmentArgs.fromBundle(getArguments()).getVerificationId();
        EnterOtpViewModelFactory factory = new EnterOtpViewModelFactory(verificationId);

        viewModel = new ViewModelProvider(this,  factory).get(EnterOtpViewModel.class);

        // setChangeNumberTextListener(); todo: handle change number action
        setVerifyOtpButtonListener();

        setupObservers();

        return binding.getRoot();
    }

    // todo
    private void setChangeNumberTextListener() {
        binding.changeNumber.setOnClickListener(v -> {
            // todo: test backstack
            Navigation.findNavController(binding.getRoot()).navigateUp();
            /*Intent intent = new Intent(OtpAuthentication.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*/
        });
    }

    private void setVerifyOtpButtonListener() {
        binding.verifyOtpBtn.setOnClickListener(v -> {
            String enterOtp = binding.otpEditText.getText().toString();
            viewModel.onVerifyOtp(enterOtp);
        });
    }

    private void setupObservers () {
        viewModel.showProgressBar().observe(getViewLifecycleOwner(), this::showProgressBar);

        viewModel.showSnackBarMessage().observe(getViewLifecycleOwner(), this::showSnackBarMessage);

        viewModel.goToSetProfile().observe(getViewLifecycleOwner(), unused -> goToSetProfileFragment());
    }

    private void showProgressBar (boolean show) {
        if (show) binding.verifingOtpProgress.setVisibility(View.VISIBLE);
        else binding.verifingOtpProgress.setVisibility(View.INVISIBLE);
    }

    private void showSnackBarMessage (int messageCode) {
        switch (messageCode) {
            case VERIFICATION_CODE_EMPTY:
                Snackbar.make(binding.getRoot(), "Please enter the code you received", Snackbar.LENGTH_SHORT).show();
                break;
            case LOGIN_FAILED:
                Snackbar.make(binding.getRoot(), "Login failed", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private void goToSetProfileFragment() {
        NavController navController = Navigation.findNavController(binding.getRoot());
        navController
                .navigate(EnterOtpFragmentDirections.actionEnterOtpFragmentToSetProfileFragment());
    }
}