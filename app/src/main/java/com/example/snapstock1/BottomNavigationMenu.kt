package com.example.snapstock1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.snapstock1.databinding.ActivityBottomNavigationMenuBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationMenu : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*// Загрузка начального фрагмента (Home)
        loadFragment(HomeFragment())

        // Обработка выбора пунктов навигации
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_discover -> {
                    loadFragment(DiscoverFragment())
                    true
                }
                R.id.nav_add -> {
                    loadFragment(AddArticleFragment())
                    true
                }
                R.id.nav_my_articles -> {
                    loadFragment(MyArticlesFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
*/
    }

    // Метод для загрузки фрагментов
   /* private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }*/
}
