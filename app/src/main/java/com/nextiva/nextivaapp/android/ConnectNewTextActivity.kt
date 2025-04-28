package com.nextiva.nextivaapp.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.databinding.ActivityConnectNewTextBinding
import com.nextiva.nextivaapp.android.features.messaging.view.BottomSheetNewMessage
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConnectNewTextActivity : BaseActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var dialogManager: DialogManager

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ConnectNewTextActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindViews())

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setStatusBarColor(ContextCompat.getColor(this, R.color.mainStatusBarDim))

        var imageAttachment: Uri? = null
        if (intent?.action == Intent.ACTION_SEND) {

            if (sessionManager.userDetails == null) {
                showErrorDialog()
            }

            if (intent.type?.startsWith("image/") == true) {
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM)
                }
                imageAttachment = uri
            }
        }

        val fragment = BottomSheetNewMessage.newInstance(imageAttachment)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.sms_list_frame_layout, fragment)
        transaction.commit()
    }

    fun bindViews(): View {
        val binding = ActivityConnectNewTextBinding.inflate(layoutInflater)
        overrideEdgeToEdge(binding.root)
        return binding.root
    }

    private fun showErrorDialog() {
        val dialogTitle = getString(R.string.error_share_image_logged_out_title)
        val dialogBody = getString(R.string.error_share_image_logged_out_message)

        dialogManager.showDialog(this,
            dialogTitle,
            dialogBody,
            getString(R.string.general_ok)
        ) { _, _ ->
            finish()
        }
    }
}