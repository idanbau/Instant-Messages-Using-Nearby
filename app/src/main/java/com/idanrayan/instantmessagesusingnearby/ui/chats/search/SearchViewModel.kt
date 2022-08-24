package com.idanrayan.instantmessagesusingnearby.ui.chats.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idanrayan.instantmessagesusingnearby.data.Message
import com.idanrayan.instantmessagesusingnearby.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    private val _pattern = MutableLiveData("")
    val pattern: LiveData<String> = _pattern
    private val _searchResults = mutableStateListOf<Message>()
    val searchResults: SnapshotStateList<Message> = _searchResults
    private var job: Job? = null

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching


    fun onPatternChange(newPattern: String) {
        _pattern.value = newPattern
    }

    fun getSearchResults(id: String) {
        _isSearching.value = true
        if (job?.isActive == true)
            job?.cancel()
        job = viewModelScope.launch {
            searchResults.clear()
            if (pattern.value!!.isNotBlank()) {
                delay(300L)
                searchResults.addAll(userRepository.searchMessages(id, pattern.value!!))
                _isSearching.value = false
            }
        }
    }

}