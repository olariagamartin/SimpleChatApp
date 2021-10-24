package com.themarto.mychatapp.repository;

import static com.themarto.mychatapp.Constants.ONE_MEGABYTE;
import static com.themarto.mychatapp.Constants.RealtimeDatabasePaths.PROFILE_IMAGE_LINK;
import static com.themarto.mychatapp.Constants.RealtimeDatabasePaths.USERNAME;
import static com.themarto.mychatapp.Constants.RealtimeDatabasePaths.USERS;
import static com.themarto.mychatapp.Constants.StoragePaths.IMAGES;
import static com.themarto.mychatapp.Constants.StoragePaths.PROFILE_IMAGE;
import static com.themarto.mychatapp.data.database.Converters.toContactEntity;
import static com.themarto.mychatapp.data.database.Converters.toContactModel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.themarto.mychatapp.data.database.ChatAppDatabase;
import com.themarto.mychatapp.data.database.ContactDao;
import com.themarto.mychatapp.data.database.ContactEntity;
import com.themarto.mychatapp.data.database.Converters;
import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.data.network.ContactDTO;

import java.util.ArrayList;
import java.util.List;

public class ContactRepository {

    private ContactDao contactDao;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference contactsRef;
    private String userId;

    private ContactRepository(Application application) {
        contactDao = ChatAppDatabase.getDatabase(application).contactDao();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        contactsRef = firebaseDatabase.getReference().child(USERS);
        userId = firebaseAuth.getUid();
    }

    public static ContactRepository getInstance(Application application) {
        return new ContactRepository(application);
    }

    // USER CONTACTS
    public LiveData<List<ContactModel>> getAllContacts() {
        return Transformations.map(contactDao.getContactList(), contactEntities -> {
            List<ContactModel> contactModels = new ArrayList<>();
            for (ContactEntity contactEntity : contactEntities) {
                if (!contactEntity.id.equals(userId)) {
                    contactModels.add(toContactModel(contactEntity));
                }
            }
            return contactModels;
        });
    }

    public LiveData<ContactModel> getContact(String id) {
        return Transformations.map(contactDao.getContact(id), Converters::toContactModel);
    }

    public DatabaseReference getAllContactsFromNetwork() {
        return contactsRef;
    }

    public void updateLocalDB(List<ContactDTO> contactListUpdated) {
        List<ContactEntity> contactEntities = new ArrayList<>();
        List<Task<byte[]>> taskList = new ArrayList<>();
        for (ContactDTO contactDTO : contactListUpdated) {
            Task<byte[]> task = getProfileImageFromNetwork(contactDTO.getProfileImageLink());
            task.addOnSuccessListener(bytes -> {
                contactEntities.add(toContactEntity(contactDTO, bytes));
            });
            taskList.add(task);
        }
        Tasks.whenAllComplete(taskList).addOnCompleteListener(task -> {
            ChatAppDatabase.databaseWriteExecutor.execute(() -> {
                contactDao.insertContacts(contactEntities);
            });
        });
    }

    private Task<byte[]> getProfileImageFromNetwork(String downloadUrl) {
        return firebaseStorage.getReferenceFromUrl(downloadUrl).getBytes(ONE_MEGABYTE);
    }

    // CURRENT USER
    public List<Task<?>> setUserProfile (String username, byte[] profileImage) {
        List<Task<?>> tasks = new ArrayList<>();
        Task<Void> setUsername = updateUsername(username);
        tasks.add(setUsername);
        UploadTask setProfileImage = updateProfileImage(profileImage);
        tasks.add(setProfileImage);
        return tasks;
    }

    public LiveData<ContactModel> getUser() {
        return Transformations.map(contactDao.getContact(userId), Converters::toContactModel);
    }

    public Task<Void> updateUsername(String username) {
        return contactsRef.child(userId).child(USERNAME).setValue(username);
    }

    public UploadTask updateProfileImage(byte[] imageData) {
        StorageReference profileImageRef = firebaseStorage.getReference()
                .child(IMAGES)
                .child(userId)
                .child(PROFILE_IMAGE);

        UploadTask uploadTask = profileImageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUriAccessToken = uri.toString();
                updateProfileImageLink(imageUriAccessToken);
            });
        });
        return uploadTask;
    }

    private void updateProfileImageLink (String imageUri) {
        contactsRef.child(userId).child(PROFILE_IMAGE_LINK).setValue(imageUri);
    }
}
