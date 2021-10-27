package com.themarto.mychatapp.mainActivity.chat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.data.domain.MessageModel;
import com.themarto.mychatapp.data.network.MessageDTO;
import com.themarto.mychatapp.repository.ChatRepository;
import com.themarto.mychatapp.repository.ContactRepository;
import com.themarto.mychatapp.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private ChatRepository chatRepository;
    private ContactRepository contactRepository;
    private String receiverUid;
    private String senderUid;
    private LiveData<ContactModel> receiver;

    private SingleLiveEvent<Void> clearMessageField = new SingleLiveEvent<>();
    private LiveData<List<MessageModel>> messageList = new MutableLiveData<>();

    public ChatViewModel(Application application, String receiverUid) {
        super(application);

        this.receiverUid = receiverUid;

        chatRepository = ChatRepository.getInstance(application, receiverUid);
        contactRepository = ContactRepository.getInstance(application);

        this.senderUid = chatRepository.getSenderUid();
        receiver = contactRepository.getContact(receiverUid);

        listenForNetworkChanges();
        loadMessages();
    }

    private void listenForNetworkChanges() {
        chatRepository.getMessagesFromNetwork().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MessageDTO> messages = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageDTO messageDTO = snapshot1.getValue(MessageDTO.class);
                    messages.add(messageDTO);
                }
                chatRepository.updateLocalDB(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMessages () {
        messageList = chatRepository.getMessages();
    }

    public LiveData<Void> clearMessageField () {
        return clearMessageField;
    }

    public String getSenderUid () {
        return senderUid;
    }

    public void onSendMessageClicked (String message) {
        if (!message.isEmpty()) {
            sendMessage(message);
        }
        clearMessageField.call();
    }

    public LiveData<List<MessageModel>> getMessageList () {
        return messageList;
    }

    public LiveData<ContactModel> getReceiver () {
        return receiver;
    }

    private void sendMessage (String message) {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        MessageModel messageModel = new MessageModel(message, senderUid, receiverUid,
                chatRepository.getSenderUid(), currentTime);
        chatRepository.sendMessage(messageModel);
    }

}
