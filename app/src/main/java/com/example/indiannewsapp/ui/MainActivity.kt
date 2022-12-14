package com.example.indiannewsapp.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.indiannewsapp.R
import com.example.indiannewsapp.ui.db.ArticleDatabase
import com.example.indiannewsapp.ui.fragments.SearchNewsFragment
import com.example.indiannewsapp.ui.repository.NewsRepository
import com.example.indiannewsapp.ui.viewmodels.NewsViewModel
import com.example.indiannewsapp.ui.viewmodels.NewsViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var vm:NewsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this,R.color.cardview_dark_background)
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
//        Customizing Action Bar
        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setCustomView(R.layout.custom_action_bar)
        supportActionBar?.setBackgroundDrawable(colorDrawable)

        //Initializing Viewmodel for all the fragments
        val repository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelFactory(application, repository)
        vm = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)

        //Setting up bottom navigation
        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

        newsNavHostFragment.findNavController().addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.searchNewsFragment,R.id.breakingNewsFragment,R.id.savedNewsFragment -> bottomNavigationView.visibility =
                    View.VISIBLE

                else -> bottomNavigationView.visibility=View.GONE
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == R.id.searchNewsFragment){
            val fragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment)
            fragment?.findNavController()?.navigate(R.id.searchNewsFragment)
        }
        return super.onOptionsItemSelected(item)
    }
}