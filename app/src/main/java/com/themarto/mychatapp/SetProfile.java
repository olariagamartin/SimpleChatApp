package com.themarto.mychatapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.udacity.mychatapp.databinding.ActivitySetProfileBinding;

public class SetProfile extends AppCompatActivity {

    private ActivitySetProfileBinding binding;
    private static int PICK_IMAGE = 123;
    private Uri imagePath;

    private FirebaseAuth firebaseAuth;
    private String username;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String imageUriAccessToken;

    private FirebaseFirestore firebaseFirestore;

    private ActivityResultLauncher<Intent> pickPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    imagePath = result.getData().getData();
                    binding.profileImage.setImageURI(imagePath);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setProfileImageListener();
        setSaveProfileButtonListener();
    }

    private void setProfileImageListener() {
        binding.profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            pickPhotoLauncher.launch(intent);
        });
    }

    private void setSaveProfileButtonListener () {
        binding.saveProfileBtn.setOnClickListener(v -> {
            username = binding.userName.getText().toString();
            if (username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username is empty", Toast.LENGTH_SHORT).show();
            } else if (imagePath == null) {
                Toast.makeText(getApplicationContext(), "Choose a profile image", Toast.LENGTH_SHORT).show();
            } else {
                binding.saveProfileProgress.setVisibility(View.VISIBLE);
                sendDataForNewUser();
                binding.saveProfileProgress.setVisibility(View.VISIBLE);
                Intent intent = new Intent(SetProfile.this, ChatActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendDataForNewUser () {
        sendDataToRealtimeDatabase();
    }

    private void sendDataToRealtimeDatabase() {
        // to send data to realtime db we need a class
    }

}