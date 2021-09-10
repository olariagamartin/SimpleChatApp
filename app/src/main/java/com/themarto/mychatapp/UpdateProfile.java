package com.themarto.mychatapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.udacity.mychatapp.R;
import com.google.firebase.udacity.mychatapp.databinding.ActivityUpdateProfileBinding;
import com.squareup.picasso.Picasso;

public class UpdateProfile extends AppCompatActivity {

    private ActivityUpdateProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

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

}