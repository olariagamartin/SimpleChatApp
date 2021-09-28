package com.themarto.mychatapp.mainActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.themarto.mychatapp.R;
import com.themarto.mychatapp.databinding.FragmentUpdateProfileBinding;

public class UpdateProfileFragment extends Fragment {

    private FragmentUpdateProfileBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}