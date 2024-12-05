package com.example.snapstock1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.snapstock1.databinding.FragmentEditPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class EditPostFragment : Fragment() {

    private lateinit var binding: FragmentEditPostBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var postId: String? = null
    private var originalTitle: String? = null
    private var originalDescription: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)

        // Получаем postId из аргументов
        postId = arguments?.getString("postId")

        if (postId != null) {
            loadPostData(postId!!)
        }

        // Обработчик кнопки для обновления поста
        binding.btnUpdate.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updatePost(postId!!, title, description)
        }

        // Обработчик кнопки для удаления поста
        binding.btnDelete.setOnClickListener {
            postId?.let { deletePost(it) }
        }

        return binding.root
    }

    private fun loadPostData(postId: String) {
        firestore.collection("pins").document(postId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    originalTitle = document.getString("title")
                    originalDescription = document.getString("description")
                    binding.etTitle.setText(originalTitle)
                    binding.etDescription.setText(originalDescription)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load post", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePost(postId: String, title: String, description: String) {
        val updatedPost = hashMapOf(
            "title" to title,
            "description" to description,
            "updated_at" to FieldValue.serverTimestamp()
        )

        firestore.collection("pins").document(postId).update(updatedPost)
            .addOnSuccessListener {
                Toast.makeText(context, "Post updated successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update post", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deletePost(postId: String) {
        firestore.collection("pins").document(postId).delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Post deleted successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show()
            }
    }
}
