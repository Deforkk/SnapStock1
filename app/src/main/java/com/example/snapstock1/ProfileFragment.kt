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

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                /*R.id.nav_home -> {
                    findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
                    true
                }
                R.id.nav_discover -> {
                    findNavController().navigate(R.id.action_profileFragment_to_discoverFragment)
                    true
                }
                R.id.nav_add -> {
                    findNavController().navigate(R.id.action_profileFragment_to_addArticleFragment)
                    true
                }
                R.id.nav_my_articles -> {
                    findNavController().navigate(R.id.action_profileFragment_to_myArticlesFragment)
                    true
                }*/
                R.id.nav_profile -> {
                    true
                }
                else -> false
            }
        }
        // описание прфиля
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

        view.findViewById<Button>(R.id.btnChangePassword).setOnClickListener {
            showChangePasswordDialog()
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

        view.findViewById<Button>(R.id.btnDeleteAccount).setOnClickListener {
            userManager.deleteAccount { success, message ->
                if (success) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut() // Логаут
                    findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // логаут
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Логаут
            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
        }

        return view
    }
    private fun showAvatarUpdateDialog(profileImageView: ImageView) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_avatar, null)
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
    // Показать диалог для смены пароля
    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.etNewPassword)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Confirm") { _, _ ->
                val newPassword = passwordEditText.text.toString()
                if (newPassword.isNotBlank()) {
                    userManager.changePassword(newPassword) { success, message ->
                        if (success) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun hashPassword(password: String): String {
        return password // Реализуйте хеширование пароля
    }
}