package com.example.indiannewsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.indiannewsapp.R
import com.example.indiannewsapp.ui.MainActivity
import com.example.indiannewsapp.ui.viewmodels.NewsViewModel
import kotlinx.android.synthetic.main.fragment_saved_article.*


class SavedArticleFragment : Fragment(R.layout.fragment_saved_article) {
    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).vm
        val article = args.article
        savedArticleTitle.text = article.title
        savedArticleContent.text = article.content
    }

}