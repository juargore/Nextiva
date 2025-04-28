package com.nextiva.nextivaapp.android.view.compose.viewstate

data class ConnectContactSelectionViewState(
    val title: String? = "",
    val subTitle: String? = "",
    val icon: Int? = null,
    val cancelTxtBtnViewState: ConnectTextButtonViewState? = null,
    val connectHeaderViewState: ConnectHeaderViewState? = null,
    val selectionInfoIcon: Int? = null,
    val textFieldChangedListener: ((String) -> Unit)? = null,
    val prefillTextFieldValue: String? = null,
    val shouldClearCurrentSearch: Boolean? = false,
    val isDigitOnly: Boolean? = false,
    val trailingIcon: Int? = null,
    val onTrailingIconClick: (() -> Unit)? = null,
    val shouldShowSearchStyleView: Boolean? = false,
    val shouldShowRecentContactsSection: Boolean? = true
)