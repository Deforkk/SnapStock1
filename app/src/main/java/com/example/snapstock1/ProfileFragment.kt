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
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var userManager: UserManager
    private lateinit var userEmail: String
    private lateinit var userId: String

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

        // описание прфиля
        userManager.getUserBiography(userId) { biography ->
            biographyEditText.setText(biography ?: "")
        }

        userManager.getUserDetails(userId) { user: User? ->
            usernameTextView.text = user?.username ?: "No username"
            Glide.with(this)
                .load(user?.avatarUrl ?: R.drawable.ic_default_avatar)
                .into(profileImageView)
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