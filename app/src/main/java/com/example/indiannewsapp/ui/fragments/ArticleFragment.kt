package com.example.indiannewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.indiannewsapp.R
import com.example.indiannewsapp.ui.MainActivity
import com.example.indiannewsapp.ui.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).vm
        val article = args.article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
            webChromeClient = WebChromeClient()
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.pluginState = WebSettings.PluginState.ON
        }
        floatingActionButton2.setOnClickListener {
            if(article.author == null){
                article.author = "USER"
            }
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}