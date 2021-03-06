package com.example.firebasetemplate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends AppFragment {
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentProfileBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                binding.textView.setText(firebaseAuth.getCurrentUser().getEmail());
                binding.textView2.setText(firebaseAuth.getCurrentUser().getDisplayName());
                if (firebaseAuth.getCurrentUser().getPhotoUrl() != null) {
                    Glide.with(getContext()).load(firebaseAuth.getCurrentUser().getPhotoUrl()).circleCrop().into(binding.imageView);
                }
                else {
                    binding.imageView.setImageResource(R.drawable.ic_person);
                }
            }
        });

        binding.buttonEdit.setOnClickListener(v -> navController.navigate(R.id.updateProfile));

    }
}