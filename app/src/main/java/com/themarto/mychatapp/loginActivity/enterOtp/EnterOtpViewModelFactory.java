package com.themarto.mychatapp.loginActivity.enterOtp;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.themarto.mychatapp.loginActivity.enterOtp.EnterOtpViewModel;

public class EnterOtpViewModelFactory implements ViewModelProvider.Factory {

    private String verificationId;
    private String phoneNumber;

    public EnterOtpViewModelFactory(String verificationId, String phoneNumber) {
        this.verificationId = verificationId;
        this.phoneNumber = phoneNumber;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EnterOtpViewModel.class)) {
            return (T) new EnterOtpViewModel(verificationId, phoneNumber);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
