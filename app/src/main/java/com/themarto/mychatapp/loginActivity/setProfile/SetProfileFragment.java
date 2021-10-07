package com.themarto.mychatapp.loginActivity.setProfile;

import static android.app.Activity.RESULT_OK;

import static com.themarto.mychatapp.Constants.PROFILE_IMAGE_NOT_SET;
import static com.themarto.mychatapp.Constants.USERNAME_EMPTY;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.themarto.mychatapp.mainActivity.MainActivity;
import com.themarto.mychatapp.databinding.FragmentSetProfileBinding;

public class SetProfileFragment extends Fragment {

    private FragmentSetProfileBinding binding;

    private SetProfileViewModel viewModel;

    private ActivityResultLauncher<Intent> pickPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    viewModel.onPhotoPickActionDone(result.getData().getData());
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSetProfileBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(SetProfileViewModel.class);

        setProfileImageListener();
        setSaveProfileButtonListener();

        setupObservers();

        return binding.getRoot();
    }

    private void setProfileImageListener() {
        binding.profileImage.setOnClickListener(v -> {
            viewModel.onProfilePhotoClicked();
        });
    }

    private void launchPhotoPicker () {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        pickPhotoLauncher.launch(intent);
    }

    private void setSaveProfileButtonListener () {
        binding.saveProfileBtn.setOnClickListener(v -> {
            String username = binding.userName.getText().toString();
            viewModel.onSaveProfileClicked(username);
        });
    }

    private void setupObservers () {
        viewModel.launchPhotoPicker()
                .observe(getViewLifecycleOwner(), unused -> launchPhotoPicker());

        viewModel.loadProfileImage()
                .observe(getViewLifecycleOwner(), unused -> loadProfileImage());

        viewModel.showSnackBarMessage()
                .observe(getViewLifecycleOwner(), this::showSnackBarMessage);

        viewModel.showProgressBar()
                .observe(getViewLifecycleOwner(), this::showProgressBar);

        viewModel.goToMainActivity()
                .observe(getViewLifecycleOwner(), unused -> goToMainActivity());
    }

    private void loadProfileImage () {
        binding.profileImage.setImageURI(viewModel.getImagePath());
    }

    private void showSnackBarMessage (int messageCode) {
        switch (messageCode) {
            case USERNAME_EMPTY:
                Snackbar.make(binding.getRoot(), "Username is empty", Snackbar.LENGTH_SHORT).show();
                break;
            case PROFILE_IMAGE_NOT_SET:
                Snackbar.make(binding.getRoot(), "Select a profile image", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private void showProgressBar (boolean show) {
        if (show) binding.saveProfileProgress.setVisibility(View.VISIBLE);
        else binding.saveProfileProgress.setVisibility(View.INVISIBLE);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}