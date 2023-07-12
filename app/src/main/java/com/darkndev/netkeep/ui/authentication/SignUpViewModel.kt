package com.darkndev.netkeep.ui.authentication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkndev.netkeep.database.NoteRepository
import com.darkndev.netkeep.utils.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: NoteRepository
) : ViewModel() {

    var signUpUsername = state.get<String>("SIGN_UP_U") ?: ""
        set(value) {
            field = value
            state["SIGN_UP_U"] = value
        }

    var signUpPassword = state.get<String>("SIGN_UP_P") ?: ""
        set(value) {
            field = value
            state["SIGN_UP_P"] = value
        }

    fun signUpClicked() = viewModelScope.launch {
        statusChannel.send(true)
        val result = repository.signUp(signUpUsername, signUpPassword)
        statusChannel.send(false)
        resultChannel.send(result)
    }

    private val resultChannel = Channel<AuthResult<String>>()
    val authResults = resultChannel.receiveAsFlow()

    private val statusChannel = Channel<Boolean>()
    val status = statusChannel.receiveAsFlow()
}