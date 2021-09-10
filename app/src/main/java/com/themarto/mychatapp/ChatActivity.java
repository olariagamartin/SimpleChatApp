package com.themarto.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.udacity.mychatapp.R;
import com.google.firebase.udacity.mychatapp.databinding.ActivityChatBinding;
import com.google.firebase.udacity.mychatapp.databinding.ChatItemviewBinding;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    FirestoreRecyclerAdapter<UserModel, ChatHolder> chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Query query = firebaseFirestore.collection("users");
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .setLifecycleOwner(this)
                .build();

        chatAdapter = new FirestoreRecyclerAdapter<UserModel, ChatHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull UserModel model) {
                holder.binding.chatName.setText(model.getName());
                String chatImageUri = model.getImage();
                Picasso.get().load(chatImageUri).into(holder.binding.chatImage);
                if (model.getStatus().equals("Online")) {
                    holder.binding.chatStatus.setText(model.getStatus());
                    holder.binding.chatStatus.setTextColor(Color.BLUE);
                } else {
                    // todo: test color on updated
                    holder.binding.chatStatus.setText(model.getStatus());
                }

                holder.itemView.setOnClickListener(v -> {
                    Toast.makeText(getApplicationContext(), "Open chat", Toast.LENGTH_SHORT)
                            .show();
                });
            }

            @NonNull
            @Override
            public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_itemview, parent, false);
                return new ChatHolder(view);
            }
        };

        binding.chatList.setAdapter(chatAdapter);
        binding.chatList.setLayoutManager(new CustomLinearLayoutManager(getApplicationContext()));

        setOptionMenu();

    }

    public class ChatHolder extends RecyclerView.ViewHolder{

        ChatItemviewBinding binding;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatItemviewBinding.bind(itemView);
        }
    }

    public class CustomLinearLayoutManager extends LinearLayoutManager {
        public CustomLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }

    private void setOptionMenu() {
        MenuItem profile = binding.toolbar.getMenu().add("Profile");
        profile.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(ChatActivity.this, UpdateProfile.class);
            startActivity(intent);
            return true;
        });
    }
}