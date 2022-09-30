package com.example.indiannewsapp.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.*
import com.example.indiannewsapp.ui.NewsApplication
import com.example.indiannewsapp.ui.models.Article
import com.example.indiannewsapp.ui.models.NewsResponse
import com.example.indiannewsapp.ui.repository.NewsRepository
import com.example.indiannewsapp.ui.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app: Application, val repository: NewsRepository):AndroidViewModel(app) {
    val breakingNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse:NewsResponse?=null

    val searchNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse:NewsResponse?=null

    init{
        getBreakingNews("in")
    }
    fun getBreakingNews(countryCode:String) {
        viewModelScope.launch {
            safeBreakingNewsCall(countryCode)

        }
    }

    fun searchNews(searchQuery:String){
        viewModelScope.launch {
            safeSearchNewsCall(searchQuery)
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse>{
           if(response.isSuccessful){
               response.body()?.let {
                   breakingNewsPage++
                   if(breakingNewsResponse == null){
                       breakingNewsResponse = it
                   }
                   else{
                       val oldArticles = breakingNewsResponse?.articles
                       val newArticles = it.articles
                       oldArticles?.addAll(newArticles)
                   }

                   return Resource.Success(breakingNewsResponse?:it)
               }
           }
        return Resource.Error(response.message())
    }
    private fun handleSearchNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let {
                searchNewsPage++
                if(searchNewsResponse == null){
                    searchNewsResponse = it
                }
                else{
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = it.articles
                    oldArticles?.addAll(newArticles)
                }

                return Resource.Success(searchNewsResponse?:it)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article)=
        viewModelScope.launch {
            repository.upsert(article)
        }

    fun getSavedNews() = repository.getSavedNews()
    fun deleteArticle(article: Article){
        viewModelScope.launch {
            repository.deleteArticle(article)
        }
    }


    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = repository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }
            else{
                searchNews.postValue(Resource.Error("NO internet connection"))
            }

        }
        catch (t:Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                val response = repository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }
            else{
                breakingNews.postValue(Resource.Error("NO internet connection"))
            }

        }
        catch (t:Throwable){
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

     fun hasInternetConnection():Boolean{
        val connectivityManager = getApplication<NewsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) ->true
                capabilities.hasTransport(TRANSPORT_CELLULAR) ->true
                capabilities.hasTransport(TRANSPORT_ETHERNET) ->true
                else -> false
            }
        }
        else{
            connectivityManager.activeNetworkInfo?.run{
                return when(type){
                    TYPE_WIFI ->true
                    TYPE_MOBILE ->true
                    TYPE_ETHERNET ->true
                    else ->false
                }
            }
        }
        return false
    }

}