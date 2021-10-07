package com.themarto.mychatapp.mainActivity.chatList;

import androidx.lifecycle.ViewModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatListViewModel extends ViewModel {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public ChatListViewModel() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }



    public Query getUsersQuery () {
        Query query = firebaseFirestore.collection("users")
                .whereNotEqualTo("uid", firebaseAuth.getUid());
        return query;
    }
}
