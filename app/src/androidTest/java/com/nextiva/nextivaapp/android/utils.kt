//package com.nextiva.nextivaapp.android
//
//import android.app.Activity
//import android.support.test.espresso.Espresso.onView
//import android.support.test.espresso.NoMatchingViewException
//import android.support.test.espresso.UiController
//import android.support.test.espresso.ViewAction
//import android.support.test.espresso.ViewAssertion
//import android.support.test.espresso.action.ViewActions.*
//import android.support.test.espresso.assertion.PositionAssertions.isCompletelyAbove
//import android.support.test.espresso.assertion.ViewAssertions.matches
//import android.support.test.espresso.matcher.ViewMatchers.*
//import android.support.test.uiautomator.UiScrollable
//import android.support.test.uiautomator.UiSelector
//import android.view.View
//import android.widget.TextView
//import arrow.core.Try
//import arrow.core.getOrElse
//import arrow.core.orNull
//import org.hamcrest.Matcher
//
//typealias MaybeString = Try<String>
//typealias MaybeBool = Try<Boolean>
//
//abstract class AbstractTextAction: ViewAction{
//    /**
//     * Returns a description of the view action. The description should not be overly long and should
//     * fit nicely in a sentence like: "performing %description% action on view with id ..."
//     */
//    override fun getDescription(): String {
//        return "Custom view action on control text"
//    }
//
//    /**
//     * A mechanism for ViewActions to specify what type of views they can operate on.
//     *
//     *
//     * A ViewAction can demand that the view passed to perform meets certain constraints. For
//     * example it may want to ensure the view is already in the viewable physical screen of the device
//     * or is of a certain type.
//     *
//     * @return a [
// * `Matcher`](http://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/Matcher.html) that will be tested prior to calling perform.
//     */
//    override fun getConstraints(): Matcher<View> {
//        return isAssignableFrom(TextView::class.java)
//    }
//
//    /**
//     * Performs this action on the given view.
//     *
//     * @param uiController the controller to use to interact with the UI.
//     * @param view the view to act upon. never null.
//     */
//    abstract override fun perform(uiController: UiController?, view: View?)
//
//
//
//}
//
//class TextAssertion(val txt: String, val ignoreCase: Boolean = false) : ViewAssertion {
//    /**
//     * Checks the state of the given view (if such a view is present).
//     *
//     * @param view the view, if one was found during the view interaction or null if it was not (which
//     * may be an acceptable option for an assertion)
//     * @param noViewFoundException an exception detailing why the view could not be found or null if
//     * the view was found
//     */
//    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
//        if (view == null){
//            if (noViewFoundException != null){
//                throw noViewFoundException
//            }else {
//                throw AssertionError("Failed to find view.")
//            }
//        }
//        val tv = view as TextView
//        if (!tv.text.contains(txt, ignoreCase)){
//            throw AssertionError("view does not contain text '$txt'")
//        }
//    }
//
//
//}
//
//
//
//fun getText(locator: Matcher<View>): String {
//    var txt = ""
//    class TextAction : AbstractTextAction() {
//
//
//        override fun perform(uiController: UiController?, view: View?){
//            if (view == null){
//                throw AssertionError("Failed to find view")
//            }
//            val tv = view as TextView
//            if (tv.text == null){
//                throw AssertionError("No text found in view")
//            }
//            txt = tv.text.toString()
//        }
//    }
//
//        onView(locator).perform(TextAction())
//    return txt
//
//
//}
//
//
//fun viewIsDisplayed(locator: Matcher<View>){
//    onView(locator).check(matches(isDisplayed()))
//}
//
//fun clickView(locator: Matcher<View>){
//    onView(locator).perform(click())
//}
//
//fun assertTextIs(locator: Matcher<View>, txt: String){
//    onView(locator).check(matches(withText(txt)))
//}
//
//fun viewIsAbove(locator: Matcher<View>, other: Matcher<View>){
//    onView(locator).check(isCompletelyAbove(other))
//}
//
//
//fun assertContainsText(locator: Matcher<View>, txt: String, ignoreCase: Boolean = false){
//
//    onView(locator).check(TextAssertion(txt, ignoreCase))
//}
//
//
//fun MaybeString.getIt(): String{
//    when(this.isFailure()){
//        true -> throw AssertionError(this.failed().orNull())
//        false -> return this.getOrElse { "" }
//    }
//}
//
//fun MaybeBool.getIt(): Boolean{
//    when(this.isSuccess()){
//        true -> return this.getOrElse { false }
//        false -> throw AssertionError(this.failed().orNull())
//    }
//}
//
//
//fun scrollView(locator: Matcher<View>){
//
//    onView(locator).perform(swipeUp())
//}
//
//fun isDone(rule: LicenseAcceptanceTestRule): Boolean{
//    return rule.activityResult.resultCode == Activity.RESULT_OK ||
//            rule.activityResult.resultCode == Activity.RESULT_CANCELED ||
//            rule.activityResult.resultCode == Activity.RESULT_FIRST_USER
//}
//
//fun scrollThrough(vararg pieces: Int){
//    pieces.forEach {
//        val piece = withId(it)
//        onView(piece).perform(scrollTo())
//    }
//}
//
//fun scrollThrough(scrollViewID: UiSelector, vararg pieces: UiSelector){
//    val scrollView = UiScrollable(scrollViewID)
//    pieces.forEach {
//        scrollView.scrollIntoView(it)
//    }
//}
//
//fun scrollIntoView(scrollableID: String, lookForID: String){
//    val scrollView = UiScrollable(UiSelector().resourceId(scrollableID))
//    scrollView.scrollIntoView(UiSelector().resourceId(lookForID))
//}
