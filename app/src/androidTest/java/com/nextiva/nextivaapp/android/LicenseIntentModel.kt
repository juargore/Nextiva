//package com.nextiva.nextivaapp.android
//
//
//
//import android.support.test.espresso.matcher.ViewMatchers.withId
//import android.support.test.uiautomator.By
//import android.support.test.uiautomator.UiObject2
//import android.support.test.uiautomator.UiSelector
//import android.support.test.uiautomator.Until
//
//
//import arrow.core.*
//
//
//class LicenseIntentModel(val activityRule: LicenseAcceptanceTestRule): BaseModel(){
//
//    companion object {
//        private const val packageName = ""//""com.nextiva.nextivaapp.android.qa"
//        private const val separator = ""//"":id/"
//        //locators
//        private const val licenseToolbar = R.id.license_agreement_toolbar
//        private const val eulaText1 = R.id.license_agreement_text_one
//        private const val eulaText2 = R.id.license_agreement_text_two
//        private const val eulaURLText = R.id.license_agreement_url
//        private const val acceptButton = R.id.license_acceptance_accept_button
//        private const val declineButton = R.id.license_acceptance_decline_button
//        private const val acceptDlgContent = R.id.md_content
//        private const val acceptDlgAcceptButton = R.id.md_buttonDefaultPositive
//        private const val acceptDlgCancelButton = R.id.md_buttonDefaultNegative
//        private const val acceptDlgTitle = R.id.md_title
//        private const val declineDlgTitle = R.id.md_title
//        private const val declineDlgContent = R.id.md_content
//        private const val declineDlgReviewButton = R.id.md_buttonDefaultPositive
//        private const val declineDlgExitButton  = R.id.md_buttonDefaultNegative
//        private const val thisPhoneNumberEntryField = R.id.device_phone_number_text_view
//        private const val licenseLogo = R.id.license_acceptance_agreement_logo_image_view
//        private const val emailEulaButton = R.id.menu_license_agreement_send_email
//        private const val eulaContainer = R.id.license_acceptance_fragment_agreement_fragment
//        private const val uiEulaContainer = "${packageName}${separator}license_acceptance_fragment_agreement_fragment"
//        //private const val uiEulaContainer = "${packageName}${separator}license_fragment_content_linear_layout"
//        private const val uiEulaPart1 = "${packageName}${separator}license_agreement_text_one"
//        private const val uiEulaPart2 = "${packageName}${separator}license_agreement_text_two"
//        private const val uiEulaURL = "${packageName}${separator}license_agreement_url"
//
//    }
//
//
//
//    /**
//     * Get the license screen title text
//     */
//    fun licenseTitleText(): MaybeString{
//        return Try{getText(withId(licenseToolbar))}
//    }
//
//    /**
//     * make sure that the Eula Text is Txt
//     * Arguments:
//     *   txt: The expected text
//     *   caseInsensitive: True the comparison is case insensitive
//     */
//    fun eulaTextIs(txt: String, caseInsensitive: Boolean = false): MaybeBool{
//        val eula = eulaText()
//        return Try{eula.getOrDefault { "" }.compareTo(txt, caseInsensitive) == 0}
//    }
//
//    /**
//     * make sure the license screen is visible
//     *
//     */
//    fun licenseScreenIsPresent(): MaybeBool{
//        return Try {
//            val rslts: Sequence<MaybeBool> = sequenceOf(acceptButtonIsPresent(), declineButtonIsPresent(), licenseTitleIsPresent())
//
//            val errors: Sequence<Throwable?> = rslts.filter { it.isFailure() }.map{it.failed().orNull()}
//            if (errors.count() > 0) throw AssertionError(errors.map { it?.message }.joinTo(StringBuilder(), "\n ").toString())
//            rslts.all { it -> it.getOrElse { false } }
//        }
//
//
//    }
//
//    /**
//     * Check for the presence of the license screen title
//     */
//    fun licenseTitleIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(licenseToolbar))
//            true
//        }
//    }
//
//
//    /**
//     * make sure that the license title is txt
//     * Arguments:
//     *   txt: The expected text
//     */
//    fun licenseTitleIs(txt: String): MaybeBool{
//        return Try{
//            assertTextIs(withId(licenseToolbar), txt)
//            true
//        }
//    }
//
//    /**
//     * make sure that the title contains text txt
//     * Arguments:
//     * txt: The text that should be in the title
//     * ignoreCase: True the case of the text is ignored.
//     */
//    fun licenseTitleContainsText(txt: String, ignoreCase: Boolean = false): MaybeBool{
//        return Try{
//            assertContainsText(withId(licenseToolbar), txt)
//            true
//        }
//    }
//
//    /**
//     * ensure that the accept button is present
//     */
//    fun acceptButtonIsPresent(): MaybeBool{
//        return Try {
//            viewIsDisplayed(withId(acceptButton))
//            true
//        }
//
//    }
//
//    /**
//     * ensure that the decline button is present
//     */
//    fun declineButtonIsPresent(): MaybeBool{
//
//        return Try{
//            viewIsDisplayed((withId(declineButton)))
//            true
//        }
//    }
//
//    /**
//     * get the text of the decline button
//     */
//    fun declineButtonText(): MaybeString{
//        return Try{
//            getText(withId(declineButton))
//        }
//    }
//
//    /**
//     * ensure that the text of the decline button is txt
//     * Arguments:
//     *   txt: The expected text
//     */
//    fun declineButtonTextIs(txt: String): MaybeBool{
//        return Try{
//            assertTextIs(withId(declineButton), txt)
//            true
//        }
//    }
//
//    /**
//     * ensure that the decline button label contains the given text
//     * Arguments:
//     *   txt: The expected text
//     *   ignoreCase: is case ignored in the check
//     */
//    fun declineButtonContainsText(txt: String, ignoreCase: Boolean = false): MaybeBool{
//        return Try{
//            assertContainsText(withId(declineButton), txt, ignoreCase)
//            true
//        }
//    }
//
//    /**
//     * show the accept dialog
//     */
//    fun showAcceptDialog(): MaybeBool{
//        return Try{
//            clickView(withId(acceptButton))
//            true
//        }
//    }
//
//    /**
//     * get the accept button text
//     *
//     */
//    fun acceptButtonText(): MaybeString{
//        return Try{
//            getText(withId(acceptButton))
//        }
//    }
//
//    /**
//     * ensure accept button label is txt
//     * Arguments:
//     *   txt: Expected text
//     */
//    fun acceptButtonTextIs(txt: String): MaybeBool{
//        return Try{
//            assertTextIs(withId(acceptButton), txt)
//            true
//        }
//    }
//
//    /**
//     * ensure that the accept button label contains the given text
//     * Arguments:
//     *   txt: The expected text
//     *   ignoreCase: True the case of text is ignored.
//     */
//    fun acceptButtonContainsText(txt: String, ignoreCase: Boolean = false): MaybeBool{
//        return Try{
//            assertContainsText(withId(acceptButton), txt)
//            true
//        }
//    }
//
//    /**
//     * Show the decline dialog
//     *
//     */
//    fun showDeclineDialog(): MaybeBool{
//        return Try {
//            clickView(withId(declineButton))
//            true
//        }
//    }
//
//
//
//    fun acceptDialogTitleIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(acceptDlgTitle))
//            true
//        }
//    }
//
//    /**
//     * get the license dialog title text
//     */
//    fun licenseDialogTitleText(): MaybeString{
//        return Try {
//            getText(withId(acceptDlgTitle))
//        }
//    }
//
//    /**
//     * get the license accept dialog description text
//     */
//    fun acceptDialogContentText(): MaybeString{
//        return Try{
//            getText(withId(acceptDlgContent))
//        }
//    }
//
//    /**
//     * Make sure that the accept dialog description text is present
//     */
//    fun acceptDialogContentIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(acceptDlgContent))
//            true
//        }
//    }
//
//    /**
//     * ensure that the accept dialog description text is the given text
//     * Arguments:
//     *   txt: The expected text
//     */
//    fun acceptDialogContentTextIs(txt: String): MaybeBool{
//        return Try{
//            assertTextIs(withId(acceptDlgAcceptButton), txt)
//            true
//        }
//    }
//
//    /**
//     * ensure that the accept dialog text contians the given text
//     * Arguments:
//     *   txt: The expected text
//     *   ignoreCase: Ignore case when checking for the text.
//     */
//    fun acceptDialogContentContainsText(txt: String, ignoreCase: Boolean = false): MaybeBool{
//        return Try{
//            assertContainsText(withId(acceptDlgContent), txt, ignoreCase)
//            true
//        }
//    }
//
//    /**
//     * ensure that the accept dialogs accept button is present
//     */
//    fun acceptDialogAcceptButtonIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(acceptDlgAcceptButton))
//            true
//        }
//    }
//
//    /**
//     * get the text of the accept dialogs accept button label
//     */
//    fun acceptDialogAcceptButtonText(): MaybeString{
//        return Try{
//            getText(withId(acceptButton))
//        }
//    }
//
//    /**
//     * ensure that the accept dialog accept button label text is the given value
//     * Arguments:
//     * txt: Expected text
//     */
//    fun acceptDialogAcceptButtonTextIs(txt: String): MaybeBool{
//        return Try{
//            assertTextIs(withId(acceptDlgAcceptButton), txt)
//            true
//        }
//    }
//
//
//    /**
//     * ensure that the accept button label text contains the given value
//     * Arguments:
//     *   txt: Expected txt
//     *   ignoreCase: True ignored text case
//     */
//    fun acceptDialogAcceptButtonContainsText(txt: String, ignoreCase: Boolean = false): MaybeBool{
//        return Try{
//            assertContainsText(withId(acceptDlgAcceptButton), txt, ignoreCase)
//            true
//        }
//    }
//
//    /**
//     *  ensure that the accept buttons cancel button is present
//     */
//    fun acceptDialogCancelButtonIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(acceptDlgCancelButton))
//            true
//        }
//    }
//
//    /**
//     * cancel the accept dialog and return to the license screen
//     */
//    fun cancelTheAcceptDialog(): MaybeBool{
//        return Try{
//            clickView(withId(acceptDlgCancelButton))
//            true
//        }
//    }
//
//    /**
//     * get the accept dialogs cancel button label text
//     */
//    fun acceptDialogCancelButtonText(): MaybeString{
//        return Try{
//            getText(withId(acceptDlgCancelButton))
//        }
//    }
//
//    /**
//     * ensure that the accept dialogs cancel button label text is the given value
//     * Arguments:
//     *   txt: The expected text
//     */
//    fun acceptDialogCancelButtonTextIs(txt: String): MaybeBool{
//        return Try{
//            assertTextIs(withId(acceptDlgCancelButton), txt)
//            true
//        }
//    }
//
//    /**
//     * ensure that the accept dialogs cancel buttons label contains the given text
//     * Arguments:
//     *   txt: The expected text
//     *   ignoreCase: True ignored the text case
//     */
//    fun acceptDialogCancelButtonContainsText(txt: String, ignoreCase: Boolean = false): MaybeBool{
//        return Try{
//            assertContainsText(withId(acceptDlgCancelButton), txt, ignoreCase)
//            true
//        }
//    }
//
//    /**
//     * abort license accept.
//     */
//    fun cancelLicenseAccept(): MaybeBool{
//        return Try{
//            clickView(withId(acceptDlgCancelButton))
//            true
//        }
//    }
//
//    /**
//     * confirm the license accept dialog
//     */
//    fun confirmLicenseAccept(): MaybeBool {
//        return Try{
//            clickView(withId(acceptDlgAcceptButton))
//            true
//        }
//    }
//
//    /**
//     * ensure that the decline dialog is present
//     */
//    fun declineDialogIsPresent(): MaybeBool{
//        return Try {
//            val rslts = sequenceOf(declineDialogExitButtonIsPresent(),
//                    declineDialogReviewButtonIsPresent(),
//                    declineDialogTitleIsPresent(),
//                    declineDialogContentIsPresent())
//            val errors = rslts.filter { it.isFailure() }.map { it.failed().orNull() }
//            if (errors.count() > 0){
//                val msg = errors.map { it?.message }.joinTo(StringBuilder(), "\n").toString()
//                throw java.lang.AssertionError(msg)
//            }
//            rslts.all { it.getOrElse { false } }
//        }
//    }
//
//    /**
//     * get the decline dialogs title text
//     */
//    fun declineDialogTitleText(): MaybeString{
//        return Try{
//            getText(withId(declineDlgTitle))
//        }
//    }
//
//    /**
//     * ensure that the decline dialogs title text is the given value
//     * Arguments:
//     *   txt: The expected text
//     */
//    fun declineDialogTitleTextIs(txt: String): MaybeBool{
//        return Try{
//            assertTextIs(withId(declineDlgTitle), txt)
//            true
//        }
//    }
//
//    /**
//     * ensure that the decline dialogs title contains the given text.
//     * arguments:
//     *   txt: The expected text
//     *   ignoreCase: True ignores case when comparing
//     */
//    fun declineDialogTitleContainsText(txt: String, ignoreCase: Boolean = false): MaybeBool{
//        return Try{
//            assertContainsText(withId(declineDlgTitle), txt, ignoreCase)
//            true
//        }
//    }
//
//    /**
//     * ensure that the decline dialog description is present
//     */
//    fun declineDialogContentIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(declineDlgContent))
//            true
//        }
//    }
//
//    /**
//     * ensure that the decline dialogs description is the given value
//     * Arguments:
//     *   txt: Expected value
//     */
//    fun declineDialogContentTextIs(txt: String): MaybeBool{
//        return Try{
//            assertTextIs(withId(declineDlgContent), txt)
//            true
//        }
//    }
//
//    /**
//     * ensure that the decline dialogs description contains the given text.
//     * arguments:
//     *   txt: The expected text
//     *   ignoreCase: True ignores case when making the comparison
//     */
//    fun declineDialogContentContainsText(txt: String, ignoreCase: Boolean = false): MaybeBool{
//        return Try{
//            assertContainsText(withId(declineDlgContent), txt, ignoreCase)
//            true
//        }
//    }
//
//    /**
//     * ensure that the decline dialogs title is present
//     */
//    fun declineDialogTitleIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(declineDlgTitle))
//            true
//        }
//    }
//
//    /**
//     * ensure that the decline dialogs review button is present
//     */
//    fun declineDialogReviewButtonIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(declineDlgReviewButton))
//            true
//        }
//    }
//
//    /**
//     * ensure that the decline dialogs exit button is present
//     */
//    fun declineDialogExitButtonIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(declineDlgExitButton))
//            true
//        }
//    }
//
//    /**
//     * get the decline dialogs exit buttons label text
//     */
//    fun declineDialogExitButtonText(): MaybeString{
//        return Try{
//            getText(withId(declineDlgExitButton))
//        }
//    }
//
//    /**
//     * ensure that the decline dialogs exit button label text is the given value
//     * arguments:
//     *   txt: The expected text
//     */
//    fun declineDialogExitButtonTextIs(txt: String): MaybeBool{
//        return Try{
//            assertTextIs(withId(declineDlgExitButton), txt)
//            true
//        }
//    }
//
//    /**
//     * ensure that the decline dialogs exit button label contains the given text
//     * arguments:
//     *   txt: The expected text
//     *   ignoreCase: True ignores the texts case when comparing
//     */
//    fun declineDialogExitButtonContainsText(txt: String, ignoreCase: Boolean = false): MaybeBool{
//        return Try{
//            assertContainsText(withId(declineDlgExitButton), txt, ignoreCase)
//            true
//        }
//    }
//
//    /**
//     * get the decline dialogs review button label text
//     */
//    fun declineDialogReviewButtonText(): MaybeString{
//        return Try{
//            getText(withId(declineDlgReviewButton))
//        }
//    }
//
//    /**
//     * ensure that the decline buttons review button label text is the given value
//     * arguments:
//     *   txt: The expected text
//     */
//    fun declineDialogReviewButtonTextIs(txt: String): MaybeBool{
//        return Try{
//            assertTextIs(withId(declineDlgReviewButton), txt)
//            true
//        }
//    }
//
//    /**
//     * ensure that the decline dialogs review button label contains the given text
//     * arguments:
//     *   txt: The expected text
//     *   ignoreCase: True ignores case when making the comparison
//     */
//    fun declineDialogReviewButtonContainsText(txt: String): MaybeBool{
//        return Try{
//            assertContainsText(withId(declineDlgReviewButton), txt)
//            true
//        }
//    }
//
//    /**
//     * get the decline dialogs description text
//     */
//    fun declineDialogContentText(): MaybeString{
//        return Try{
//            getText(withId(declineDlgContent))
//        }
//    }
//
//    /**
//     * decline the eula and exit the app
//     */
//    fun declineDialogExitApp(): MaybeBool{
//        return Try{
//            clickView(withId(declineDlgExitButton))
//            true
//        }
//    }
//
//    /**
//     * cancel the decline dialog and go back to the licensing screen
//     */
//    fun declineDialogReviewEula(): MaybeBool{
//        return Try{
//            clickView(withId(declineDlgReviewButton))
//            true
//        }
//    }
//
//    /**
//     * get the eula text
//     */
//    fun eulaText(): MaybeString {
//        return Try{
//            val eula1 = getText(withId(eulaText1))
//            val eula2 = getText(withId(eulaText2))
//            val eulaURL = getText(withId(eulaURLText))
//
//            "$eula1\n$eula2\n$eulaURL"
//        }
//    }
//
//    /**
//     * ensure that the eula contains the given text
//     * arguments:
//     */
//    fun eulaContainsText(txt: String): MaybeBool{
//        return Try {
//            eulaText().right().contains(txt)
//        }
//
//    }
//
//    fun eulaTextIsPresent(): MaybeBool{
//        return Try{
//            //scrollThrough(eulaText1, eulaText2, eulaURLText)
//            scrollThrough(UiSelector().resourceId(uiEulaContainer),
//                    UiSelector().resourceId(uiEulaPart1),
//                    UiSelector().resourceId(uiEulaPart2),
//                    UiSelector().resourceId(uiEulaURL))
//            true
//        }
//    }
//
//    fun scrollEula(): MaybeBool{
//        return Try{
//            scrollView(withId(eulaContainer))
//            true
//        }
//    }
//
//    /**
//     * restart the activity
//     */
//    fun restartActivity(){
//        activityRule.runOnUiThread{activityRule.activity.recreate()}
//    }
//
//    fun thisPhoneScreenIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(thisPhoneNumberEntryField))
//            true
//        }
//    }
//
//     fun licenseScreenLogoIsPresent(): MaybeBool{
//         return Try{
//             viewIsDisplayed(withId(licenseLogo))
//             true
//         }
//     }
//
//    fun licenseScreenLogoIsAtTop(): MaybeBool{
//        return Try{
//            viewIsAbove(withId(licenseLogo), withId(eulaText1))
//            true
//        }
//    }
//
//    fun licenseScreenEmailEulaButtonIsPresent(): MaybeBool{
//        return Try{
//            viewIsDisplayed(withId(emailEulaButton))
//            true
//        }
//    }
//
//    fun licenseScreenEmailEulaButtonIsAtTop(): MaybeBool{
//        return Try{
//            viewIsAbove(withId(emailEulaButton), withId(eulaText1))
//            true
//        }
//    }
//
//    fun emailEula(): MaybeBool{
//        return Try{
//            clickView(withId(emailEulaButton))
//            true
//        }
//    }
//
//    fun validateGmailEula(subjectContains: String, bodyContains: String): MaybeBool{
//        return Try{
//            uiDevice.wait(
//                    Until.hasObject(By.pkg(gmailPackageName).depth(0)), 5000L)
//
//            val subjectField: UiObject2 = uiDevice.findObject(By.clazz("android.widget.EditText"))
//            val bodyField: UiObject2 = uiDevice.findObject(By.clazz("android.webkit.WebView"))
//            subjectField.wait(Until.textContains(subjectContains) ,5000L)
//            bodyField.wait(Until.textContains(bodyContains), 5000L)
//            uiDevice.pressHome()
//
//            true
//        }
//    }
//
//}