package com.example.snapstock1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation_menu)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Загрузка начального фрагмента (Home)
        loadFragment(ProfileFragment())

        // Обработка выбора пунктов навигации
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                /*R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_discover -> loadFragment(DiscoverFragment())
                R.id.nav_add -> loadFragment(AddArticleFragment())
                R.id.nav_my_articles -> loadFragment(MyArticlesFragment())*/
                R.id.nav_profile -> loadFragment(ProfileFragment())
                else -> false
            }
        }
    }

    // Метод для загрузки фрагмента
    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
        return true
    }
}
