package com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.databinding.BottomSheetWizardImportDuplicatesStrategyBinding
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetImportWizardViewModel
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetImportWizardViewModel.DuplicateStrategy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BottomSheetWizardDuplicatesStrategy : Fragment() {

    private lateinit var skipButton: View
    private lateinit var replaceButton: View
    private lateinit var mergeButton: View
    private lateinit var importAnywayButton: View
    private lateinit var nextButton: View
    private lateinit var backIcon: RelativeLayout
    private lateinit var cancelIcon: RelativeLayout
    private lateinit var cancelButton: TextView

    private var viewModel: BottomSheetImportWizardViewModel? = null

    companion object {
        const val TAG = "BottomSheetWizardDuplicatesStrategy"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.bottom_sheet_wizard_import_duplicates_strategy,
            container,
            false
        )
        view?.let { bindViews(view) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (parentFragment as? BottomSheetImportWizard)?.showFullHeight()

        parentFragment?.let { parent ->
            viewModel = ViewModelProvider(
                parent
            )[BottomSheetImportWizardViewModel::class.java]
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel?.strategy?.collect { strategy ->
                        when (strategy) {
                            DuplicateStrategy.Skip -> skipButton.isSelected = true
                            DuplicateStrategy.Replace -> replaceButton.isSelected = true
                            DuplicateStrategy.Merge -> mergeButton.isSelected = true
                            DuplicateStrategy.ImportAnyway -> importAnywayButton.isSelected = true
                            else -> {
                                nextButton.isSelected = false
                            }
                        }
                    }
                }

                launch {
                    viewModel?.unselect?.collect { strategy ->
                        when (strategy) {
                            DuplicateStrategy.Skip -> skipButton.isSelected = false
                            DuplicateStrategy.Replace -> replaceButton.isSelected = false
                            DuplicateStrategy.Merge -> mergeButton.isSelected = false
                            DuplicateStrategy.ImportAnyway -> importAnywayButton.isSelected = false
                            else -> {
                            }
                        }
                    }
                }

                launch {
                    viewModel?.enableNextButton?.collect { enabled ->
                        nextButton.apply {
                            if (enabled) {
                                setOnClickListener {
                                    (parentFragment as? BottomSheetImportWizard)?.let { parent ->
                                        parent.setDialogState(BottomSheetBehavior.STATE_HIDDEN)
                                        parent.setFragment(
                                            BottomSheetImportAllLocalContacts(),
                                            BottomSheetImportAllLocalContacts.TAG
                                        )
                                    }
                                }
                                alpha = 1f
                                isEnabled = true
                            } else {
                                setOnClickListener { }
                                alpha = 0.24f
                                isEnabled = false
                            }
                        }
                    }
                }
            }
        }

        skipButton.setOnClickListener {
            viewModel?.setStrategy(DuplicateStrategy.Skip)
        }
        replaceButton.setOnClickListener {
            viewModel?.setStrategy(DuplicateStrategy.Replace)
        }
        mergeButton.setOnClickListener {
            viewModel?.setStrategy(DuplicateStrategy.Merge)
        }
        importAnywayButton.isEnabled = false
        /*importAnywayButton.setOnClickListener {
            viewModel?.setStrategy(DuplicateStrategy.ImportAnyway)
        }*/
    }

    private fun bindViews(view: View) {
        val binding = BottomSheetWizardImportDuplicatesStrategyBinding.bind(view)

        skipButton = binding.bottomSheetImportDuplicatesSkipButton
        replaceButton = binding.bottomSheetImportDuplicatesReplaceButton
        mergeButton = binding.bottomSheetImportDuplicatesMergeButton
        importAnywayButton = binding.bottomSheetImportDuplicatesImportButton
        nextButton = binding.bottomSheetImportNextButton
        backIcon = binding.backArrowInclude.backArrowView
        cancelIcon = binding.cancelIconInclude.closeIconView
        cancelButton = binding.bottomSheetImportAllCancel

        cancelIcon.setOnClickListener { (parentFragment as BottomSheetImportWizard).dismiss() }
        cancelButton.setOnClickListener {
            viewModel?.resetStrategy()
            viewModel?.resetContacts()
            (parentFragment as BottomSheetImportWizard).onBackPressed()
        }
        backIcon.setOnClickListener {
            (parentFragment as BottomSheetImportWizard).onBackPressed()
        }
    }
}