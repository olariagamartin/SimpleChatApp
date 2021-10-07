package com.themarto.mychatapp.mainActivity.chatList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.themarto.mychatapp.UserModel;
import com.themarto.mychatapp.databinding.FragmentChatListBinding;
import com.themarto.mychatapp.utils.CustomLinearLayoutManager;

public class ChatListFragment extends Fragment {

    private FragmentChatListBinding binding;

    private ChatListViewModel viewModel;

    ChatListAdapter chatListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(ChatListViewModel.class);

        setupRecyclerView();

        setOptionMenu();

        return binding.getRoot();
    }

    private void setupRecyclerView () {
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(viewModel.getUsersQuery(), UserModel.class)
                .setLifecycleOwner(this)
                .build();

        chatListAdapter = new ChatListAdapter(options, this::goToChatFragment);

        binding.chatList.setAdapter(chatListAdapter);
        binding.chatList.setLayoutManager(new CustomLinearLayoutManager(requireContext()));
    }

    private void goToChatFragment (UserModel model) {
        NavDirections action = ChatListFragmentDirections
                .actionChatListFragmentToChatFragment(
                        model.getUid());
        Navigation.findNavController(binding.getRoot()).navigate(action);
    }

    private void setOptionMenu() {
        MenuItem profile = binding.toolbar.getMenu().add("Profile");
        profile.setOnMenuItemClickListener(item -> {
            NavDirections action = ChatListFragmentDirections
                    .actionChatListFragmentToUpdateProfileFragment();
            Navigation.findNavController(binding.getRoot()).navigate(action);
            return true;
        });
    }

}