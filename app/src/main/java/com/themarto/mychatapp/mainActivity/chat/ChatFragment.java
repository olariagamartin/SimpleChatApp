package com.themarto.mychatapp.mainActivity.chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.themarto.mychatapp.data.domain.MessageModel;
import com.themarto.mychatapp.UserModel;
import com.themarto.mychatapp.databinding.FragmentChatBinding;

import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    private ChatViewModel viewModel;

    private MessageAdapter adapter;
    private LinearLayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChatFragmentArgs args = ChatFragmentArgs.fromBundle(getArguments());
        String receiverUid = args.getReceiverUid();
        ChatViewModelFactory factory = new ChatViewModelFactory(receiverUid);
        viewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        setupSendMessageButton();

        setupMessageList();

        setupObservers();

        return binding.getRoot();
    }

    private void setupObservers() {
        viewModel.clearMessageField()
                .observe(getViewLifecycleOwner(), unused -> clearMessageField());

        viewModel.getReceiver()
                .observe(getViewLifecycleOwner(), this::loadToolbar);

        viewModel.getMessageList()
                .observe(getViewLifecycleOwner(), this::loadMessages);
    }

    private void clearMessageField () {
        binding.messageToSend.setText(null);
    }

    private void setupSendMessageButton () {
        binding.sendBtn.setOnClickListener(v -> {
            String message = binding.messageToSend.getText().toString();
            viewModel.onSendMessageClicked(message);
        });
    }

    private void loadToolbar(UserModel receiver) {
        binding.chatName.setText(receiver.getName());
        Picasso.get().load(receiver.getImage()).into(binding.chatImage);
    }

    private void setupMessageList () {
        adapter = new MessageAdapter(viewModel.getSenderUid());
        layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.messageList.setLayoutManager(layoutManager);
        binding.messageList.setAdapter(adapter);
    }

    private void loadMessages (List<MessageModel> messageList) {
        adapter.submitList(messageList);
        binding.messageList.smoothScrollToPosition(adapter.getItemCount());
    }

}