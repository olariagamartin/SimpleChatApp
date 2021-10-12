package com.themarto.mychatapp.mainActivity.chat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.themarto.mychatapp.UserModel;
import com.themarto.mychatapp.data.domain.MessageModel;
import com.themarto.mychatapp.data.network.MessageDTO;
import com.themarto.mychatapp.repository.ChatRepository;
import com.themarto.mychatapp.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private ChatRepository repository;
    private String receiverUid;
    private String senderUid;
    // TODO: change
    private MutableLiveData<UserModel> receiver = new MutableLiveData<>();

    private SingleLiveEvent<Void> clearMessageField = new SingleLiveEvent<>();
    private LiveData<List<MessageModel>> messageList = new MutableLiveData<>();

    public ChatViewModel(Application application, String receiverUid) {
        super(application);

        this.receiverUid = receiverUid;

        repository = ChatRepository.getInstance(application, receiverUid);

        this.senderUid = repository.getSenderUid();

        //loadReceiver();
        listenForNetworkChanges();
        loadMessages();
    }

    /*private void loadReceiver () {
        firebaseFirestore.collection("users")
                .document(receiverUid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    receiver.setValue(value.toObject(UserModel.class));
                }
            }
        });
    }*/

    private void listenForNetworkChanges() {
        // TODO: detach listener
        repository.getMessagesFromNetwork().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MessageDTO> messages = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageDTO messageDTO = snapshot1.getValue(MessageDTO.class);
                    messages.add(messageDTO);
                }
                repository.updateLocalDB(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMessages () {
        messageList = repository.getMessages();
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

    private void sendMessage (String message) {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        MessageModel messageModel = new MessageModel(message, senderUid, receiverUid,
                repository.getSenderUid(), currentTime);
        repository.sendMessage(messageModel);
    }

}
