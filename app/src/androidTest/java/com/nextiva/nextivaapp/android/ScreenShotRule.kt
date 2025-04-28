//package com.nextiva.nextivaapp.android
//
//import android.graphics.Bitmap.CompressFormat
//import android.support.test.runner.screenshot.CustomScreenCaptureProcessor
//import android.support.test.runner.screenshot.ScreenCaptureProcessor
//import android.support.test.runner.screenshot.Screenshot
//import org.junit.rules.TestWatcher
//import org.junit.runner.Description
//
//
//class ScreenShotRule: TestWatcher() {
//    /**
//     * Invoked when a test fails
//     */
//    override fun failed(e: Throwable?, description: Description?) {
//        super.failed(e, description)
//        screenShot(description)
//    }
//
//    private fun screenShot(description: Description?){
//        val processors = HashSet<ScreenCaptureProcessor>()
//        processors.add(CustomScreenCaptureProcessor())
//        val capture = Screenshot.capture()
//        capture.name = "${description?.getTestClass()?.getSimpleName()}-${description?.getMethodName()}"
//        capture.format = CompressFormat.JPEG
//        capture.process(processors)
//
//    }
//}
//
//
