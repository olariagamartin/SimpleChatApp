package com.themarto.mychatapp.mainActivity.chat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ChatViewModelFactory implements ViewModelProvider.Factory {

    private String receiverUid;

    private Application application;

    public ChatViewModelFactory(Application application, String receiverUid) {
        this.application = application;
        this.receiverUid = receiverUid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            return (T) new ChatViewModel(application, receiverUid);
        }
        throw new IllegalArgumentException();
    }
}
