package com.themarto.mychatapp.mainActivity.chatList;

import static com.themarto.mychatapp.utils.NetworkConnection.isConnected;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.data.network.ContactDTO;
import com.themarto.mychatapp.repository.ContactRepository;

import java.util.ArrayList;
import java.util.List;

public class ChatListViewModel extends AndroidViewModel {

    private ContactRepository repository;

    public LiveData<List<ContactModel>> contactList;

    public ChatListViewModel(@NonNull Application application) {
        super(application);
        repository = ContactRepository.getInstance(application);
        loadContacts();
        listenForNetworkUpdates();
        manageStatus();
    }

    private void loadContacts () {
        contactList = repository.getAllContacts();
    }

    private void listenForNetworkUpdates() {
        // TODO: detach listener
        repository.getAllContactsFromNetwork().addSnapshotListener((value, error) -> {
            if (error == null) {
                List<ContactDTO> contactDTOList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    ContactDTO contactDTO = doc.toObject(ContactDTO.class);
                    contactDTOList.add(contactDTO);
                }
                repository.updateLocalDB(contactDTOList);
            }
        });
    }

    public LiveData<List<ContactModel>> getContactList () {
        return contactList;
    }

    public boolean isConnectedToNetwork () {
        return isConnected(getApplication());
    }

    private void manageStatus() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userUid = firebaseAuth.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userStatusDatabaseRef = firebaseDatabase.getReference()
                .child("users")
                .child(userUid)
                .child("status");

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
