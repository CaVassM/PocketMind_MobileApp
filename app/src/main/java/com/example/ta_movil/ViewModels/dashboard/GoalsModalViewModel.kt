package com.example.ta_movil.ViewModels.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GoalsModalViewModel : ViewModel() {
    var showModal by mutableStateOf(false)
        private set

    fun showAddGoalModal() {
        showModal = true
    }

    fun hideModal() {
        showModal = false
    }
}
