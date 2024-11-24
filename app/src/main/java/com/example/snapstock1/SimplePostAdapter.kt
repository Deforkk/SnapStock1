package com.example.snapstock1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapstock1.databinding.ItemPostBinding
import com.google.firebase.firestore.QueryDocumentSnapshot

class SimplePostAdapter(private val posts: List<QueryDocumentSnapshot>) :
    RecyclerView.Adapter<SimplePostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: QueryDocumentSnapshot) {
            binding.postTitle.text = post.getString("title")
            binding.postDescription.text = post.getString("description")
            Glide.with(binding.postImage.context).load(post.getString("image_url"))
                .into(binding.postImage)
        }
    }
}
