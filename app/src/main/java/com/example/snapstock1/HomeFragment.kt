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

        // Настройка кнопок сортировки
        binding.sortByDateIcon.setOnClickListener {
            loadPosts(orderBy = "created_at")
        }

        binding.sortByLikesIcon.setOnClickListener {
            loadPosts(orderBy = "likes")
        }
        loadPosts()

        // Настройка нижней панели навигации через метод из родительского класса
        setupBottomNavigation(binding.bottomNavigation)
        
        return binding.root
    }

    private fun loadPosts(orderBy: String = "created_at") {
        if (orderBy == "likes") {
            // Сортировка по лайкам
            loadPostsByLikes()
        } else {
            // Сортировка по дате
            firestore.collection("pins")
                .orderBy(orderBy) // Сортировка по полю: "created_at"
                .get()
                .addOnSuccessListener { documents ->
                    posts.clear()
                    writers.clear()

                    val userIds = mutableSetOf<String>()

                    for (doc in documents) {
                        posts.add(doc)
                        val userId = doc.getString("user_id")
                        if (!userId.isNullOrEmpty()) {
                            userIds.add(userId)
                        }
                    }

                    // Загружаем авторов
                    loadWriters(userIds)

                    // Обновляем адаптер постов
                    binding.recyclerViewPosts.adapter?.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    // Обработка ошибки
                }
        }
    }

    private fun loadPostsByLikes() {
        // Сначала получаем список всех постов
        firestore.collection("pins")
            .get()
            .addOnSuccessListener { postsDocuments ->
                val postLikesCount = mutableListOf<Pair<QueryDocumentSnapshot, Long>>()

                // Для каждого поста подсчитываем количество лайков
                for (postDoc in postsDocuments) {
                    val postId = postDoc.id
                    firestore.collection("likes")
                        .whereEqualTo("postId", postId) // Получаем все лайки для этого поста
                        .get()
                        .addOnSuccessListener { likesDocs ->
                            // Количество лайков для этого поста
                            val likesCount = likesDocs.size().toLong()

                            // Сохраняем пару (пост, количество лайков)
                            postLikesCount.add(Pair(postDoc, likesCount))

                            // Когда все посты обработаны
                            if (postLikesCount.size == postsDocuments.size()) {
                                // Сортируем по количеству лайков
                                postLikesCount.sortByDescending { it.second }

                                // Обновляем список постов
                                posts.clear()
                                for (pair in postLikesCount) {
                                    posts.add(pair.first)
                                }

                                // Загружаем авторов
                                loadWritersFromPosts(posts)

                                // Обновляем адаптер
                                binding.recyclerViewPosts.adapter?.notifyDataSetChanged()
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                // Обработка ошибки
            }
    }

    private fun loadWritersFromPosts(postsList: List<QueryDocumentSnapshot>) {
        val userIds = mutableSetOf<String>()
        for (post in postsList) {
            val userId = post.getString("user_id")
            if (!userId.isNullOrEmpty()) {
                userIds.add(userId)
            }
        }
        loadWriters(userIds)
    }
    private fun loadWriters(userIds: Set<String>) {
        if (userIds.isNotEmpty()) { // Проверка, что userIds не пустой
            firestore.collection("users")
                .whereIn(FieldPath.documentId(), userIds.toList()) // Если список не пуст, выполняем запрос
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
        } else {
            // Если userIds пустой, просто не делаем запрос
            binding.recyclerViewTopWriters.adapter = TopWritersAdapter(writers.toList())
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
