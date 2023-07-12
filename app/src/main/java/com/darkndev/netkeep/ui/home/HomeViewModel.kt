package com.darkndev.netkeep.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.darkndev.netkeep.database.NoteRepository
import com.darkndev.netkeep.models.Note
import com.darkndev.netkeep.utils.NetworkConnectivityObserver
import com.darkndev.netkeep.utils.Resource
import com.darkndev.netkeep.utils.errorMessage
import com.darkndev.netkeep.utils.user.Connection
import com.darkndev.netkeep.utils.user.Transaction
import com.darkndev.netkeep.utils.user.Transaction.ADD
import com.darkndev.netkeep.utils.user.Transaction.DELETE
import com.darkndev.netkeep.utils.user.Transaction.EDIT
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
        networkConnectivityObserver.observe().stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun refresh() = viewModelScope.launch {
        stateFlow.value = State.LOADING
        if (networkState.first() == Connection.Available)
            when (val resource = repository.sync()) {
                is Resource.Error -> {
                    homeChannel.send(HomeEvent.ShowMessage("Sync Failed: ${errorMessage { resource.error }}"))
                }

                is Resource.Success -> {
                    homeChannel.send(HomeEvent.ShowMessage(resource.data!!))
                }
            }
        stateFlow.value = State.NOT_LOADING
    }

    fun transaction(transaction: Transaction, note: Note) = viewModelScope.launch {
        stateFlow.value = State.LOADING
        if (networkState.first() == Connection.Available) {
            val status = when (transaction) {
                ADD -> repository.serverAddNote(note)
                EDIT -> repository.serverEditNote(note)
                DELETE -> repository.serverDeleteNote(note)
            }
            when (status) {
                is Resource.Error -> homeChannel.send(HomeEvent.ShowMessage("Sync Failed: ${errorMessage { status.error }}"))
                is Resource.Success -> homeChannel.send(HomeEvent.ShowMessage(status.data!!))
            }
        }
        stateFlow.value = State.NOT_LOADING
    }

    fun signOutClicked() = viewModelScope.launch {
        repository.signOut()
        homeChannel.send(HomeEvent.Navigate)
    }

    private val homeChannel = Channel<HomeEvent>()
    val homeEvent = homeChannel.receiveAsFlow()

    sealed class HomeEvent {
        data class ShowMessage(val message: String) : HomeEvent()
        object Navigate : HomeEvent()
    }

    enum class State {
        LOADING, NOT_LOADING
    }
}