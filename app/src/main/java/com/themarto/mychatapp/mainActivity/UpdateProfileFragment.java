package com.themarto.mychatapp.mainActivity;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;
import com.themarto.mychatapp.R;
import com.themarto.mychatapp.UserModel;
import com.themarto.mychatapp.databinding.FragmentUpdateProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UpdateProfileFragment extends Fragment {

    private FragmentUpdateProfileBinding binding;
    // todo: move all to view model
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private UserModel user;

    private Uri imagePath;

    // todo: test
    private ActivityResultLauncher<Intent> pickPhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // todo: notify view model
                    imagePath = result.getData().getData();

                    updateProfileImage();
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // todo: move to view model
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false);

        fetchDataFromFirebase();

        setEditUsernameAction();

        return binding.getRoot();
    }

    // todo: move to view model
    private void fetchDataFromFirebase() {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            // todo: check for null object
            user = documentSnapshot.toObject(UserModel.class);
            loadUserData();
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Load user data failed", Toast.LENGTH_SHORT)
                    .show();
        });
    }

    // todo: observe data from view model
    private void loadUserData() {
        Picasso.get().load(user.getImage()).into(binding.profileImage);
        binding.username.setText(user.getName());
    }

    // todo: change name
    // todo: notify view model
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View editLayout = getLayoutInflater().inflate(R.layout.dialog_update_username, null);
        EditText username = editLayout.findViewById(R.id.username);
        username.setText(user.getName());
        username.requestFocus(); // required for API 28+
        username.setSelection(username.getText().length());

        builder.setView(editLayout)
                .setPositiveButton("Save", (dialog, which) -> {
                    // todo: send to view model
                    updateUsername(username.getText().toString());
                })
                .setNegativeButton("Cancel", null);
        // 1: avoid cut the view when keyboard appears, 2: make the keyboard appear
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();
    }

    // todo: move to view model
    private void updateUsername(String username) {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());

        documentReference.update("name", username)
                .addOnSuccessListener(unused -> {
                    binding.username.setText(username);
                });
    }

    // todo: move to view model
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
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // todo: move to view model
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