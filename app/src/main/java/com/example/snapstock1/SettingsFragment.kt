package com.example.snapstock1

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider

class SettingsFragment : Fragment() {

    private lateinit var userManager: UserManager
    private lateinit var userId: String
    private lateinit var userEmail: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        userManager = UserManager(requireContext())
        val reportProblemButton = view.findViewById<Button>(R.id.ReportProblem)
        reportProblemButton.setOnClickListener {
            showReportProblemDialog()
        }

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown Email"

        // Отображение текущей почты
        view.findViewById<TextView>(R.id.tvCurrentEmail).text = "Current Email: $userEmail"

        // Смена почты
        view.findViewById<Button>(R.id.btnChangeEmail).setOnClickListener {
            showChangeEmailDialog()
        }
        // Смена пароля
        view.findViewById<Button>(R.id.btnChangePassword).setOnClickListener {
            showChangePasswordDialog()
        }

        // Удаление аккаунта
        view.findViewById<Button>(R.id.btnDeleteAccount).setOnClickListener {
            userManager.deleteAccount { success, message ->
                if (success) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut() // Логаут
                    findNavController().navigate(R.id.action_settingsFragment_to_signInFragment)
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Логаут
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_settingsFragment_to_signInFragment)
        }

        return view
    }

    private fun showChangeEmailDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_email, null)
        val newEmailEditText = dialogView.findViewById<EditText>(R.id.etNewEmail)

        AlertDialog.Builder(requireContext())
            .setTitle("Change Email")
            .setView(dialogView)
            .setPositiveButton("Send Verification") { _, _ ->
                val newEmail = newEmailEditText.text.toString()
                if (newEmail.isNotBlank()) {
                    // Проверка и отправка email на подтверждение
                    sendVerificationEmail(newEmail)
                } else {
                    Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun sendVerificationEmail(newEmail: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val currentUser = firebaseUser ?: return

        // Показать диалог для ввода пароля и реаутентификации
        val passwordEditText = EditText(context)
        AlertDialog.Builder(requireContext())
            .setTitle("Enter your current password")
            .setView(passwordEditText)
            .setPositiveButton("Reauthenticate") { _, _ ->
                val currentPassword = passwordEditText.text.toString()
                if (currentPassword.isNotEmpty()) {
                    val credentials = EmailAuthProvider.getCredential(currentUser.email ?: "", currentPassword)
                    currentUser.reauthenticate(credentials)
                        .addOnCompleteListener { reauthTask ->
                            if (reauthTask.isSuccessful) {
                                // Если реаутентификация прошла успешно, обновим email
                                currentUser.verifyBeforeUpdateEmail(newEmail)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Почта успешно обновлена и письмо отправлено
                                            currentUser.sendEmailVerification().addOnCompleteListener { emailTask ->
                                                if (emailTask.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "Verification email sent to $newEmail",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to send verification email.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, "Failed to update email.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(context, "Reauthentication failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Показать диалог для смены пароля
    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.etNewPassword)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
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



    private fun showReportProblemDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.report_problem_dialog, null)
        val subjectEditText = dialogView.findViewById<EditText>(R.id.subjectEditText)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.descriptionEditText)
        val emailEditText = dialogView.findViewById<EditText>(R.id.emailEditText)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Report a problem")
            .setView(dialogView)
            .setPositiveButton("Send") { _, _ ->
                val subject = subjectEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val email = emailEditText.text.toString()

                userManager.reportProblem(subject, description, email) { success, message ->
                    if (success) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error sending report: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

}
