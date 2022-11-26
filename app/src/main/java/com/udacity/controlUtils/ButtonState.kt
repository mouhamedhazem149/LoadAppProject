package com.udacity.controlUtils

sealed class ButtonState {
    object Transition : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}