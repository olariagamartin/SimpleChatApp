package com.themarto.mychatapp.mainActivity.updateProfile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;
import com.themarto.mychatapp.R;
import com.themarto.mychatapp.data.domain.ContactModel;
import com.themarto.mychatapp.databinding.FragmentUpdateProfileBinding;

public class UpdateProfileFragment extends Fragment {

    private FragmentUpdateProfileBinding binding;

    private UpdateProfileViewModel viewModel;

    private ActivityResultLauncher<Intent> pickPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri imagePath = result.getData().getData();
                    //viewModel.onProfileImagePicked(imagePath);
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(UpdateProfileViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false);

        setupClickActions();

        setupObservers();

        return binding.getRoot();
    }

    private void setupObservers () {
        //viewModel.getUsername().observe(getViewLifecycleOwner(), this::loadUsername);
        //viewModel.getImageLink().observe(getViewLifecycleOwner(), this::loadImage);
        viewModel.getUser().observe(getViewLifecycleOwner(), this::loadUser);

        viewModel.showDialogChangeUsername()
                .observe(getViewLifecycleOwner(), unused -> showDialogChangeUsername());

        viewModel.launchImagePicker()
                .observe(getViewLifecycleOwner(), unused -> launchImagePicker());

        viewModel.showProgressBar().observe(getViewLifecycleOwner(), this::showProgressBar);
    }

    private void loadUser (ContactModel user) {
        binding.username.setText(user.getName());
        binding.profileImage.setImageBitmap(user.getProfileImage());
    }

    private void loadUsername(String username) {
        binding.username.setText(username);
    }

    private void loadImage (String imageLink) {
        Picasso.get().load(imageLink).into(binding.profileImage);
    }

    private void launchImagePicker () {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        pickPhotoLauncher.launch(intent);
    }

    private void showProgressBar (boolean show) {
        if (show) binding.progressBar.setVisibility(View.VISIBLE);
        else binding.progressBar.setVisibility(View.INVISIBLE);
    }

    private void setupClickActions() {
        binding.editUsername.setOnClickListener(v -> {
            viewModel.onEditUserNameClicked();
        });
        binding.profileImage.setOnClickListener(v -> {
            viewModel.onEditProfileImageClicked();
        });
    }

    private void showDialogChangeUsername() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View editLayout = getLayoutInflater().inflate(R.layout.dialog_update_username, null);
        EditText username = editLayout.findViewById(R.id.username);
        //username.setText(viewModel.getUsername().getValue());
        username.requestFocus(); // required for API 28+
        username.setSelection(username.getText().length());

        builder.setView(editLayout)
                .setPositiveButton("Save", (dialog, which) -> {
                    String nUsername = username.getText().toString();
                    //viewModel.onSaveUsernameClicked(nUsername);
                })
                .setNegativeButton("Cancel", null);
        // 1: avoid cut the view when keyboard appears, 2: make the keyboard appear
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();
    }
}