package com.darkndev.netkeep.ui.authentication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkndev.netkeep.database.NoteRepository
import com.darkndev.netkeep.database.PreferencesManager
import com.darkndev.netkeep.utils.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: NoteRepository,
    private val prefs: PreferencesManager
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
            resultChannel.send(AuthResult.Authorized("Authorised"))
    }

    fun signInClicked() = viewModelScope.launch {
        statusChannel.send(true)
        val result = repository.signIn(signInUsername, signInPassword)
        statusChannel.send(false)
        resultChannel.send(result)
    }

    private val resultChannel = Channel<AuthResult<String>>()
    val authResults = resultChannel.receiveAsFlow()

    private val statusChannel = Channel<Boolean>()
    val status = statusChannel.receiveAsFlow()
}