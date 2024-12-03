package com.example.snapstock1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
//import androidx.glance.visibility
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapstock1.databinding.ItemPostBinding
import com.google.firebase.auth.FirebaseAuth
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
        private var isLiked = false
        private fun updateLikeButtonAppearance() {
            if (isLiked) {
                binding.likeButton.visibility = View.GONE
                binding.unlikeButton.visibility = View.VISIBLE
            } else {
                binding.likeButton.visibility = View.VISIBLE
                binding.unlikeButton.visibility = View.GONE
            }
        }

        private fun toggleLike(postId: String, userId: String?) {
            if (!userId.isNullOrEmpty()) {
                val userManager = UserManager(binding.root.context)
                if (isLiked) {
                    // Удаляем лайк
                    userManager.removeLike(postId, userId) { success ->
                        if (success) {
                            isLiked = false
                            updateLikeButtonAppearance()
                            Toast.makeText(binding.root.context, "Like removed", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            // Обработка ошибки
                            Toast.makeText(
                                binding.root.context,
                                "Failed to remove like",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // Добавляем лайк
                    userManager.addLike(postId, userId) { success ->
                        if (success) {
                            isLiked = true
                            updateLikeButtonAppearance()
                            Toast.makeText(binding.root.context, "The post was liked", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            // Обработка ошибки
                            Toast.makeText(
                                binding.root.context,
                                "Failed to like post",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                // Пользователь не авторизован
                Toast.makeText(
                    binding.root.context,
                    "Please log in",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

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


            // Обработчик кнопки лайка
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (!currentUserId.isNullOrEmpty()) {
                firestore.collection("likes").document(post.id).get()
                    .addOnSuccessListener { document ->
                        isLiked = document.exists() && document.getString("userId") == currentUserId
                        updateLikeButtonAppearance() }
                binding.likeButton.setOnClickListener {
                    toggleLike(post.id, currentUserId)
                }
                binding.unlikeButton.setOnClickListener {
                    toggleLike(post.id, currentUserId)
                }
            }
        }

    }
}
