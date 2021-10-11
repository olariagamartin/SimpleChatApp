package com.themarto.mychatapp.repository;

import static com.themarto.mychatapp.data.database.Converters.toMessageDTO;
import static com.themarto.mychatapp.data.database.Converters.toMessageEntity;
import static com.themarto.mychatapp.data.database.Converters.toMessageModel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.themarto.mychatapp.data.database.ChatAppDatabase;
import com.themarto.mychatapp.data.database.MessageDao;
import com.themarto.mychatapp.data.database.MessageEntity;
import com.themarto.mychatapp.data.domain.MessageModel;
import com.themarto.mychatapp.data.network.MessageDTO;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private MessageDao messageDao;
    private LiveData<List<MessageModel>> messageList;
    private String chatRoomId;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference chatReference;

    private ChatRepository (Application application, String chatRoomId) {
        messageDao = ChatAppDatabase.getDatabase(application).messageDao();
        this.chatRoomId = chatRoomId;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        chatReference = firebaseDatabase.getReference().child("simpleChatRooms")
                .child(chatRoomId).child("messages");
        listenForDBChanges();
    }

    public static ChatRepository getInstance (Application application, String roomId) {
        return new ChatRepository(application, roomId);
    }

    public LiveData<List<MessageModel>> getMessages (String roomId) {
        return messageList;
    }

    public void updateLocalDB (List<MessageDTO> messageListUpdated) {
        List<MessageEntity> messageEntities = new ArrayList<>();
        for (MessageDTO messageDTO : messageListUpdated) {
            messageEntities.add(toMessageEntity(messageDTO));
        }
        ChatAppDatabase.databaseWriteExecutor.execute(() -> {
            messageDao.insertMessages(messageEntities);
        });
    }

    public DatabaseReference getMessagesFromNetwork () {
        return chatReference;
    }

    public Task<Void> sendMessage (MessageModel message) {
        String messageRef = chatReference.push().getKey();
        MessageDTO messageToSend = toMessageDTO(message, messageRef);
        return chatReference.child(messageRef).setValue(messageToSend);
    }

    private void listenForDBChanges () {
        messageList = Transformations.map(messageDao.getMessages(chatRoomId), messageEntities -> {
            List<MessageModel> messageModels = new ArrayList<>();
            for (MessageEntity messageEntity : messageEntities) {
                messageModels.add(toMessageModel(messageEntity));
            }
            return messageModels;
        });
    }
}
