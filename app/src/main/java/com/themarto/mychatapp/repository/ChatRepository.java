package com.themarto.mychatapp.repository;

import static com.themarto.mychatapp.Constants.RealtimeDatabasePaths.MESSAGES;
import static com.themarto.mychatapp.Constants.RealtimeDatabasePaths.SIMPLE_CHAT_ROOMS;
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
import com.themarto.mychatapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private MessageDao messageDao;
    private LiveData<List<MessageModel>> messageList;
    private String receiverId;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference chatReference;
    private String chatRoomId;

    private ChatRepository (Application application, String receiverId) {
        messageDao = ChatAppDatabase.getDatabase(application).messageDao();
        this.receiverId = receiverId;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        chatRoomId = Utils.getChatRoomId(firebaseAuth.getUid(), receiverId);
        chatReference = firebaseDatabase.getReference().child(SIMPLE_CHAT_ROOMS)
                .child(chatRoomId).child(MESSAGES);

        listenForDBChanges();
    }

    public static ChatRepository getInstance (Application application, String receiverId) {
        return new ChatRepository(application, receiverId);
    }

    public String getSenderUid () {
        return firebaseAuth.getUid();
    }

    public String getChatRoomId () {
        return chatRoomId;
    }

    public LiveData<List<MessageModel>> getMessages () {
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
        LiveData<List<MessageEntity>> messages = messageDao.getMessages(chatRoomId);
        messageList = Transformations.map(messages, messageEntities -> {
            List<MessageModel> messageModels = new ArrayList<>();
            for (MessageEntity messageEntity : messageEntities) {
                messageModels.add(toMessageModel(messageEntity));
            }
            return messageModels;
        });
    }
}
