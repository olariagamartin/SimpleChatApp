package com.themarto.mychatapp.mainActivity.chatList;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;
import com.themarto.mychatapp.R;
import com.themarto.mychatapp.UserModel;
import com.themarto.mychatapp.databinding.ChatItemviewBinding;

public class ChatListAdapter extends FirestoreRecyclerAdapter<UserModel, ChatListAdapter.ChatHolder> {

    private ItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     */
    public ChatListAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options,
                           ItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

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

        holder.itemView.setOnClickListener(v -> listener.onClick(model));
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_itemview, parent, false);
        return new ChatHolder(view);
    }

    public class ChatHolder extends RecyclerView.ViewHolder{

        ChatItemviewBinding binding;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatItemviewBinding.bind(itemView);
        }
    }

    public interface ItemClickListener {
        void onClick (UserModel model);
    }
}
