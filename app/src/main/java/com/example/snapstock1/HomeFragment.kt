package com.example.snapstock1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapstock1.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class HomeFragment : BottomNavigationFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val posts = mutableListOf<QueryDocumentSnapshot>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewPosts.adapter = SimplePostAdapter(posts)

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
                posts.addAll(documents)
                binding.recyclerViewPosts.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Вывод ошибки в Toast
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
