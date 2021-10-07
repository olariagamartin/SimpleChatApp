package com.themarto.mychatapp.loginActivity.sendOtp;

import static com.themarto.mychatapp.Constants.EMPTY_NUMBER;
import static com.themarto.mychatapp.Constants.INVALID_NUMBER;
import static com.themarto.mychatapp.Constants.PHONE_NUMBER_LENGTH;
import static com.themarto.mychatapp.Constants.VERIFICATION_FAILED;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.themarto.mychatapp.utils.SingleLiveEvent;

public class SendOtpViewModel extends ViewModel {

    private String countryCode;
    private String phoneNumber;
    private String verificationId;

    private SingleLiveEvent<Integer> showSnackBarMessage = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> showProgressBar = new MutableLiveData<>();
    private SingleLiveEvent<Void> verifyPhoneNumber = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> goToEnterOtp = new SingleLiveEvent<>();

    public LiveData<Integer> showSnackBarMessage () {
        return showSnackBarMessage;
    }

    public LiveData<Boolean> showProgressBar () {
        return showProgressBar;
    }

    public LiveData<Void> verifyPhoneNumber () {
        return verifyPhoneNumber;
    }

    public LiveData<Void> goToEnterOtp () {
        return goToEnterOtp;
    }

    public void setCountryCode (String code) {
        countryCode = code;
    }

    public void setPhoneNumber (String number) {
        phoneNumber = number;
    }

    public void onCodeSent (String verificationId) {
        showProgressBar.setValue(true);
        this.verificationId = verificationId;
        goToEnterOtp.call();
    }

    public String getFullPhoneNumber () {
        return countryCode + phoneNumber;
    }

    public String getVerificationId () {
        return verificationId;
    }

    public void onSendOtpClicked () {
        if (phoneNumber.isEmpty()) {
            showSnackBarMessage.setValue(EMPTY_NUMBER);
        }
        else if (phoneNumber.length() < PHONE_NUMBER_LENGTH){
            showSnackBarMessage.setValue(INVALID_NUMBER);
        }
        else {
            showProgressBar.setValue(true);
            verifyPhoneNumber.call();
        }
    }

    public void verificationFailed () {
        // todo: provide feedback to the user
        showSnackBarMessage.setValue(VERIFICATION_FAILED);
    }
}
