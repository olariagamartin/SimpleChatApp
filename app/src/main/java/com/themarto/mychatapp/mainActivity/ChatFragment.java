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
import com.themarto.mychatapp.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    // todo: move to view model
    private String receiverName;
    private String receiverImageLink;
    private String receiverUid;

    // todo: move to view model
    private String senderUid;

    // todo: move to view model
    private String chatRoomId;

    // todo: move to view model
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference simpleChatReference;

    private MessageAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<MessageModel> messageArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // todo: load on view model factory
        // pass only the id
        loadArgs();

        // todo: move to view model
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // todo: move to view model
        senderUid = firebaseAuth.getUid();

        // todo: move to view model
        chatRoomId = getChatRoomId(senderUid, receiverUid);

        // todo: move to view model
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
            // todo: move to view model
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

    private void loadArgs() {
        ChatFragmentArgs args = ChatFragmentArgs.fromBundle(getArguments());
        receiverName = args.getReceiverName();
        receiverImageLink = args.getReceiverImage();
        receiverUid = args.getReceiverUid();
    }

    private void setupToolbar () {
        binding.chatName.setText(receiverName);
        Picasso.get().load(receiverImageLink).into(binding.chatImage);
    }

    // todo: move to utils
    private String getChatRoomId (String senderId, String receiverId) {
        if (senderId.compareTo(receiverId) < 0 ) {
            return senderId + receiverId;
        } else {
            return receiverId + senderId;
        }
    }

    private void setupMessageList () {
        // todo: move adapter to mainActivity package
        messageArrayList = new ArrayList<MessageModel>();
        adapter = new MessageAdapter(messageArrayList, senderUid);
        layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.messageList.setLayoutManager(layoutManager);
        binding.messageList.setAdapter(adapter);

        fetchMessages();
    }

    // todo: move to view model
    private void fetchMessages () {
        simpleChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel messageModel = snapshot1.getValue(MessageModel.class);
                    messageArrayList.add(messageModel);
                }
                // todo: use list adapter
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}