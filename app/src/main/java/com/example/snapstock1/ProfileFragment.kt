package com.example.snapstock1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.example.snapstock1.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class ProfileFragment : BottomNavigationFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val posts = mutableListOf<QueryDocumentSnapshot>()

    private lateinit var userManager: UserManager
    private lateinit var userEmail: String
    private lateinit var userId: String
    private val defaultAvatarUrl = R.drawable.ic_default_avatar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        userManager = UserManager(requireContext())

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        userEmail = arguments?.getString("userEmail") ?: ""

        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPosts.adapter = SimplePostAdapter(posts, firestore)

        setupUI()
        loadUserProfile()

        // Настройка нижней панели навигации
        setupBottomNavigation(binding.bottomNavigation)

        firestore.collection("favorite")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val postIds = documents.documents.mapNotNull { it.getString("postId") }

                if (postIds.isNotEmpty()) {
                    firestore.collection("pins")
                        .whereIn(FieldPath.documentId(), postIds)
                        .get()
                        .addOnSuccessListener { postDocuments ->
                            filterAndProcessPosts(postDocuments)
                        }
                        .addOnFailureListener { e ->
                            // Обработка ошибки
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                // Обработка ошибки
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }

        return binding.root
    }

    private fun filterAndProcessPosts(documents: com.google.firebase.firestore.QuerySnapshot) {
        posts.clear()

        val userIds = mutableSetOf<String>()

        for (doc in documents) {
            posts.add(doc)
            val userId = doc.getString("user_id")
            if (!userId.isNullOrEmpty()) {
                userIds.add(userId)
            }
        }

        // Обновляем адаптер постов
        binding.recyclerViewPosts.adapter?.notifyDataSetChanged()
    }

    private fun setupUI() {
        binding.icSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        binding.profileImageView.setOnClickListener {
            showAvatarUpdateDialog(binding.profileImageView)
        }

        binding.btnSaveBiography.setOnClickListener {
            val newBiography = binding.etBiography.text.toString()
            if (newBiography.isNotEmpty()) {
                userManager.updateUserBiography(userId, newBiography) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Biography cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUserProfile() {
        // Загрузка биографии
        userManager.getUserBiography(userId) { biography ->
            binding.etBiography.setText(biography ?: "")
        }

        // Загрузка данных пользователя
        userManager.getUserDetails(userId) { user ->
            binding.usernameTextView.text = user?.username ?: "No username"
            binding.userEmailTextView.text = user?.email ?: "No email" // Отображаем email
            Glide.with(this)
                .load(user?.avatarUrl ?: defaultAvatarUrl)
                .into(binding.profileImageView)
        }
    }

    private fun showAvatarUpdateDialog(profileImageView: ImageView) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_avatar, null)
        val avatarUrlEditText = dialogView.findViewById<EditText>(R.id.etAvatarUrl)

        AlertDialog.Builder(requireContext())
            .setTitle("Update Avatar")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newAvatarUrl = avatarUrlEditText.text.toString()
                if (newAvatarUrl.isNotEmpty()) {
                    userManager.updateUserAvatar(userId, newAvatarUrl) { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        if (success) {
                            Glide.with(this)
                                .load(newAvatarUrl)
                                .into(profileImageView)
                        }
                    }
                } else {
                    Toast.makeText(context, "Avatar URL cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Remove Avatar") { _, _ ->
                userManager.updateUserAvatar(userId, null) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        Glide.with(this)
                            .load(defaultAvatarUrl)
                            .into(profileImageView)
                    }
                }
            }
            .setNeutralButton("Cancel", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}