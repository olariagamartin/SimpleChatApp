package com.themarto.mychatapp.loginActivity.setProfile;

import static com.themarto.mychatapp.Constants.PROFILE_IMAGE_NOT_SET;
import static com.themarto.mychatapp.Constants.USERNAME_EMPTY;
import static com.themarto.mychatapp.utils.Utils.compressImage;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.themarto.mychatapp.utils.SingleLiveEvent;

import java.util.HashMap;
import java.util.Map;

public class SetProfileViewModel extends AndroidViewModel {

    private Uri imagePath;

    private FirebaseAuth firebaseAuth;
    private String username;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String imageUriAccessToken;

    private FirebaseFirestore firebaseFirestore;

    private SingleLiveEvent<Void> launchPhotoPicker = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> loadProfileImage = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> showSnackBarMessage = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> showProgressBar = new MutableLiveData<>();
    private SingleLiveEvent<Void> goToMainActivity = new SingleLiveEvent<>();

    public SetProfileViewModel(@NonNull Application application) {
        super(application);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public Uri getImagePath () {
        return imagePath;
    }

    public LiveData<Void> loadProfileImage () {
        return loadProfileImage;
    }

    public LiveData<Integer> showSnackBarMessage () {
        return showSnackBarMessage;
    }

    public LiveData<Boolean> showProgressBar () {
        return showProgressBar;
    }

    public LiveData<Void> goToMainActivity () {
        return goToMainActivity;
    }

    public void onProfilePhotoClicked () {
        launchPhotoPicker.call();
    }

    public LiveData<Void> launchPhotoPicker () {
        return launchPhotoPicker;
    }

    public void onPhotoPickActionDone (Uri data) {
        imagePath = data;
        loadProfileImage.call();
    }

    public void onSaveProfileClicked (String username) {
        if (username.isEmpty()) {
            showSnackBarMessage.setValue(USERNAME_EMPTY);
        } else if (imagePath == null) {
            showSnackBarMessage.setValue(PROFILE_IMAGE_NOT_SET);
        } else {
            showProgressBar.setValue(true);
            this.username = username;
            saveUserData();
        }
    }

    // todo: move to repository
    // todo: handle no network connection
    private void saveUserData() {
        StorageReference imageRef = storageReference
                .child("images")
                .child(firebaseAuth.getUid())
                .child("profile image");

        byte[] data = compressImage(getApplication().getApplicationContext(), imagePath);
        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                imageUriAccessToken = uri.toString();
                sendDataToFirestore();
            });
        });
    }

    private void sendDataToFirestore () {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());

        documentReference.set(getUserData()).addOnSuccessListener(unused -> {
            goToMainActivity.call();
        });
    }

    private Map<String, Object> getUserData () {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", username);
        userData.put("image", imageUriAccessToken);
        userData.put("uid", firebaseAuth.getUid());
        userData.put("status", "Online");
        return userData;
    }
}
