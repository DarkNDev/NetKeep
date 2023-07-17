package com.darkndev.netkeep.utils.user

sealed class Event {
    data class ShowMessage(val message: String) : Event()
    object Navigate : Event()
}
