//package android.support.test.runner.screenshot
//
//
//import android.os.Environment.getExternalStorageDirectory
//import java.io.File
//
//
//class CustomScreenCaptureProcessor : BasicScreenCaptureProcessor {
//    companion object {
//        private val store = File(getExternalStorageDirectory(), "espresso_screenshots")
//
//    }
//    constructor(): super(store){
//        if (!(store.exists() || store.mkdirs())){
//            throw AssertionError("Failed to create the screenshot directory at ${store.absolutePath}")
//        }
//
//    }
//
//}
//
