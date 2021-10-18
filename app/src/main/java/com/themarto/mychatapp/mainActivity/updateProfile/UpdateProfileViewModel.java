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

    /*public void onSaveUsernameClicked (String nUsername) {
        updateUsername(nUsername);
    }*/

    public LiveData<Boolean> showProgressBar () {
        return showProgressBar;
    }

    public LiveData<Void> showDialogChangeUsername () {
        return showDialogChangeUsername;
    }

    public LiveData<Void> launchImagePicker () {
        return launchImagePicker;
    }

    /*private void updateUsername(String nUsername) {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());

        documentReference.update("name", nUsername)
                .addOnSuccessListener(unused -> {
                    this.username.setValue(nUsername);
                });
    }*/

    /*public void onProfileImagePicked (Uri imagePath) {
        StorageReference imageRef = storageReference
                .child("images")
                .child(firebaseAuth.getUid())
                .child("profile image");

        byte[] data  = compressImage(getApplication(), imagePath);
        UploadTask uploadTask = imageRef.putBytes(data);

        showProgressBar.setValue(true);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUriAccessToken = uri.toString();
                updateProfileImageLink(imageUriAccessToken);
            });
            showProgressBar.setValue(false);
        });
    }*/

    /*private void updateProfileImageLink(String link) {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());

        documentReference.update("image", link)
                .addOnSuccessListener(unused -> {
                    imageLink.setValue(link);
                });
    }*/
}
