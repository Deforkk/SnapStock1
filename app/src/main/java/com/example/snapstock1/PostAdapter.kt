package com.example.snapstock1

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapstock1.databinding.ItemPostBinding
import com.google.firebase.firestore.QueryDocumentSnapshot

class PostAdapter(
    private val posts: List<QueryDocumentSnapshot>,
    private val onPostClick: (QueryDocumentSnapshot) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        Log.d("PostAdapter", "Binding post: ${post.getString("title")}")
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: QueryDocumentSnapshot) {
            binding.postTitle.text = post.getString("title")
            binding.postDescription.text = post.getString("description")

            // Загрузка изображения
            val imageUrl = post.getString("image_url") // Предполагается, что ссылка на изображение хранится в поле image_url
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(imageUrl) // Загружаем изображение из URL
                    .into(binding.postImage)
            }

            binding.root.setOnClickListener {
                onPostClick(post)
            }
        }
    }
}


