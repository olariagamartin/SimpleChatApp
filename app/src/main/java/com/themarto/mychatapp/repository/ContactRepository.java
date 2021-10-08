package com.themarto.mychatapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.themarto.mychatapp.data.database.ChatAppDatabase;
import com.themarto.mychatapp.data.database.ContactDao;
import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.data.network.ContactDTO;

import java.util.List;

public class ContactRepository {

    private ContactDao contactDao;
    private LiveData<List<ContactModel>> contactList;

    private ContactRepository(Application application) {
        contactDao = ChatAppDatabase.getDatabase(application).contactDao();
    }

    static ContactRepository getInstance (Application application) {
        return new ContactRepository(application);
    }

    public LiveData<List<ContactModel>> getAllContacts () {
        // TODO
        return null;
    }

    public Task<QuerySnapshot> getAllContactsFromNetwork () {
        // TODO
        return null;
    }

    public void updateLocalDB (List<ContactDTO> contactListUpdated) {
        // TODO
    }

}
