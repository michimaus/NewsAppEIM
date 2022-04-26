package com.example.newsappeim.services


import android.util.Log
import com.example.newsappeim.data.model.ListOfNewsModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

//Hot news here
//https://newsdata.io/api/1/news?apikey=pub_6783a52d4e171eb4b5981530303223ea168c&country=gb&language=en&category=top

var TAG: String = "NewsService"

interface NewsService {

    @GET("news?apikey=pub_6783a52d4e171eb4b5981530303223ea168c&country=gb&language=en&category=top")
    suspend fun getLatest(): Response<ListOfNewsModel>
//    suspend fun getLatest(): Call<List<NewsModel>>
//    suspend fun getLatest(): Response<List<Any>>

    companion object {
        var newsService: NewsService? = null
        fun getInstance(): NewsService {
            if (newsService == null) {

                Log.wtf(TAG, "we trying...")

                val retrofit = Retrofit
                    .Builder()
                    .baseUrl("https://newsdata.io/api/1/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                newsService = retrofit.create(NewsService::class.java)
            }
            return newsService!!
        }

    }
}