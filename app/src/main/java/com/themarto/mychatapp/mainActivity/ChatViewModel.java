package com.themarto.mychatapp.mainActivity;

import static com.themarto.mychatapp.utils.Utils.getChatRoomId;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.themarto.mychatapp.MessageModel;
import com.themarto.mychatapp.UserModel;
import com.themarto.mychatapp.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// todo: rename to MessageAdapter
public class ChatViewModel extends ViewModel {

    private String receiverUid;
    private MutableLiveData<UserModel> receiver = new MutableLiveData<>();

    private String senderUid;

    private String chatRoomId;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference simpleChatReference;
    private FirebaseFirestore firebaseFirestore;

    private SingleLiveEvent<Void> clearMessageField = new SingleLiveEvent<>();
    private MutableLiveData<List<MessageModel>> messageList = new MutableLiveData<>();

    public ChatViewModel(String receiverUid) {
        this.receiverUid = receiverUid;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        senderUid = firebaseAuth.getUid();

        chatRoomId = getChatRoomId(senderUid, receiverUid);

        simpleChatReference = firebaseDatabase.getReference().child("simpleChatRooms")
                .child(chatRoomId).child("messages");

        loadReceiver();
        loadMessages();
    }

    private void loadReceiver () {
        firebaseFirestore.collection("users")
                .document(receiverUid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    receiver.setValue(value.toObject(UserModel.class));
                }
            }
        });
    }

    private void loadMessages () {
        simpleChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MessageModel> messages = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel messageModel = snapshot1.getValue(MessageModel.class);
                    messages.add(messageModel);
                }
                messageList.setValue(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<Void> clearMessageField () {
        return clearMessageField;
    }

    public LiveData<UserModel> getReceiver () {
        return receiver;
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
        MessageModel messageModel = new MessageModel(message, senderUid, receiverUid, currentTime);
        simpleChatReference.push().setValue(messageModel);
    }

}
