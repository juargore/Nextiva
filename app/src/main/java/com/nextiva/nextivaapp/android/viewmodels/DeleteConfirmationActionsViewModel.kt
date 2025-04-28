package com.nextiva.nextivaapp.android.viewmodels

import androidx.lifecycle.ViewModel

class DeleteConfirmationActionsViewModel : ViewModel() {
    var deleteAction: (() -> Unit)? = null
    var cancelAction: (() -> Unit)? = null
    var onShowAgainDialogChanged: ((Boolean) -> Unit)? = null
}