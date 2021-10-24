package com.themarto.mychatapp.loginActivity.enterOtp;

import static com.themarto.mychatapp.Constants.LOGIN_FAILED;
import static com.themarto.mychatapp.Constants.VERIFICATION_CODE_EMPTY;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.themarto.mychatapp.utils.SingleLiveEvent;

public class EnterOtpViewModel extends ViewModel {

    private FirebaseAuth firebaseAuth;

    private String verificationId;
    private String phoneNumber;

    private SingleLiveEvent<Integer> showSnackBarMessage = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> showProgressBar = new MutableLiveData<>();
    private SingleLiveEvent<Void> goToSetProfile = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> restartLogin = new SingleLiveEvent<>();

    public EnterOtpViewModel(String verificationId, String phoneNumber) {
        firebaseAuth = FirebaseAuth.getInstance();
        this.verificationId = verificationId;
        this.phoneNumber = phoneNumber;
    }

    public LiveData<Integer> showSnackBarMessage () {
        return showSnackBarMessage;
    }

    public LiveData<Boolean> showProgressBar () {
        return showProgressBar;
    }

    public LiveData<Void> goToSetProfile () {
        return goToSetProfile;
    }

    public LiveData<Void> restartLogin () {
        return restartLogin;
    }

    public String getPhoneNumber () {
        return phoneNumber;
    }

    public void onVerifyOtp(String enterOtp) {
        if (enterOtp.isEmpty()) {
            showSnackBarMessage.setValue(VERIFICATION_CODE_EMPTY);
        }
        else {
            showProgressBar.setValue(true);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enterOtp);
            signInWithPhoneCredential(credential);
        }
    }

    public void onChangePhoneNumber () {
        restartLogin.call();
    }

    private void signInWithPhoneCredential (PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showProgressBar.setValue(true);
                goToSetProfile.call();
            } else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    showProgressBar.setValue(false);
                    showSnackBarMessage.setValue(LOGIN_FAILED);
                }
            }
        });
    }
}
