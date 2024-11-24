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

class HomeFragment : Fragment() {

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

        // Настройка нижней панели навигации
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add -> {
                    findNavController().navigate(R.id.action_profileFragment_to_addArticleFragment)
                    true
                }
                R.id.nav_discover -> {
                    findNavController().navigate(R.id.action_homeFragment_to_discoverFragment)
                    true
                }
                R.id.nav_profile -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    true
                }
                R.id.nav_my_articles -> {
                    findNavController().navigate(R.id.action_profileFragment_to_myArticlesFragment)
                    true
                }
                R.id.nav_home -> {
                    true
                }
                else -> false
            }
        }
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
