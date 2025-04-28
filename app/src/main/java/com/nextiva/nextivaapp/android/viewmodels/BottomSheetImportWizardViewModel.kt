package com.nextiva.nextivaapp.android.viewmodels

import androidx.lifecycle.ViewModel
import com.nextiva.nextivaapp.android.models.NextivaContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BottomSheetImportWizardViewModel @Inject constructor() : ViewModel() {

    var selectedContacts : List<NextivaContact> = listOf()
    var shareContacts = false
    var duplicatesStrategy: String = DuplicateStrategy.Skip.toString().lowercase()

    enum class DuplicateStrategy {
        Skip, Replace, Merge, ImportAnyway
    }

    private var _strategy: MutableStateFlow<DuplicateStrategy?> =
        MutableStateFlow(null)
    private var _unselect: MutableStateFlow<DuplicateStrategy?> =
        MutableStateFlow(DuplicateStrategy.Replace)
    val strategy: StateFlow<DuplicateStrategy?> = _strategy
    val unselect: StateFlow<DuplicateStrategy?> = _unselect
    val enableNextButton: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun setStrategy(strategy: DuplicateStrategy) {
        if (strategy != _strategy.value) {
            enableNextButton.value = true
            _unselect.value = _strategy.value
            _strategy.value = strategy
            duplicatesStrategy = strategy.toString().lowercase()
        }
    }

    fun resetStrategy() {
        _strategy.value = null
        enableNextButton.value = false
        shareContacts = false
    }

    fun resetContacts() {
        selectedContacts = emptyList()
    }
}