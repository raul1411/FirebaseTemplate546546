package com.example.firebasetemplate;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentUpdateProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;
import java.util.UUID;

public class UpdateProfile extends AppFragment {

    private FragmentUpdateProfileBinding binding;
    private Uri uriLink;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.nameEditText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        binding.photo.setOnClickListener(view2 -> {
            galeria.launch("image/*");
        });

        appViewModel.uriImagenPerfilSeleccionada.observe(getViewLifecycleOwner(), uri -> {
            Glide.with(this).load(uri).into(binding.photo);
            uriLink = uri;
        });

        binding.updateButton.setOnClickListener(view1 -> {

            if (!binding.nameEditText.getText().toString().isEmpty()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(binding.nameEditText.getText().toString())
                        .build();
                user.updateProfile(userProfileChangeRequest);
            }

            if (uriLink != null) {
                FirebaseStorage.getInstance()
                        .getReference("/images/" + UUID.randomUUID() + ".jpg")
                        .putFile(uriLink)
                        .continueWithTask(task1 -> Objects.requireNonNull(task1.getResult()).getStorage().getDownloadUrl())
                        .addOnSuccessListener(urlDescarga -> {
                            uriLink = urlDescarga;
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uriLink)
                                    .build();
                            user.updateProfile(profileUpdates);
                        });
            }
            navController.navigate(R.id.profileFragment);
            Log.d("asd", "Profile updated!");
        });

    }

    private final ActivityResultLauncher<String> galeria = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> appViewModel.setUriImagenPerfilSeleccionada(uri));



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)).getRoot();
    }
}