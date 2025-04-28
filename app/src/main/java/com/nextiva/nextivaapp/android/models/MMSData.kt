package com.nextiva.nextivaapp.android.models

import android.net.Uri
import okhttp3.MultipartBody

data class MMSData(var fileName: String,
                   var fileUri: Uri,
                   var bitmapData: ByteArray,
                   var body: MultipartBody.Part,
                   var contentType: String)