package com.example.nytimes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nytimes.network.domain.GetMostViewedUseCase
import com.example.nytimes.utils.ErrorUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMostViewedUseCase: GetMostViewedUseCase
) : ViewModel() {

    private val _state = MutableSharedFlow<MainState>()
    val state: SharedFlow<MainState> = _state


    fun getMostViewedArticles(period: Int) {
        viewModelScope.launch {
            _state.emit(MainState.ShowLoadingState)
            try {
                val res = getMostViewedUseCase.execute(period)
                _state.emit(MainState.GetArticle(res.results))
            } catch (e: Exception) {
                _state.emit(MainState.ShowErrorMessage(ErrorUtils.getErrorMessage(e)))
            }
            _state.emit(MainState.HideLoadingState)

        }

    }


}