package com.darkndev.netkeep.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.darkndev.netkeep.database.NoteRepository
import com.darkndev.netkeep.utils.Resource
import com.darkndev.netkeep.utils.errorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    val allNotes = repository.allNotes.asLiveData(viewModelScope.coroutineContext)

    private val stateFlow = MutableStateFlow(State.NOT_LOADING)
    val state get() = stateFlow.stateIn(viewModelScope, SharingStarted.Lazily, State.NOT_LOADING)

    fun refresh() = viewModelScope.launch {
        stateFlow.value = State.LOADING
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

    private val homeChannel = Channel<HomeEvent>()
    val homeEvent = homeChannel.receiveAsFlow()

    sealed class HomeEvent {
        data class ShowMessage(val message: String) : HomeEvent()
    }

    enum class State {
        LOADING, NOT_LOADING
    }
}