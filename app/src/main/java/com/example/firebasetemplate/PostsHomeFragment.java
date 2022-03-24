package com.example.firebasetemplate;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class PostsHomeFragment extends AppFragment {

    private FragmentPostsBinding binding;
    private PostsAdapter adapter;
    List<Post> postsList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentPostsBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fab.setOnClickListener(v -> navController.navigate(R.id.newPostFragment));

        binding.postsRecyclerView.setAdapter(adapter = new PostsAdapter());

        setQuery().addSnapshotListener((collectionSnapshot, e) -> {
            List<Post> postsNewList = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot: collectionSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
                post.postid = documentSnapshot.getId();
                postsNewList.add(post);
            }
            adapter.updateCommentsList(postsNewList);
        });
    }

    Query setQuery() {
        return db.collection("posts");
    }

    class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ViewholderPostBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Post post = postsList.get(position);
            holder.binding.contenido.setText(post.content);
            holder.binding.autor.setText(post.authorName);
            holder.binding.postLayout.setOnClickListener(v -> {
                NavGraphDirections.ActionGlobalPostDetails action = NavGraphDirections.actionGlobalPostDetails();
                action.setPostid(post.postid);
                navController.navigate(action);
            });

            Glide.with(getContext()).load(post.imageUser).centerCrop().into(holder.binding.autorFoto);
            Glide.with(requireContext()).load(post.imageUrl).into(holder.binding.imagen);

            holder.binding.favorito.setOnClickListener(v -> db.collection("posts").document(post.postid)
                    .update("likes."+auth.getUid(),
                            !post.likes.containsKey(auth.getUid()) ? true : FieldValue.delete()));

            holder.binding.favorito.setChecked(post.likes.containsKey(auth.getUid()));
        }
        public void updateCommentsList(List<Post> newList) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return postsList.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return postsList.get(oldItemPosition).postid.equals(newList.get(newItemPosition).postid);
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Post oldPost = postsList.get(oldItemPosition);
                    Post newpost = newList.get(newItemPosition);

                    if (!Objects.equals(oldPost.postid, newpost.postid)) return false;
                    if (!Objects.equals(oldPost.content, newpost.content)) return false;
                    if (!Objects.equals(oldPost.authorName, newpost.authorName)) return false;
                    if (!Objects.equals(oldPost.imageUrl, newpost.imageUrl)) return false;

                    if (oldPost.likes.containsKey(auth.getUid()) != newpost.likes.containsKey(auth.getUid())) return false;

                    return oldPost.likes.size() != newpost.likes.size();
                }
            });

            postsList.clear();
            postsList.addAll(newList);
            diffResult.dispatchUpdatesTo(this);
        }

        @Override
        public int getItemCount() {
            return postsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewholderPostBinding binding;
            public ViewHolder(ViewholderPostBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}