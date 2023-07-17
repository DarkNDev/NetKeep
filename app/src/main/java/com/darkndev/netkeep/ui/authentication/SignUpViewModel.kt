package com.darkndev.netkeep.ui.authentication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.darkndev.netkeep.database.NoteRepository
import com.darkndev.netkeep.di.NetKeepScope
import com.darkndev.netkeep.utils.user.AuthResult
import com.darkndev.netkeep.utils.user.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: NoteRepository,
    @NetKeepScope private val netKeepScope: CoroutineScope
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

    fun signUpClicked() = netKeepScope.launch {
        if (signUpUsername.isBlank() || signUpPassword.isBlank())
            return@launch signingChannel.send(Event.ShowMessage("Check Fields"))
        statusChannel.send(true)
        val result = repository.signUp(signUpUsername, signUpPassword)
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