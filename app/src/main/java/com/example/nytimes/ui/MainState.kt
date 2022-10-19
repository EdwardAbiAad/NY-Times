package com.example.nytimes.ui

import com.example.nytimes.model.Result

sealed class MainState {


    object ShowLoadingState : MainState()

    object HideLoadingState : MainState()

    data class ShowErrorMessage(val errorMessage: String) : MainState()

    data class GetArticle(
        val result: List<Result?>?
    ) : MainState()
}