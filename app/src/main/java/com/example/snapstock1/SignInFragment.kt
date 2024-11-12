package com.example.snapstock1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInFragment : Fragment() {
    private lateinit var userManager: UserManager
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_sign_in_fragment, container, false)
        userManager = UserManager(requireContext())
        auth = FirebaseAuth.getInstance() // Инициализируем FirebaseAuth

        // Переход на экран регистрации
        view.findViewById<TextView>(R.id.tvSignUp).setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        view.findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.etEmail).text.toString()
            val password = view.findViewById<EditText>(R.id.etPassword).text.toString()
            val passwordHash = hashPassword(password) // Захешируйте пароль

            userManager.loginUser(email, password) { success, message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    findNavController().navigate(R.id.action_signInFragment_to_profileFragment)
                }
            }
        }

        // Обработка "Forgot Password?"
        view.findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            showForgotPasswordDialog() //диалог для сброса пароля
        }

        return view
    }

    private fun hashPassword(password: String): String {
        return password
    }

    // Показать диалоговое окно для ввода email при сбросе пароля
    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Reset Password")

        val input = EditText(requireContext())
        input.hint = "Enter your email"
        builder.setView(input)

        builder.setPositiveButton("Reset") { _, _ ->
            val email = input.text.toString()
            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    // Отправка письма для сброса пароля
    // Отправка письма для сброса пароля
    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Password reset email sent", Toast.LENGTH_SHORT).show()

                    // Теперь обновим пароль в базе данных Firestore
                    updatePasswordInFirestore(email)
                } else {
                    Toast.makeText(requireContext(), "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Обновляем пароль в базе данных Firestore
    private fun updatePasswordInFirestore(email: String) {
        val usersRef = FirebaseFirestore.getInstance().collection("users")
        usersRef.whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Получаем данные пользователя из Firestore
                    val userDoc = documents.documents[0]
                    // Обновляем пароль в Firestore
                    userDoc.reference.update("password", auth.currentUser?.email) // Например, сохраняем новый пароль
                } else {
                    Toast.makeText(requireContext(), "User not found in Firestore", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error updating password in Firestore", Toast.LENGTH_SHORT).show()
            }
    }

}
