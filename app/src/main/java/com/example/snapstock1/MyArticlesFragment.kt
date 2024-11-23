package com.example.snapstock1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MyArticlesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_articles, container, false)

        // Настройка нижней панели навигации
        val bottomNavigation = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                /*
                R.id.nav_discover -> {
                    findNavController().navigate(R.id.action_profileFragment_to_discoverFragment)
                    true
                }
                R.id.nav_add -> {
                    findNavController().navigate(R.id.action_profileFragment_to_addArticleFragment)
                    true
                }
                R.id.nav_my_articles -> {
                    findNavController().navigate(R.id.action_profileFragment_to_myArticlesFragment)
                    true
                }*/
                R.id.nav_home -> {
                    findNavController().navigate(R.id.action_myArticlesFragment_to_homeFragment)
                    true
                }
                R.id.nav_profile -> {
                    findNavController().navigate(R.id.action_myArticlesFragment_to_profileFragment)
                    true
                }

                else -> false
            }
        }

        return view
    }
}
