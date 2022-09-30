package com.example.indiannewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.indiannewsapp.R
import com.example.indiannewsapp.ui.MainActivity
import com.example.indiannewsapp.ui.adapters.NewsAdapter
import com.example.indiannewsapp.ui.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.indiannewsapp.ui.utils.Resource
import com.example.indiannewsapp.ui.viewmodels.NewsViewModel
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {
    private lateinit var vm:NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    val TAG = "Breaking News Fragment"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm = (activity as MainActivity).vm
        setupRecyclerView()
        println("======================================CLICKED===========================================")
        newsAdapter.setOnItemClickListener {
            val bundle  = Bundle().apply{
                 putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment,bundle)
        }

        vm.breakingNews.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    hideProgressBar()
                    it.data?.let{
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages =it.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = vm.breakingNewsPage == totalPages
                        if(isLastPage) {
                            rvbreakingnews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let{
                        Toast.makeText(activity, "An Error Ocurred :$it", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }

            }
        })
    }

    private fun hideProgressBar() {
        pgbar.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressBar() {
        pgbar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling= false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount>= QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                vm.getBreakingNews("in")
                isScrolling = false
            }

        }
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
        rvbreakingnews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}