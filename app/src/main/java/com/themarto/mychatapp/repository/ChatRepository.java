package com.themarto.mychatapp.repository;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.Query;
import com.themarto.mychatapp.data.domain.MessageModel;
import com.themarto.mychatapp.data.network.MessageDTO;

import java.util.List;

public class ChatRepository {

    public LiveData<List<MessageModel>> getMessages (String roomId) {
        // TODO
        return null;
    }

    public void updateLocalDB (LiveData<List<MessageDTO>> messageListUpdated) {
        // TODO
    }

    public Query getMessagesFromNetwork (String roomId) {
        // TODO
        return null;
    }

    public Query sendMessage (MessageModel message) {
        // TODO
        return null;
    }
}
