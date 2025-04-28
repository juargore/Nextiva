package com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.databinding.BottomSheetImportLocalContactsLoadingBinding
import com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard.ui.importContactLoadingUI
import com.nextiva.nextivaapp.android.util.extensions.orFalse
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetImportContactLoadingViewModel
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetImportWizardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BottomSheetImportContactLoading: Fragment() {

    private lateinit var binding: BottomSheetImportLocalContactsLoadingBinding

    private val viewModel: BottomSheetImportContactLoadingViewModel by viewModels()
    private var parentViewModel: BottomSheetImportWizardViewModel? = null
    private var parentFrag: BottomSheetImportWizard? = null

    companion object {
        fun newInstance() = BottomSheetImportContactLoading()
        const val TAG = "BottomSheetImportContactLoading"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetImportLocalContactsLoadingBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (parentFragment as? BottomSheetImportWizard)?.let { parent ->
            parentFrag = parent
            parentFrag?.isCancelable = false
            parentViewModel = ViewModelProvider(parent)[BottomSheetImportWizardViewModel::class.java]
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                parentFrag?.collapseDialog(
                    view.measuredHeight,
                    BottomSheetBehavior.STATE_COLLAPSED
                )
            }
        }
        )

        lifecycleScope.launch {
            viewModel.state.collect { uiState ->
                binding.composeView.apply {
                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        importContactLoadingUI(
                            uiState = uiState,
                            onDone = { parentFrag?.dismiss() },
                            onRetry = {
                                viewModel.startContactImport(
                                    parentViewModel?.selectedContacts.orEmpty(),
                                    parentViewModel?.duplicatesStrategy.orEmpty()
                                )
                            },
                            onCancel = { parentFrag?.dismiss() },
                            onFinish = { viewModel.finishAnimation() }
                        )
                    }
                }
            }
        }

        setup(savedInstanceState)
    }

    private fun setup(savedInstanceState: Bundle?) {
        if(savedInstanceState == null) {
            val contactType = if (parentViewModel?.shareContacts.orFalse()) {
                Enums.Contacts.ContactTypes.CONNECT_SHARED
            } else {
                Enums.Contacts.ContactTypes.CONNECT_PERSONAL
            }
            parentViewModel?.selectedContacts?.forEach { it.convertToConnect(contactType) }
            viewModel.startContactImport(
                parentViewModel?.selectedContacts.orEmpty(),
                parentViewModel?.duplicatesStrategy.orEmpty()
            )
        }
    }
}