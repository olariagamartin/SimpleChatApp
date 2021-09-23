package com.themarto.mychatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {

    private List<MessageModel> messageList;

    private final static int SENT_MESSAGE = 334;
    private final static int RECEIVED_MESSAGE = 457;

    private String currentUserId;

    public MessageAdapter(List<MessageModel> messageList, String currentUserId) {
        this.messageList = messageList;
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
        MessageModel message = messageList.get(position);
        if (holder.getClass() == SentViewHolder.class) {
            ((SentViewHolder) holder).bind(message);
        } else {
            ((ReceivedViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).senderId.equals(currentUserId)) {
            return SENT_MESSAGE;
        } else {
            return RECEIVED_MESSAGE;
        }
    }

    class SentViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        TextView time;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }

        public void bind (MessageModel messageModel) {
            message.setText(messageModel.getMessage());
            time.setText(getDate(messageModel.getTimestamp()));
        }
    }

    class ReceivedViewHolder extends RecyclerView.ViewHolder {

        TextView message;
        TextView time;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }

        public void bind (MessageModel messageModel) {
            message.setText(messageModel.getMessage());
            time.setText(getDate(messageModel.getTimestamp()));
        }
    }

    public static String getDate (long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(new Date(millis));
    }
}
