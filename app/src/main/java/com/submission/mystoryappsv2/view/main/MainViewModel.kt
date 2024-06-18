package com.submission.mystoryappsv2.view.main


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.submission.mystoryappsv2.data.remote.Story
import com.submission.mystoryappsv2.data.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    private var currentToken: String? = null
    private val _storiesFlow = MutableStateFlow<PagingData<Story>>(PagingData.empty())
    val storiesFlow: StateFlow<PagingData<Story>> = _storiesFlow
    fun getStoriesFlow(token: String): Flow<PagingData<Story>> {
        return repository.getStories(token).cachedIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to logout", e)
            }
        }
    }
    suspend fun refreshStories() {
        currentToken?.let { token ->
            repository.getStories(token).collectLatest { pagingData ->
                _storiesFlow.value = pagingData
            }
        }
    }
}
