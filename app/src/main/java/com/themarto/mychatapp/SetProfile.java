package com.themarto.mychatapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.udacity.mychatapp.databinding.ActivitySetProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetProfile extends AppCompatActivity {

    private ActivitySetProfileBinding binding;
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
                Intent intent = new Intent(SetProfile.this, ChatListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendDataForNewUser () {
        sendDataToRealtimeDatabase();
        sendImageToStorage();
    }

    private void sendDataToRealtimeDatabase() {
        // to send data to realtime db we need a class
        username = binding.userName.getText().toString().trim();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        UserProfile userProfile = new UserProfile(username, firebaseAuth.getUid());
        databaseReference.setValue(userProfile);
        Toast.makeText(getApplicationContext(), "User Profile dded successfully", Toast.LENGTH_SHORT).show();
    }

    private void sendImageToStorage () {
        StorageReference imageRef = storageReference
                .child("images")
                .child(firebaseAuth.getUid())
                .child("profile image");

        byte[] data = getCompressedImage();

        // putting image to storage
        UploadTask uploadTask = imageRef.putBytes(data);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                imageUriAccessToken = uri.toString();
                Toast.makeText(getApplicationContext(), "URI get success", Toast.LENGTH_SHORT).show();
                sendDataToCloudFirestore();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "URI get failed", Toast.LENGTH_SHORT).show();
            });

            Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Image no uploaded", Toast.LENGTH_SHORT).show();
        });
    }

    private byte[] getCompressedImage() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void sendDataToCloudFirestore() {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", username);
        userData.put("image", imageUriAccessToken);
        userData.put("uid", firebaseAuth.getUid());
        userData.put("status", "Online");

        documentReference.set(userData).addOnSuccessListener(unused -> {
            Toast.makeText(getApplicationContext(),
                    "Data on Cloud Firestore send success",
                    Toast.LENGTH_SHORT).show();
        });
    }

}