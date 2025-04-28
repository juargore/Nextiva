package com.nextiva.nextivaapp.android.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.databinding.DialogPermissionRationaleBinding

class PermissionRationaleDialog(val activity: Activity,
                                private val rationaleMessage: String,
                                private val imageId: Int,
                                val finishedCallback: () -> Unit): Dialog(activity) {
    private lateinit var image: ImageView
    private lateinit var message: TextView
    private lateinit var button: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindViews())

        image.setImageDrawable(ContextCompat.getDrawable(activity, imageId))
        message.text = rationaleMessage
        button.setOnClickListener {
            dismiss()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        finishedCallback()
    }

    private fun bindViews(): View {
        val binding = DialogPermissionRationaleBinding.inflate(layoutInflater)

        image = binding.dialogPermissionRationaleImage
        message = binding.dialogPermissionRationaleMessage
        button = binding.dialogPermissionRationaleButton

        return binding.root
    }

}