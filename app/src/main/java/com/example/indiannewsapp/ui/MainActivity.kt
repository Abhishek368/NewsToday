package com.example.indiannewsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
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

        //Customizing Action Bar
//        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
//        supportActionBar?.setCustomView(R.layout.custom_action_bar)

        //Initializing Viewmodel for all the fragments
        val repository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = NewsViewModelFactory(application, repository)
        vm = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)

        //Setting up bottom navigation
        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

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