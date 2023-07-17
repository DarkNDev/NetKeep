package com.darkndev.netkeep.ui.authentication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkndev.netkeep.database.NoteRepository
import com.darkndev.netkeep.database.PreferencesManager
import com.darkndev.netkeep.di.NetKeepScope
import com.darkndev.netkeep.utils.user.AuthResult
import com.darkndev.netkeep.utils.user.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: NoteRepository,
    private val prefs: PreferencesManager,
    @NetKeepScope private val netKeepScope: CoroutineScope
) : ViewModel() {

    var signInUsername = state.get<String>("SIGN_IN_U") ?: ""
        set(value) {
            field = value
            state["SIGN_IN_U"] = value
        }

    var signInPassword = state.get<String>("SIGN_IN_P") ?: ""
        set(value) {
            field = value
            state["SIGN_IN_P"] = value
        }

    fun authenticate() = viewModelScope.launch {
        val token = prefs.token.first()
        if (!token.isNullOrBlank())
            signingChannel.send(Event.Navigate)
    }

    fun signInClicked() = netKeepScope.launch {
        if (signInUsername.isBlank() || signInPassword.isBlank())
            return@launch signingChannel.send(Event.ShowMessage("Check Fields"))
        statusChannel.send(true)
        val result = repository.signIn(signInUsername, signInPassword)
        statusChannel.send(false)
        when (result) {
            is AuthResult.Authorized ->
                signingChannel.send(Event.Navigate)

            is AuthResult.Unauthorized ->
                result.data?.let {
                    signingChannel.send(Event.ShowMessage(it))
                }


            is AuthResult.UnknownError ->
                result.data?.let {
                    signingChannel.send(Event.ShowMessage(it))
                }
        }
    }

    private val statusChannel = Channel<Boolean>()
    val status = statusChannel.receiveAsFlow()

    private val signingChannel = Channel<Event>()
    val signingEvent = signingChannel.receiveAsFlow()
}