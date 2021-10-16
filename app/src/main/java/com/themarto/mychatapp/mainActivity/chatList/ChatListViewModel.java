package com.themarto.mychatapp.mainActivity.chatList;

import static com.themarto.mychatapp.utils.NetworkConnection.isConnected;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
}
