package com.darkndev.netkeep.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.darkndev.netkeep.database.NoteRepository
import com.darkndev.netkeep.utils.NetworkConnectivityObserver
import com.darkndev.netkeep.utils.errorMessage
import com.darkndev.netkeep.utils.user.Connection
import com.darkndev.netkeep.utils.user.Event
import com.darkndev.netkeep.utils.user.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NoteRepository,
    networkConnectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    val allNotes = repository.allNotes.asLiveData(viewModelScope.coroutineContext)

    private val stateFlow = MutableStateFlow(State.NOT_LOADING)
    val state get() = stateFlow.stateIn(viewModelScope, SharingStarted.Lazily, State.NOT_LOADING)

    private val networkState =
        networkConnectivityObserver.observe().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun updateServerNotes() = viewModelScope.launch {
        stateFlow.value = State.LOADING
        if (networkState.first() == Connection.Available)
            when (val resource = repository.uploadAllNotes()) {
                is Resource.Error -> {
                    homeChannel.send(Event.ShowMessage("Upload Failed: ${errorMessage { resource.error }}"))
                }

                is Resource.Success -> {
                    homeChannel.send(Event.ShowMessage(resource.data!!))
                }
            }
        stateFlow.value = State.NOT_LOADING
    }

    fun signOutClicked() = viewModelScope.launch {
        repository.signOut()
        homeChannel.send(Event.Navigate)
    }

    private val homeChannel = Channel<Event>()
    val homeEvent = homeChannel.receiveAsFlow()

    enum class State {
        LOADING, NOT_LOADING
    }
}