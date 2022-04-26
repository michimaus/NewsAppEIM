package com.example.newsappeim.repositories

import com.example.newsappeim.services.NewsService

class NewsRepository constructor(private val newsService: NewsService) {
    suspend fun getLatest() = newsService.getLatest()
}
