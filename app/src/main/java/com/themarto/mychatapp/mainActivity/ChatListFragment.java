package com.themarto.mychatapp.mainActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;
import com.themarto.mychatapp.R;
import com.themarto.mychatapp.UserModel;
import com.themarto.mychatapp.databinding.ChatItemviewBinding;
import com.themarto.mychatapp.databinding.FragmentChatListBinding;

public class ChatListFragment extends Fragment {

    private FragmentChatListBinding binding;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    FirestoreRecyclerAdapter<UserModel, ChatHolder> chatAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater, container, false);

        Query query = firebaseFirestore.collection("users")
                .whereNotEqualTo("uid", firebaseAuth.getUid());

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
                    // todo: test
                    NavDirections action = ChatListFragmentDirections
                            .actionChatListFragmentToChatFragment(
                                    model.getUid(),
                                    model.getName(),
                                    model.getImage());
                    Navigation.findNavController(binding.getRoot()).navigate(action);
                    /*Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                    intent.putExtra("receiverName", model.name);
                    intent.putExtra("receiverImageUrl", model.getImage());
                    intent.putExtra("receiverUid", model.getUid());
                    startActivity(intent);*/
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
        binding.chatList.setLayoutManager(new CustomLinearLayoutManager(requireContext()));

        setOptionMenu();

        return binding.getRoot();
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
            // todo: test
            NavDirections action = ChatListFragmentDirections
                    .actionChatListFragmentToUpdateProfileFragment();
            Navigation.findNavController(binding.getRoot()).navigate(action);
            /*Intent intent = new Intent(ChatListActivity.this, UpdateProfile.class);
            startActivity(intent);*/
            return true;
        });
    }

}