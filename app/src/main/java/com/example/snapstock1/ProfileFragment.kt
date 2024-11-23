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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileFragment : Fragment() {

    private lateinit var userManager: UserManager
    private lateinit var userEmail: String
    private lateinit var userId: String
    private val defaultAvatarUrl = R.drawable.ic_default_avatar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        userManager = UserManager(requireContext())

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        userEmail = arguments?.getString("userEmail") ?: ""


        val biographyEditText = view.findViewById<EditText>(R.id.etBiography)
        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        // Настройка нижней панели навигации
        val bottomNavigation = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Переход к настройкам
        view.findViewById<ImageView>(R.id.icSettings).setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add -> {
                    findNavController().navigate(R.id.action_profileFragment_to_addArticleFragment)
                    true
                }
                R.id.nav_my_articles -> {
                    findNavController().navigate(R.id.action_profileFragment_to_myArticlesFragment)
                    true
                }
                R.id.nav_home -> {
                    findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
                    true
                }
                R.id.nav_discover -> {
                    findNavController().navigate(R.id.action_profileFragment_to_discoverFragment)
                    true
                }
                R.id.nav_profile -> {
                    true
                }

                else -> false
            }
        }
        // описание прoфиля
        userManager.getUserBiography(userId) { biography ->
            biographyEditText.setText(biography ?: "")
        }

        userManager.getUserDetails(userId) { user: User? ->
            usernameTextView.text = user?.username ?: "No username"
            Glide.with(this)
                .load(user?.avatarUrl ?: defaultAvatarUrl)
                .into(profileImageView)
        }
        // Открываем диалог для загрузки новой аватарки
        profileImageView.setOnClickListener {
            showAvatarUpdateDialog(profileImageView)
        }

        // Кнопка для сохранения биографии
        view.findViewById<Button>(R.id.btnSaveBiography).setOnClickListener {
            val newBiography = biographyEditText.text.toString()
            if (newBiography.isNotEmpty()) {
                userManager.updateUserBiography(userId, newBiography) { success, message ->
                    if (success) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Biography cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        return view
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

    private fun hashPassword(password: String): String {
        return password // Реализуйте хеширование пароля
    }
}