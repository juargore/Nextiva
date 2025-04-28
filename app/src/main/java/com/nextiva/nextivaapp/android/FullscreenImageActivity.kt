package com.nextiva.nextivaapp.android

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName
import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.FULL_IMAGE_ACTIVITY_SCREEN
import com.nextiva.nextivaapp.android.databinding.ActivityFullscreenImageBinding
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.util.extensions.downloadFileAsByteArray
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

@AndroidEntryPoint
class FullscreenImageActivity : BaseActivity() {

    private var attachment: ByteArray? = null
    private var attachmentLink: String = ""
    lateinit var fullScreenImageView: ImageView
    lateinit var toolbar: Toolbar
    private lateinit var backButtonLayout: RelativeLayout
    private lateinit var downloadButtonLayout: RelativeLayout

    @Inject
    lateinit var avatarManager: AvatarManager
    @Inject
    lateinit var mPermissionManager: PermissionManager

    companion object {
        const val LINK_EXTRA = "LINK_EXTRA"
        const val CONTENT_TYPE = "CONTENT_TYPE"

        fun newIntent(context: Context, attachmentLink: String, contentType: String): Intent {
            val intent = Intent(context, FullscreenImageActivity::class.java)
            intent.putExtra(LINK_EXTRA, attachmentLink)
            intent.putExtra(CONTENT_TYPE, contentType)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(bindViews())
        loadAttachmentOnScreen()

        setSupportActionBar(toolbar)
        title = ""
    }

    private fun loadAttachmentOnScreen() {
        intent?.let {
            it.getStringExtra(LINK_EXTRA)?.let { link ->
                attachmentLink = link
                when {
                    intent.getStringExtra(CONTENT_TYPE)?.equals(Enums.Attachment.AttachmentContentType.IMAGE_GIF) == true -> {
                        try {
                            mDbManager.getContentDataFromSmsId(link)
                                    .subscribe(object : DisposableSingleObserver<ByteArray>() {
                                        override fun onSuccess(contentData: ByteArray) {
                                            attachment = contentData
                                            Glide.with(this@FullscreenImageActivity)
                                                    .asGif()
                                                    .load(contentData)
                                                    .into(fullScreenImageView)
                                        }

                                        override fun onError(e: Throwable) {
                                            FirebaseCrashlytics.getInstance().recordException(e)
                                        }
                                    })

                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                        }
                    }
                    intent.getStringExtra(CONTENT_TYPE)?.contains(Enums.Attachment.ContentMajorType.AUDIO) == true -> {
                        fullScreenImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_supported_audio))
                        fullScreenImageView.layoutParams?.height = 450
                        fullScreenImageView.layoutParams?.width = 450
                    }
                    else -> {
                        if (link.startsWith("file://")) {
                            mDbManager.getContentDataFromSmsId(link)
                                    .subscribe(object : DisposableSingleObserver<ByteArray>() {
                                        override fun onSuccess(contentData: ByteArray) {
                                            attachment = contentData
                                            Glide.with(this@FullscreenImageActivity)
                                                    .load(contentData)
                                                    .into(fullScreenImageView)                                    }

                                        override fun onError(e: Throwable) {
                                            FirebaseCrashlytics.getInstance().recordException(e)
                                        }
                                    })
                        } else {
                            mDialogManager.showProgressDialog(this, FULL_IMAGE_ACTIVITY_SCREEN, R.string.progress_loading)

                            Glide.with(this@FullscreenImageActivity)
                                    .load(link)
                                    .listener(object : RequestListener<Drawable> {
                                        override fun onResourceReady(res: Drawable?, m: Any?, t: Target<Drawable>?, ds: DataSource?, r: Boolean): Boolean {
                                            mDialogManager.dismissProgressDialog()
                                            return false
                                        }
                                        override fun onLoadFailed(e: GlideException?, m: Any?, t: Target<Drawable>?, r: Boolean): Boolean {
                                            mDialogManager.dismissProgressDialog()
                                            e?.fillInStackTrace()?.let { error ->
                                                FirebaseCrashlytics.getInstance().recordException(error)
                                            }
                                            return false
                                        }
                                    })
                                    .into(fullScreenImageView)

                            lifecycleScope.launch {
                                attachment = mSessionManager.sessionId?.let { it1 ->
                                    link.downloadFileAsByteArray(
                                        it1, mSessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // override default colors from BaseActivity to match ui design
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        setStatusBarColor(ContextCompat.getColor(this, R.color.connectGrey09))
    }

    private fun saveMediaToStorage() {
        if (attachment == null) {
            showSimpleToast(R.string.chat_details_attachment_invalid)
        } else {
            mDialogManager.showProgressDialog(this, FULL_IMAGE_ACTIVITY_SCREEN, R.string.progress_downloading)
            val filename = attachmentLink.substringAfterLast("/")
            var fos: OutputStream? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }

            fos?.use {
                it.write(attachment)
                runOnUiThread {
                    mDialogManager.dismissProgressDialog()
                    showCustomToastWhenFinished()
                }
            }
        }
    }

    private fun showSimpleToast(message: Int) {
        Toast.makeText(this, getString(message), Toast.LENGTH_LONG).show()
    }

    @SuppressLint("InflateParams")
    private fun showCustomToastWhenFinished() {
        val inflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_toast, null)
        val textView = view.findViewById<TextView>(R.id.custom_toast_message)
        textView.text = getString(R.string.chat_details_image_saved)
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }

    private fun bindViews(): View {
        val binding = ActivityFullscreenImageBinding.inflate(layoutInflater)
        fullScreenImageView = binding.fullScreenImageView
        toolbar = binding.fullscreenImageToolbar
        backButtonLayout = binding.backArrowInclude.backArrowView
        downloadButtonLayout = binding.downloadButtonInclude.buttonIconView

        binding.downloadButtonInclude.apply {
            buttonIconFontTextView.setIcon(
                    getString(R.string.fa_download),
                    Enums.FontAwesomeIconType.REGULAR
            )
            buttonIconFontTextView.setTextColor(
                    ContextCompat.getColor(this@FullscreenImageActivity, R.color.white)
            )
        }
        binding.backArrowInclude.buttonIconFontTextView.setTextColor(
                ContextCompat.getColor(this@FullscreenImageActivity, R.color.white)
        )

        backButtonLayout.setOnClickListener { finish() }
        downloadButtonLayout.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveMediaToStorage()
            } else {
                mPermissionManager.requestStorageToDownloadPermission(
                        this,
                        ScreenName.APP_PREFERENCES,
                        { saveMediaToStorage() },
                        { showSimpleToast(R.string.permission_required_title) }
                )
            }
        }

        overrideEdgeToEdge(binding.root)

        return binding.root
    }
}
