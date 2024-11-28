package com.example.snapstock1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapstock1.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class HomeFragment : BottomNavigationFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val posts = mutableListOf<QueryDocumentSnapshot>()
    private val writers = mutableSetOf<Writer>() // Список уникальных авторов

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Настройка Top Writers RecyclerView
        binding.recyclerViewTopWriters.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Настройка Posts RecyclerView
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPosts.adapter = SimplePostAdapter(posts, firestore)

        loadPosts()

        // Настройка нижней панели навигации через метод из родительского класса
        setupBottomNavigation(binding.bottomNavigation)
        
        return binding.root
    }

    private fun loadPosts() {
        firestore.collection("pins")
            .get()
            .addOnSuccessListener { documents ->
                posts.clear()
                writers.clear()

                val userIds = mutableSetOf<String>() // Список уникальных user_id

                for (doc in documents) {
                    posts.add(doc)
                    val userId = doc.getString("user_id")
                    if (!userId.isNullOrEmpty()) {
                        userIds.add(userId) // Собираем уникальные user_id
                    }
                }

                // Загружаем данные о пользователях
                loadWriters(userIds)

                // Обновляем адаптер постов
                binding.recyclerViewPosts.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Обработка ошибки
            }
    }

    private fun loadWriters(userIds: Set<String>) {
        firestore.collection("users")
            .whereIn(FieldPath.documentId(), userIds.toList())
            .get()
            .addOnSuccessListener { userDocuments ->
                for (userDoc in userDocuments) {
                    val username = userDoc.getString("username") ?: "Unknown"
                    val avatarUrl = userDoc.getString("avatar_url")
                    writers.add(Writer(username, avatarUrl))
                }

                // Обновляем адаптер Top Writers
                binding.recyclerViewTopWriters.adapter = TopWritersAdapter(writers.toList())
            }
            .addOnFailureListener { e ->
                // Обработка ошибки
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
