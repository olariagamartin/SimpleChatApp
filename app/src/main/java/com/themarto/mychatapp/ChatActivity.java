package com.themarto.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.udacity.mychatapp.R;
import com.google.firebase.udacity.mychatapp.databinding.ActivityChatBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    private String receiverName;
    private String receiverImageLink;
    private String receiverUid;

    private String senderUid;

    private String chatRoomId;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference simpleChatReference;

    private MessageAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<MessageModel> messageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadExtra();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        senderUid = firebaseAuth.getUid();

        chatRoomId = getChatRoomId(senderUid, receiverUid);

        simpleChatReference = firebaseDatabase.getReference().child("simpleChatRooms")
                .child(chatRoomId).child("messages");

        setupSendMessageButton();

        setupMessageList();

        setupToolbar();

    }

    private void setupSendMessageButton () {
        binding.sendBtn.setOnClickListener(v -> {
            String message = binding.messageToSend.getText().toString();
            if (!message.isEmpty()) {
                Calendar calendar = Calendar.getInstance();
                long currentTime = calendar.getTimeInMillis();
                MessageModel messageModel = new MessageModel(message, senderUid, receiverUid, currentTime);
                simpleChatReference.push().setValue(messageModel);

                binding.messageToSend.setText(null);
            }
        });
    }

    private void loadExtra () {
        receiverName = getIntent().getStringExtra("receiverName");
        receiverImageLink = getIntent().getStringExtra("receiverImageUrl");
        receiverUid = getIntent().getStringExtra("receiverUid");
    }

    private void setupToolbar () {
        binding.chatName.setText(receiverName);
        Picasso.get().load(receiverImageLink).into(binding.chatImage);
    }

    private String getChatRoomId (String senderId, String receiverId) {
        if (senderId.compareTo(receiverId) < 0 ) {
            return senderId + receiverId;
        } else {
            return receiverId + senderId;
        }
    }

    private void setupMessageList () {
        messageArrayList = new ArrayList<MessageModel>();
        adapter = new MessageAdapter(messageArrayList, senderUid);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        binding.messageList.setLayoutManager(layoutManager);
        binding.messageList.setAdapter(adapter);

        fetchMessages();
    }

    private void fetchMessages () {
        simpleChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel messageModel = snapshot1.getValue(MessageModel.class);
                    messageArrayList.add(messageModel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}