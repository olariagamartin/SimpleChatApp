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

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.themarto.mychatapp.repository.ContactRepository;
import com.themarto.mychatapp.utils.SingleLiveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetProfileViewModel extends AndroidViewModel {

    private ContactRepository contactRepository;

    private Uri imagePath;

    private String username;

    private SingleLiveEvent<Void> launchPhotoPicker = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> loadProfileImage = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> showSnackBarMessage = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> showProgressBar = new MutableLiveData<>();
    private SingleLiveEvent<Void> goToMainActivity = new SingleLiveEvent<>();

    public SetProfileViewModel(@NonNull Application application) {
        super(application);

        contactRepository = ContactRepository.getInstance(getApplication());
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

    private void saveUserData() {
        byte[] data = compressImage(getApplication().getApplicationContext(), imagePath);
        List<Task<?>> tasks = contactRepository.setUserProfile(username, data);
        Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
            goToMainActivity.call();
        });
    }
}
