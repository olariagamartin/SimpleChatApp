package com.themarto.mychatapp.loginActivity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EnterOtpViewModelFactory implements ViewModelProvider.Factory {

    private String verificationId;

    public EnterOtpViewModelFactory(String verificationId) {
        this.verificationId = verificationId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EnterOtpViewModel.class)) {
            return (T) new EnterOtpViewModel(verificationId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
