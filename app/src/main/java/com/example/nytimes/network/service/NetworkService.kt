package com.example.nytimes.network.service

import com.example.nytimes.model.ArticleResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkService {

    companion object {
        const val BASE_PATH = "svc/mostpopular/v2"
    }

    @GET("$BASE_PATH/{section}/{period}.json")
    suspend fun getArticles(
        @Path("section") section: String,
        @Path("period") period: String,
        @Query("api-key") api: String
    ): ArticleResult
}