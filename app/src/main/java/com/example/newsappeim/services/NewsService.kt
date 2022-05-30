package com.example.newsappeim.services

import com.example.newsappeim.MainAppActivity
import com.example.newsappeim.data.model.ListOfNewsModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*
import kotlin.collections.HashMap

//Hot news here
//https://newsdata.io/api/1/news?apikey=pub_6783a52d4e171eb4b5981530303223ea168c&country=gb&language=en&category=top

var TAG: String = "NewsService"

interface NewsService {

    @GET("news?apikey=pub_6783a52d4e171eb4b5981530303223ea168c&category=top")
    suspend fun getLatest(@Query("country") address: String): Response<ListOfNewsModel>

    companion object {
        val myMap: MutableMap<String, String> = HashMap<String, String>()
        var countryCode: String = "us"

        var newsService: NewsService? = null
        fun getInstance(): NewsService {
            if (newsService == null) {

//                ROMANIA GOT NO NEWS!!!
                myMap["country"] = "us"
                if (MainAppActivity.countryCode.isNotEmpty()) {
                    myMap["country"] = MainAppActivity.countryCode.lowercase(Locale.getDefault())
                    countryCode = MainAppActivity.countryCode.lowercase(Locale.getDefault())
                }


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