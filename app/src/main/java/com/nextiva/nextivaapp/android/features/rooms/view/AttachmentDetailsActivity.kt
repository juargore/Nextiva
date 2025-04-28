package com.nextiva.nextivaapp.android.features.rooms.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.features.rooms.view.components.CustomizedGlideImage
import com.nextiva.nextivaapp.android.features.rooms.view.components.ToolbarIconView
import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
import com.nextiva.nextivaapp.android.util.extensions.drawableToByteArray
import com.nextiva.nextivaapp.android.util.extensions.gifDrawableToByteArray
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

class AttachmentDetailsActivity : BaseActivity() {

    @Inject
    lateinit var mPermissionManager: PermissionManager

    private var drawable: Drawable? = null
    private var gifDrawable: GifDrawable? = null

    companion object {
        private const val PARAMS_FILENAME = "PARAMS_FILENAME"
        private const val PARAMS_URL = "PARAMS_URL"

        fun newIntent(context: Context, filename: String, url: String): Intent {
            val intent = Intent(context, AttachmentDetailsActivity::class.java)
            intent.putExtra(PARAMS_FILENAME, filename)
            intent.putExtra(PARAMS_URL, url)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setContent {
            val filename = intent.extras?.getString(PARAMS_FILENAME) ?: ""
            val url = intent.extras?.getString(PARAMS_URL) ?: ""

            DetailScreen(url = url, filename = filename)
        }
    }

    @Composable
    fun DetailScreen(url: String, filename: String) {
        val view = LocalView.current
        val context = LocalContext.current
        var isLoading by remember { mutableStateOf(true) }
        var loadingFailed by remember { mutableStateOf(false) }
        val contentType = if (filename.lowercase().endsWith(Enums.Attachment.ContentExtensionType.EXT_GIF)) {
            Enums.Attachment.AttachmentContentType.IMAGE_GIF
        } else {
            Enums.Attachment.AttachmentContentType.IMAGE_PNG
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.connectGrey09))
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (loadingFailed) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.general_padding_xxxxxlarge)),
                        painter = painterResource(id = R.drawable.placeholder_padded),
                        contentDescription = ""
                    )
                }

                mSessionManager.sessionId?.let {
                    CustomizedGlideImage(
                        contentScale = ContentScale.FillWidth,
                        data = url,
                        sessionId = it,
                        corpAcctNumber = mSessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                        contentType = contentType,
                        loadFailed = {
                            isLoading = false
                            loadingFailed = true
                        },
                        zoomEnabled = true,
                        loadCompleted = {
                            if (contentType == Enums.Attachment.AttachmentContentType.IMAGE_PNG) {
                                drawable = it
                            } else {
                                gifDrawable = it as GifDrawable
                            }
                            isLoading = false
                        }
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator()
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
            ) {
                ToolbarIconView(
                    modifier = Modifier.align(Alignment.CenterStart),
                    iconString = stringResource(R.string.fa_arrow_left)
                ) {
                    finish()
                }

                ToolbarIconView(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    iconString = stringResource(R.string.fa_download)
                ) {
                    var attachment: ByteArray? = null
                    if (drawable != null) {
                        attachment = drawable?.drawableToByteArray()
                    }
                    if (gifDrawable != null) {
                        attachment = gifDrawable?.gifDrawableToByteArray()
                    }
                    attachment?.let {
                        isLoading = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            saveMediaToStorage(it, filename) { isLoading = false }
                        } else {
                            mPermissionManager.requestStorageToDownloadPermission(
                                context as Activity,
                                Enums.Analytics.ScreenName.APP_PREFERENCES,
                                {
                                    saveMediaToStorage(it, filename) { isLoading = false }
                                },
                                { showSimpleToast(R.string.permission_required_title) }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun saveMediaToStorage(attachment: ByteArray, filename: String, onFinished: () -> Unit) {
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
            showCustomToastWhenFinished()
            onFinished()
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
}
