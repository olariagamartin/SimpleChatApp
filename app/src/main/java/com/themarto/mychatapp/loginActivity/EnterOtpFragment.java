package com.themarto.mychatapp.loginActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.themarto.mychatapp.databinding.FragmentEnterOtpBinding;

public class EnterOtpFragment extends Fragment {

    private FragmentEnterOtpBinding binding;
    String enterOtp;

    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEnterOtpBinding.inflate(inflater, container, false);

        setChangeNumberTextListener();
        setVerifyOtpButtonListener();

        return binding.getRoot();
    }

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
            enterOtp = binding.otpEditText.getText().toString();
            if (enterOtp.isEmpty()) {
                Toast.makeText(requireContext(), "Introduce the code you received", Toast.LENGTH_SHORT).show();
            } else {
                binding.verifingOtpProgress.setVisibility(View.VISIBLE);
                // todo: test
                String codeReceived = EnterOtpFragmentArgs.fromBundle(getArguments()).getOtp();
                //String codeReceived = "otp"; // getIntent().getStringExtra("otp");
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeReceived, enterOtp);
                signInWithPhoneCredential(credential);
            }
        });
    }

    private void signInWithPhoneCredential (PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.verifingOtpProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();
                goToSetProfileFragment();
            } else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    binding.verifingOtpProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToSetProfileFragment() {
        // todo: test backstack
        NavController navController = Navigation.findNavController(binding.getRoot());
        navController
                .navigate(EnterOtpFragmentDirections.actionEnterOtpFragmentToSetProfileFragment());
        /*Intent intent = new Intent(OtpAuthentication.this, SetProfile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);*/
    }
}