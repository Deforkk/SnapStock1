package com.example.snapstock1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapstock1.databinding.FragmentMyArticlesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class MyArticlesFragment : BottomNavigationFragment() {

    private lateinit var binding: FragmentMyArticlesBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val posts = mutableListOf<QueryDocumentSnapshot>()
    private lateinit var postAdapter: PostAdapter  // Адаптер для RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализация binding
        binding = FragmentMyArticlesBinding.inflate(inflater, container, false)

        // Настройка RecyclerView
        binding.recyclerViewMyArticles.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(posts, ::onPostClick)
        binding.recyclerViewMyArticles.adapter = postAdapter

        // Загрузка постов
        loadUserPosts()

        // Настройка нижней панели навигации через метод из родительского класса
        setupBottomNavigation(binding.bottomNavigation)

        return binding.root
    }

    private fun loadUserPosts() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("pins")
                .whereEqualTo("user_id", userId)  // Загрузка только постов текущего пользователя
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(context, "No posts found", Toast.LENGTH_SHORT).show()
                    } else {
                        posts.clear()
                        for (document in documents) {
                            posts.add(document)
                        }
                        postAdapter.notifyDataSetChanged()  // Уведомление адаптера о данных
                        Log.d("Firestore", "Posts loaded: ${documents.size()}")  // Логирование количества постов
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to load posts", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onPostClick(post: QueryDocumentSnapshot) {
        val bundle = Bundle()
        bundle.putString("postId", post.id)  // Сохраняем post ID в Bundle

        // Навигация с передачей данных через Bundle
        findNavController().navigate(R.id.action_myArticlesFragment_to_editPostFragment, bundle)
    }
}

