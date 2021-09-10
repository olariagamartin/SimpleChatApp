package com.themarto.mychatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase () {
        DocumentReference documentReference = firebaseFirestore
                .collection("users")
                .document(firebaseAuth.getUid());
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            UserModel userModel = documentSnapshot.toObject(UserModel.class);
            loadUserData(userModel);
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Load user data failed", Toast.LENGTH_SHORT)
                    .show();
        });
    }

    private void loadUserData(UserModel user) {
        Picasso.get().load(user.getImage()).into(binding.profileImage);
        binding.username.setText(user.getName());
    }
}