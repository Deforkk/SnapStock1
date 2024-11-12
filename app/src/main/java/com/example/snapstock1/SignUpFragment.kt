package com.example.snapstock1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SignUpFragment : Fragment() {
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_sign_up_fragment, container, false)
        userManager = UserManager(requireContext())

        view.findViewById<Button>(R.id.btnSignUp).setOnClickListener {
            val email = view.findViewById<EditText>(R.id.etEmail).text.toString()
            val password = view.findViewById<EditText>(R.id.etPassword).text.toString()
            val username = view.findViewById<EditText>(R.id.etUsername).text.toString()
            val passwordHash = hashPassword(password)

            userManager.registerUser(email, password, username) { success, message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
                }
            }
        }

        return view
    }

    private fun hashPassword(password: String): String {
        return password // временно возвращаем пароль как есть, для простоты
    }
}
