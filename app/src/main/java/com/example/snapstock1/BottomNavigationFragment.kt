package com.example.snapstock1

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BottomNavigationFragment : Fragment() {

    fun setupBottomNavigation(bottomNavigation: BottomNavigationView) {
        bottomNavigation.setOnItemSelectedListener { item ->
            val currentDestination = findNavController().currentDestination?.id
            when (item.itemId) {
                R.id.nav_add -> {
                    if (currentDestination != R.id.addArticleFragment) {
                        findNavController().navigate(R.id.action_global_addArticleFragment)
                    }
                    true
                }
                R.id.nav_my_articles -> {
                    if (currentDestination != R.id.myArticlesFragment) {
                        findNavController().navigate(R.id.action_global_myArticlesFragment)
                    }
                    true
                }
                R.id.nav_home -> {
                    if (currentDestination != R.id.homeFragment) {
                        findNavController().navigate(R.id.action_global_homeFragment)
                    }
                    true
                }
                R.id.nav_discover -> {
                    if (currentDestination != R.id.discoverFragment) {
                        findNavController().navigate(R.id.action_global_discoverFragment)
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (currentDestination != R.id.profileFragment) {
                        findNavController().navigate(R.id.action_global_profileFragment)
                    }
                    true
                }
                else -> false
            }
        }

        // Синхронизация текущего выбранного элемента
        val currentDestinationId = findNavController().currentDestination?.id
        when (currentDestinationId) {
            R.id.addArticleFragment -> bottomNavigation.selectedItemId = R.id.nav_add
            R.id.myArticlesFragment -> bottomNavigation.selectedItemId = R.id.nav_my_articles
            R.id.homeFragment -> bottomNavigation.selectedItemId = R.id.nav_home
            R.id.discoverFragment -> bottomNavigation.selectedItemId = R.id.nav_discover
            R.id.profileFragment -> bottomNavigation.selectedItemId = R.id.nav_profile
        }
    }
}
