package com.example.newsappeim.screens.main_app_ui.hot_news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsappeim.repositories.NewsRepository
import com.example.newsappeim.screens.main_app_ui.NewsListViewModel

class HotNewsViewModelFactory constructor(private val repository: NewsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(NewsListViewModel::class.java)) {
            NewsListViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("HotNewsViewModel Not Found")
        }
    }
}