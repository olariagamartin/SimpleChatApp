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
import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.databinding.FragmentChatListBinding;
import com.themarto.mychatapp.utils.CustomLinearLayoutManager;

import java.util.List;

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

        setupObservers();

        return binding.getRoot();
    }

    private void setupObservers () {
        viewModel.getContactList().observe(getViewLifecycleOwner(), this::loadContactList);
    }

    private void loadContactList (List<ContactModel> contacts) {
        chatListAdapter.submitList(contacts);
        chatListAdapter.setConnected(viewModel.isConnectedToNetwork());
    }

    private void setupRecyclerView () {
        chatListAdapter = new ChatListAdapter(this::goToChatFragment);

        binding.chatList.setAdapter(chatListAdapter);
        binding.chatList.setLayoutManager(new CustomLinearLayoutManager(requireContext()));
    }

    private void goToChatFragment (ContactModel contact) {
        NavDirections action = ChatListFragmentDirections
                .actionChatListFragmentToChatFragment(
                        contact.getId());
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