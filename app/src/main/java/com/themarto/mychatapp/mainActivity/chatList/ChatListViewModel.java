package com.themarto.mychatapp.mainActivity.chatList;

import static com.themarto.mychatapp.utils.NetworkConnection.isConnected;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.data.network.ContactDTO;
import com.themarto.mychatapp.repository.ContactRepository;
import com.themarto.mychatapp.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class ChatListViewModel extends AndroidViewModel {

    private ContactRepository repository;

    private LiveData<List<ContactModel>> contactList;

    private SingleLiveEvent<Void> goToUpdateProfile = new SingleLiveEvent<>();

    private SingleLiveEvent<Void> goToLoginActivity = new SingleLiveEvent<>();

    private SingleLiveEvent<Void> showLogoutAlertDialog = new SingleLiveEvent<>();

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    public ChatListViewModel(@NonNull Application application) {
        super(application);
        repository = ContactRepository.getInstance(application);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        loadContacts();
        listenForNetworkUpdates();
        manageStatus();
    }

    private void loadContacts () {
        contactList = repository.getAllContacts();
    }

    private void listenForNetworkUpdates() {
        // TODO: detach listener
        repository.getAllContactsFromNetwork()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ContactDTO> contactDTOList = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            ContactDTO contactDTO = postSnapshot.getValue(ContactDTO.class);
                            contactDTOList.add(contactDTO);
                        }
                        repository.updateLocalDB(contactDTOList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public LiveData<List<ContactModel>> getContactList () {
        return contactList;
    }

    public void onProfileItemClicked () {
        goToUpdateProfile.call();
    }

    public LiveData<Void> goToUpdateProfile () {
        return goToUpdateProfile;
    }

    public void onLogoutItemClicked () {
        showLogoutAlertDialog.call();
    }

    public void logout () {
        firebaseAuth.signOut();
        goToLoginActivity.call();
    }

    public LiveData<Void> goToLoginActivity () {
        return goToLoginActivity;
    }

    public LiveData<Void> showLogoutAlertDialog () { return showLogoutAlertDialog; }

    public boolean isConnectedToNetwork () {
        return isConnected(getApplication());
    }

    private void manageStatus() {
        // todo: move to repository
        String userUid = firebaseAuth.getUid();
        DatabaseReference userStatusDatabaseRef = firebaseDatabase.getReference()
                .child("users")
                .child(userUid)
                .child("online");

        firebaseDatabase.getReference(".info/connected")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean connected = snapshot.getValue(Boolean.class);
                        if (connected == false) {
                            return;
                        }
                        userStatusDatabaseRef.onDisconnect().setValue(false);
                        userStatusDatabaseRef.setValue(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
