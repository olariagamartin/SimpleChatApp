package com.themarto.mychatapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.udacity.mychatapp.R;
import com.google.firebase.udacity.mychatapp.databinding.ActivityUpdateProfileBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UpdateProfile extends AppCompatActivity {

    private ActivityUpdateProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private UserModel user;

    private Uri imagePath;

    private ActivityResultLauncher<Intent> pickPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    imagePath = result.getData().getData();

                    updateProfileImage();
                    // receive the photo taken
                    /*Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    binding.profileImage.setImageBitmap(imageBitmap);*/
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        fetchDataFromFirebase();

        setEditUsernameAction();
    }

    private void fetchDataFromFirebase() {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            user = documentSnapshot.toObject(UserModel.class);
            loadUserData();
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Load user data failed", Toast.LENGTH_SHORT)
                    .show();
        });
    }

    private void loadUserData() {
        Picasso.get().load(user.getImage()).into(binding.profileImage);
        binding.username.setText(user.getName());
    }

    private void setEditUsernameAction() {
        binding.editUsername.setOnClickListener(v -> {
            showDialogChangeUsername();
        });
        binding.profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            // take photo
            //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            pickPhotoLauncher.launch(intent);
        });
    }

    private void showDialogChangeUsername() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View editLayout = getLayoutInflater().inflate(R.layout.dialog_update_username, null);
        EditText username = editLayout.findViewById(R.id.username);
        username.setText(user.getName());
        username.requestFocus(); // required for API 28+
        username.setSelection(username.getText().length());

        builder.setView(editLayout)
                .setPositiveButton("Save", (dialog, which) -> {
                    updateUsername(username.getText().toString());
                })
                .setNegativeButton("Cancel", null);
        // 1: avoid cut the view when keyboard appears, 2: make the keyboard appear
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();
    }

    private void updateUsername(String username) {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());

        documentReference.update("name", username)
                .addOnSuccessListener(unused -> {
                    binding.username.setText(username);
                });
    }

    private void updateProfileImage() {
        StorageReference imageRef = storageReference
                .child("images")
                .child(firebaseAuth.getUid())
                .child("profile image");

        byte[] data = getCompressedImage();

        UploadTask uploadTask = imageRef.putBytes(data);
        binding.progressBar.setVisibility(View.VISIBLE);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUriAccessToken = uri.toString();
                //Toast.makeText(getApplicationContext(), "URI get success", Toast.LENGTH_SHORT).show();
                updateProfileImageLink(imageUriAccessToken);
            }).addOnFailureListener(e -> {
                //Toast.makeText(getApplicationContext(), "URI get failed", Toast.LENGTH_SHORT).show();
            });

            //Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> {
            //Toast.makeText(getApplicationContext(), "Image no uploaded", Toast.LENGTH_SHORT).show();
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

    private void updateProfileImageLink(String link) {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());

        documentReference.update("image", link)
                .addOnSuccessListener(unused -> {
                    binding.profileImage.setImageURI(imagePath);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                });
    }

}