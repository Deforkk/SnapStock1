package com.example.snapstock1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapstock1.databinding.ItemPostBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class SimplePostAdapter(
    private val posts: List<QueryDocumentSnapshot>,
    private val firestore: FirebaseFirestore
) : RecyclerView.Adapter<SimplePostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, firestore)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(
        private val binding: ItemPostBinding,
        private val firestore: FirebaseFirestore
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: QueryDocumentSnapshot) {
            // Загрузка данных поста
            binding.postTitle.text = post.getString("title")
            binding.postDescription.text = post.getString("description")
            Glide.with(binding.postImage.context).load(post.getString("image_url"))
                .into(binding.postImage)

            // Загрузка автора
            val userId = post.getString("user_id")
            if (!userId.isNullOrEmpty()) {
                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { userDocument ->
                        val username = userDocument.getString("username") ?: "Unknown"
                        binding.postAuthor.text = username
                    }
                    .addOnFailureListener {
                        binding.postAuthor.text = "Unknown"
                    }
            } else {
                binding.postAuthor.text = "Unknown"
            }

            // Загрузка даты
            val createdAt = post.getTimestamp("created_at")
            binding.postDate.text = createdAt?.toDate()?.toString() ?: "Unknown Date"
        }
    }
}
