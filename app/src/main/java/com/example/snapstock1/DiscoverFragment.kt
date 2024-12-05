package com.example.snapstock1

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapstock1.databinding.FragmentDiscoverBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class DiscoverFragment : BottomNavigationFragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val posts = mutableListOf<QueryDocumentSnapshot>()
    private val filteredPosts = mutableListOf<QueryDocumentSnapshot>()
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)

        // Настройка RecyclerView
        binding.recyclerViewDiscover.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(filteredPosts) { /* Обработчик нажатий */ }
        binding.recyclerViewDiscover.adapter = postAdapter

        setupBottomNavigation(binding.bottomNavigation)

        // Загружаем все посты при старте
        loadAllPosts()

        // Настраиваем поиск
        setupSearchView()

        return binding.root
    }

    private fun loadAllPosts() {
        firestore.collection("pins")
            .get()
            .addOnSuccessListener { documents ->
                posts.clear()
                filteredPosts.clear()
                for (document in documents) {
                    posts.add(document)
                    Log.d("DiscoverFragment", "Loaded post: ${document.getString("title")}")
                }
                filteredPosts.addAll(posts)
                postAdapter.notifyDataSetChanged()
                Log.d("DiscoverFragment", "Total posts loaded: ${posts.size}")
            }
            .addOnFailureListener { e ->
                Log.e("DiscoverFragment", "Error loading posts", e)
            }
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterPosts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText)
                return true
            }
        })
    }

    private fun filterPosts(query: String?) {
        filteredPosts.clear()
        if (TextUtils.isEmpty(query)) {
            filteredPosts.addAll(posts)
            Log.d("DiscoverFragment", "Empty query, showing all posts")
        } else {
            val lowerCaseQuery = query!!.lowercase()
            for (post in posts) {
                val title = post.getString("title")?.lowercase() ?: ""
                val description = post.getString("description")?.lowercase() ?: ""
                if (title.contains(lowerCaseQuery) || description.contains(lowerCaseQuery)) {
                    filteredPosts.add(post)
                    Log.d("DiscoverFragment", "Post matches query: $title")
                }
            }
        }
        postAdapter.notifyDataSetChanged()
        Log.d("DiscoverFragment", "Filtered posts count: ${filteredPosts.size}")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
