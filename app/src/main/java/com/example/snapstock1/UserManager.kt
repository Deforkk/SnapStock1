package com.example.snapstock1

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

class UserManager(private val context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Регистрация
    fun registerUser(email: String, password: String, username: String, onComplete: (Boolean, String?) -> Unit) {
        // Регистрируем пользователя с помощью Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            val userData = hashMapOf(
                                "email" to email,
                                "password" to password,
                                "username" to username,
                                "created_at" to System.currentTimeMillis()
                            )
                            firestore.collection("users").document(user.uid).set(userData)
                                .addOnSuccessListener {
                                    onComplete(true, "Registration successful. Please verify your email.")
                                }
                                .addOnFailureListener {
                                    onComplete(false, "Failed to save user data.")
                                }
                        } else {
                            onComplete(false, "Failed to send verification email.")
                        }
                    }
                } else {
                    onComplete(false, task.exception?.message ?: "Registration failed.")
                }
            }
    }

    // Вход пользователя
    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        onComplete(true, "Login successful")
                    } else {
                        auth.signOut() // Выход, если почта не подтверждена
                        onComplete(false, "Please verify your email before logging in.")
                    }
                } else {
                    onComplete(false, task.exception?.message ?: "Login failed.")
                }
            }
    }

    fun getUserDetails(userId: String, onComplete: (User?) -> Unit) {
        val usersRef = firestore.collection("users")
        usersRef.document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val username = document.getString("username")
                    val avatarUrl = document.getString("avatar_url")
                    val email = document.getString("email")
                    onComplete(User(username, avatarUrl, email))
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }
    // Обновление аватарки пользователя
    fun updateUserAvatar(userId: String, avatarUrl: String?, onComplete: (Boolean, String) -> Unit) {
        val userRef = firestore.collection("users").document(userId)

        val updates = if (avatarUrl != null) {
            mapOf("avatar_url" to avatarUrl)
        } else {
            mapOf("avatar_url" to FieldValue.delete()) // Удаление URL
        }

        userRef.update(updates)
            .addOnSuccessListener {
                onComplete(true, if (avatarUrl != null) "Avatar updated successfully." else "Avatar removed successfully.")
            }
            .addOnFailureListener { exception ->
                onComplete(false, "Error updating avatar: ${exception.message}")
            }
    }
    // Получить биографию пользователя
    fun getUserBiography(userId: String, onComplete: (String?) -> Unit) {
        val usersRef = firestore.collection("users")
        usersRef.document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onComplete(document.getString("bio"))
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    // Установить биографию пользователя
    fun updateUserBiography(userId: String, biography: String, onComplete: (Boolean, String) -> Unit) {
        val usersRef = firestore.collection("users")
        // Теперь используем UID пользователя для поиска в базе данных
        usersRef.document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Обновляем биографию пользователя по его UID
                    document.reference.update("bio", biography)
                        .addOnSuccessListener {
                            onComplete(true, "Biography updated successfully.")
                        }
                        .addOnFailureListener { e ->
                            onComplete(false, "Error updating biography: ${e.message}")
                        }
                } else {
                    onComplete(false, "User not found.")
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, "Error finding user: ${e.message}")
            }
    }

    // Смена пароля
    fun changePassword(newPassword: String, onComplete: (Boolean, String) -> Unit) {
        val user = auth.currentUser // Получаем текущего авторизованного пользователя

        if (user != null) {
            // Обновляем пароль через Firebase Authentication
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Если изменение пароля прошло успешно
                        onComplete(true, "Password changed successfully.")
                    } else {
                        // Ошибка при изменении пароля
                        onComplete(false, task.exception?.message ?: "Failed to change password.")
                    }
                }
        } else {
            onComplete(false, "User is not logged in.")
        }
    }

    // Удаление аккаунта
    fun deleteAccount(onComplete: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            // Сначала удаляем данные пользователя из Firestore
            val usersRef = firestore.collection("users")
            usersRef.document(user.uid).delete()
                .addOnSuccessListener {
                    // Теперь удаляем аккаунт из Firebase Authentication
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onComplete(true, "Account deleted successfully.")
                            } else {
                                onComplete(false, "Failed to delete account from Firebase Authentication: ${task.exception?.message}")
                            }
                        }
                }
                .addOnFailureListener { exception ->
                    onComplete(false, "Failed to delete user data from Firestore: ${exception.message}")
                }
        } else {
            onComplete(false, "User is not logged in.")
        }
    }

    //Репорт проблемы
    fun reportProblem(subject: String, description: String, email: String?, onComplete: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        val problemData = hashMapOf(
            "timestamp" to System.currentTimeMillis(),
            "subject" to subject,
            "description" to description,
            "email" to email,
            "userId" to user?.uid
        )

        firestore.collection("reports").add(problemData)
            .addOnSuccessListener {
                onComplete(true, "The issue has been sent successfully.")
            }
            .addOnFailureListener { exception ->
                onComplete(false, "Error sending problem: ${exception.message}")
            }
    }

    fun addLike(postId: String, userId: String, onComplete: (Boolean) -> Unit) {
        val likeData = hashMapOf(
            "postId" to postId,
            "userId" to userId
        )
        firestore.collection("likes").document(postId).set(likeData)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
    fun removeLike(postId: String, userId: String, onComplete: (Boolean) -> Unit) {
        firestore.collection("likes").document(postId).delete()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}
