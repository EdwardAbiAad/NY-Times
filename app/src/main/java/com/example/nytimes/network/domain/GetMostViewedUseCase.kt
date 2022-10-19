package com.example.nytimes.network.domain

import com.example.nytimes.model.ArticleResult
import com.example.nytimes.network.service.NetworkService
import com.example.nytimes.utils.AppModel
import javax.inject.Inject

class GetMostViewedUseCase @Inject constructor(
    private val networkService: NetworkService
) {
    suspend fun execute(period: Int): ArticleResult {
        return networkService.getArticles("viewed",period.toString(),AppModel.appKey)
    }
}