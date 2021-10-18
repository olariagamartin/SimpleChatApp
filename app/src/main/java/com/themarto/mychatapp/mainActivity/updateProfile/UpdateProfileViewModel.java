package com.themarto.mychatapp.mainActivity.updateProfile;

import static com.themarto.mychatapp.utils.Utils.compressImage;

import android.app.Application;
import android.net.Uri;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.themarto.mychatapp.UserModel;
import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.repository.ContactRepository;
import com.themarto.mychatapp.utils.SingleLiveEvent;

public class UpdateProfileViewModel extends AndroidViewModel {

    private ContactRepository contactRepository;

    private LiveData<ContactModel> user;

    private SingleLiveEvent<Void> showDialogChangeUsername = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> launchImagePicker = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> showProgressBar = new MutableLiveData<>();

    public UpdateProfileViewModel(Application application) {
        super(application);
        contactRepository = ContactRepository.getInstance(application);

        user = contactRepository.getUser();
    }

    public LiveData<ContactModel> getUser () {
        return user;
    }

    public void onEditUserNameClicked () {
        showDialogChangeUsername.call();
    }

    public void onEditProfileImageClicked () {
        launchImagePicker.call();
    }

    public void onSaveUsernameClicked (String nUsername) {
        updateUsername(nUsername);
    }

    public LiveData<Boolean> showProgressBar () {
        return showProgressBar;
    }

    public LiveData<Void> showDialogChangeUsername () {
        return showDialogChangeUsername;
    }

    public LiveData<Void> launchImagePicker () {
        return launchImagePicker;
    }

    private void updateUsername(String nUsername) {
        contactRepository.updateUsername(nUsername);
    }

    public void onProfileImagePicked (Uri imagePath) {
        showProgressBar.setValue(true);
        byte[] data  = compressImage(getApplication(), imagePath);
        contactRepository.updateProfileImage(data)
                .addOnSuccessListener(taskSnapshot -> {
                    showProgressBar.setValue(false);
                })
                .addOnFailureListener(e -> {
                    // todo: launch error
                    showProgressBar.setValue(false);
                });
    }
}
