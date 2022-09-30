package com.example.indiannewsapp.ui.repository

import androidx.lifecycle.LiveData
import com.example.indiannewsapp.ui.api.RetrofitInstance
import com.example.indiannewsapp.ui.db.ArticleDatabase
import com.example.indiannewsapp.ui.models.Article
import com.example.indiannewsapp.ui.models.NewsResponse
import retrofit2.Response

class NewsRepository(val db : ArticleDatabase) {
    suspend fun getBreakingNews(countryCode:String,pageNumber:Int):Response<NewsResponse>{
        return RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)
    }
    suspend fun searchNews(query: String,pageNumber: Int):Response<NewsResponse>{
        return RetrofitInstance.api.searchNews(query,pageNumber)
    }
    suspend fun upsert(article :Article) : Long{
        return db.getArticleDao().upsert(article)
    }

    fun getSavedNews(): LiveData<List<Article>> {
       return db.getArticleDao().getAllArticles()
    }
    suspend fun deleteArticle(article:Article){
        db.getArticleDao().deleteArticle(article)
    }
}