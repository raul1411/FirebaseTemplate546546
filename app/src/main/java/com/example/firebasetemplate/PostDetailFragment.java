package com.example.firebasetemplate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentPostDetailBinding;
import com.example.firebasetemplate.databinding.FragmentPostsBinding;
import com.example.firebasetemplate.databinding.ViewholderPostBinding;
import com.example.firebasetemplate.model.Post;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class PostDetailFragment extends AppFragment {
    private FragmentPostDetailBinding binding;
//    private PostsHomeFragment.PostsAdapter adapter;
//    List<Post> postsList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentPostDetailBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db.collection("posts")
                .document(PostDetailFragmentArgs.fromBundle(getArguments()).getPostid())
                .addSnapshotListener((collectionSnapshot, e) -> {
                    if (collectionSnapshot != null) {
                        Post post = collectionSnapshot.toObject(Post.class);

                        binding.autor.setText(post.authorName);
                        binding.contenido.setText(post.content);

                        if (getActivity() == null) {
                            return;
                        } else {
                            Glide.with(getActivity()).load(post.imageUser).centerCrop().into(binding.autorFoto);
                            Glide.with(getActivity()).load(post.imageUrl).centerCrop().into(binding.imagen);
                        }

                        binding.favorito.setChecked(post.likes.containsKey(auth.getUid()));
                        post.postid = db.collection("posts").document(PostDetailFragmentArgs.fromBundle(getArguments()).getPostid()).getId();

                        binding.favorito.setOnClickListener(view1 ->
                            db.collection("posts").document(post.postid)
                                    .update("likes."+auth.getUid(),
                                            !post.likes.containsKey(auth.getUid()) ? true : FieldValue.delete()));
                    }
                });

//        binding.fab.setOnClickListener(v -> navController.navigate(R.id.newPostFragment));
//
//        binding.postsRecyclerView.setAdapter(adapter = new PostsHomeFragment.PostsAdapter());
//
//        setQuery().addSnapshotListener((collectionSnapshot, e) -> {
//            List<Post> postsNewList = new ArrayList<>();
//            for (DocumentSnapshot documentSnapshot: collectionSnapshot) {
//                Post post = documentSnapshot.toObject(Post.class);
//                post.postid = documentSnapshot.getId();
//                postsNewList.add(post);
//            }
//            adapter.updateCommentsList(postsNewList);
//        });
    }
}