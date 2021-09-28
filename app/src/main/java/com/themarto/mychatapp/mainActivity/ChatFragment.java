package com.themarto.mychatapp.mainActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.themarto.mychatapp.MessageAdapter;
import com.themarto.mychatapp.MessageModel;
import com.themarto.mychatapp.R;
import com.themarto.mychatapp.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadExtra();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        senderUid = firebaseAuth.getUid();

        chatRoomId = getChatRoomId(senderUid, receiverUid);

        simpleChatReference = firebaseDatabase.getReference().child("simpleChatRooms")
                .child(chatRoomId).child("messages");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        setupSendMessageButton();

        setupMessageList();

        setupToolbar();

        return binding.getRoot();
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
        // todo: load received data
        /*receiverName = getIntent().getStringExtra("receiverName");
        receiverImageLink = getIntent().getStringExtra("receiverImageUrl");
        receiverUid = getIntent().getStringExtra("receiverUid");*/
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
        layoutManager = new LinearLayoutManager(requireContext());
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