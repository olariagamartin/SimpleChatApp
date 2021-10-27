package com.themarto.mychatapp.mainActivity.chat;

import static com.themarto.mychatapp.utils.Utils.getDate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.themarto.mychatapp.data.domain.MessageModel;
import com.themarto.mychatapp.R;
import com.themarto.mychatapp.databinding.MessageReceivedItemviewBinding;
import com.themarto.mychatapp.databinding.MessageSentItemviewBinding;

public class MessageAdapter extends ListAdapter<MessageModel, RecyclerView.ViewHolder>{

    private final static int SENT_MESSAGE = 334;
    private final static int RECEIVED_MESSAGE = 457;

    private String currentUserId;

    protected MessageAdapter(String currentUserId) {
        super(new ChatDiffCallback());
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENT_MESSAGE) {
            return new SentViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent_itemview, parent, false));
        } else {
            return new ReceivedViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_received_itemview, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = getItem(position);
        if (holder.getClass() == SentViewHolder.class) {
            ((SentViewHolder) holder).bind(message);
        } else {
            ((ReceivedViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getSenderId().equals(currentUserId)) {
            return SENT_MESSAGE;
        }
        else {
            return RECEIVED_MESSAGE;
        }
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {

        MessageSentItemviewBinding binding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MessageSentItemviewBinding.bind(itemView);
        }

        public void bind (MessageModel messageModel) {
            binding.message.setText(messageModel.getMessage());
            binding.time.setText(getDate(messageModel.getTimestamp()));
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {

        MessageReceivedItemviewBinding binding;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MessageReceivedItemviewBinding.bind(itemView);
        }

        public void bind (MessageModel messageModel) {
            binding.message.setText(messageModel.getMessage());
            binding.time.setText(getDate(messageModel.getTimestamp()));
        }
    }

    public static class ChatDiffCallback extends DiffUtil.ItemCallback<MessageModel> {

        @Override
        public boolean areItemsTheSame(@NonNull MessageModel oldItem, @NonNull MessageModel newItem) {
            return oldItem.getTimestamp() == newItem.getTimestamp();
        }

        @Override
        public boolean areContentsTheSame(@NonNull MessageModel oldItem, @NonNull MessageModel newItem) {
            return oldItem.getMessage().equals(newItem.getMessage())
                    && oldItem.getTimestamp() == newItem.getTimestamp();
        }
    }
}
