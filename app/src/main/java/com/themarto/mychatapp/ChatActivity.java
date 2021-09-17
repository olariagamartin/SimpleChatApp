package com.themarto.mychatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.udacity.mychatapp.R;
import com.google.firebase.udacity.mychatapp.databinding.ActivityChatBinding;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    private String receiverName;
    private String receiverImageLink;
    private String receiverUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadExtra();
        setupToolbar();

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
}