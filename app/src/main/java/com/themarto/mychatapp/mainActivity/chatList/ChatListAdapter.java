package com.themarto.mychatapp.mainActivity.chatList;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.themarto.mychatapp.R;
import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.databinding.ChatItemviewBinding;

public class ChatListAdapter extends ListAdapter<ContactModel, ChatListAdapter.ChatHolder> {

    private ItemClickListener listener;

    private boolean isConnected = false;

    protected ChatListAdapter(ItemClickListener listener) {
        super(new ContactDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_itemview, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        ContactModel currentContact = getItem(position);
        holder.binding.chatName.setText(currentContact.getName());
        holder.binding.chatImage.setImageBitmap(currentContact.getProfileImage());
        if (isConnected) {
            if (currentContact.isOnline()) {
                holder.binding.chatStatus.setText("Online");
                holder.binding.chatStatus.setTextColor(Color.BLUE);
            } else {
                holder.binding.chatStatus.setTextColor(Color.GRAY);
                holder.binding.chatStatus.setText("Offline");
            }
            holder.binding.chatStatus.setVisibility(View.VISIBLE);
        } else {
            holder.binding.chatStatus.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(currentContact));
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public class ChatHolder extends RecyclerView.ViewHolder{

        ChatItemviewBinding binding;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatItemviewBinding.bind(itemView);
        }
    }

    public static class ContactDiffCallback extends DiffUtil.ItemCallback<ContactModel> {

        @Override
        public boolean areItemsTheSame(@NonNull ContactModel oldItem, @NonNull ContactModel newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ContactModel oldItem, @NonNull ContactModel newItem) {
            return oldItem.equals(newItem);
        }
    }

    public interface ItemClickListener {
        void onClick (ContactModel contact);
    }
}
