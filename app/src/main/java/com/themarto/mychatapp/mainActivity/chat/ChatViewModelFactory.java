package com.themarto.mychatapp.mainActivity.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ChatViewModelFactory implements ViewModelProvider.Factory {

    private String receiverUid;

    public ChatViewModelFactory(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            return (T) new ChatViewModel(receiverUid);
        }
        throw new IllegalArgumentException();
    }
}
