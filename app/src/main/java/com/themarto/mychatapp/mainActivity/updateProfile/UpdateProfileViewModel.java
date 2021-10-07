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
import com.themarto.mychatapp.utils.SingleLiveEvent;

public class UpdateProfileViewModel extends AndroidViewModel {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private MutableLiveData<String>  username = new MutableLiveData<>();
    private MutableLiveData<String>  imageLink = new MutableLiveData<>();
    private SingleLiveEvent<Void> showDialogChangeUsername = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> launchImagePicker = new SingleLiveEvent<>();
    private MutableLiveData<Boolean> showProgressBar = new MutableLiveData<>();

    public UpdateProfileViewModel(Application application) {
        super(application);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        fetchUserData();
    }

    private void fetchUserData () {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            // todo: check for null object
            UserModel model = documentSnapshot.toObject(UserModel.class);
            username.setValue(model.getName());
            imageLink.setValue(model.getImage());
        }).addOnFailureListener(e -> {
            // notify error
        });
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

    public LiveData<String> getUsername () {
        return username;
    }

    public LiveData<String> getImageLink () {
        return imageLink;
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
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());

        documentReference.update("name", nUsername)
                .addOnSuccessListener(unused -> {
                    this.username.setValue(nUsername);
                });
    }

    public void onProfileImagePicked (Uri imagePath) {
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
    }

    private void updateProfileImageLink(String link) {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());

        documentReference.update("image", link)
                .addOnSuccessListener(unused -> {
                    imageLink.setValue(link);
                });
    }
}
