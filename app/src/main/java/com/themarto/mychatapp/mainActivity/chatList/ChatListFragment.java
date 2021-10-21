package com.themarto.mychatapp.mainActivity.chatList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.themarto.mychatapp.R;
import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.databinding.FragmentChatListBinding;
import com.themarto.mychatapp.loginActivity.LoginActivity;
import com.themarto.mychatapp.mainActivity.MainActivity;
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

        viewModel.goToUpdateProfile().observe(getViewLifecycleOwner(), unused -> goToUpdateProfile());

        viewModel.goToLoginActivity().observe(getViewLifecycleOwner(), unused -> goToLoginActivity());
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

    private void goToUpdateProfile () {
        NavDirections action = ChatListFragmentDirections
                .actionChatListFragmentToUpdateProfileFragment();
        Navigation.findNavController(binding.getRoot()).navigate(action);
    }

    private void goToLoginActivity () {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void setOptionMenu() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.go_to_profile: viewModel.onProfileItemClicked();
                    return true;
                case R.id.sign_out: viewModel.onLogoutItemClicked();
                    return true;
                default: return true;
            }
        });
    }

}