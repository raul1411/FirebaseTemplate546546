package com.example.firebasetemplate;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;

import java.util.UUID;

public class RegisterFragment extends AppFragment {
    private com.example.firebasetemplate.databinding.FragmentRegisterBinding binding;
    private Uri uriImagen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = com.example.firebasetemplate.databinding.FragmentRegisterBinding.inflate(inflater, container, false)).getRoot();
    }

    private final ActivityResultLauncher<String> galeria = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        appViewModel.setUriImagenSeleccionada(uri);
    });

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.profilePic.setOnClickListener(v1 -> {
            galeria.launch("image/*");
        });

        appViewModel.uriImagenSeleccionada.observe(getViewLifecycleOwner(), uri -> {
            uriImagen = uri;
            Glide.with(this).load(uriImagen).into(binding.profilePic);
        });


        binding.createAccountButton.setOnClickListener(v -> {
            if (binding.passwordEditText.getText().toString().isEmpty()) {
                binding.passwordEditText.setError("Required");
                return;
            }

            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                            binding.emailEditText.getText().toString(),
                            binding.passwordEditText.getText().toString()
                    ).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseStorage.getInstance().getReference("/profileimages/" + UUID.randomUUID() + ".jpg")
                            .putFile(uriImagen)
                            .continueWithTask( task2 ->
                                task2.getResult().getStorage().getDownloadUrl()
                                    .addOnSuccessListener( url -> {
                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(binding.usernameEditText.getText().toString())
                                                .setPhotoUri(uriImagen)
                                                .build();
                                        firebaseUser.updateProfile(userProfileChangeRequest);
                                    })
                            );


                    // upload photo i obtinc url de descarrega
                    // success: actualitza el perfil de l'usuari amb el nom i la foto
                    navController.navigate(R.id.action_registerFragment_to_postsHomeFragment);
                } else {
                    Toast.makeText(requireContext(), task.getException().getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private final ActivityResultLauncher<String> galeria2 = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> appViewModel.setUriImagenPerfilSeleccionada(uri));
}
