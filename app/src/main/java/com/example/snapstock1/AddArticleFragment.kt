package com.example.snapstock1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.snapstock1.databinding.FragmentAddArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddArticleFragment : Fragment() {

    private lateinit var binding: FragmentAddArticleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddArticleBinding.inflate(inflater, container, false)

        // Обработчик кнопки для сохранения поста
        binding.btnSavePost.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val description = binding.etDescription.text.toString()
            val imageUrl = binding.etImageUrl.text.toString()
            val tags = binding.etTags.text.toString().split(",").map { it.trim() }

            if (title.isEmpty() || description.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            savePost(title, description, imageUrl, tags)
        }

        // Обработчик кнопки закрытия
        binding.icClose.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun savePost(title: String, description: String, imageUrl: String, tags: List<String>) {
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(context, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        val post = hashMapOf(
            "title" to title,
            "description" to description,
            "image_url" to imageUrl,
            "tags" to tags,
            "user_id" to userId,
            "created_at" to FieldValue.serverTimestamp()
        )

        firestore.collection("pins").add(post)
            .addOnSuccessListener {
                Toast.makeText(context, "Post added successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addArticleFragment_to_homeFragment)
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add post!", Toast.LENGTH_SHORT).show()
            }
    }
}
