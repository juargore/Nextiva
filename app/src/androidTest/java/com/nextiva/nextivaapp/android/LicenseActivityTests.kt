//package com.nextiva.nextivaapp.android
//
//
//import android.support.test.filters.LargeTest
//import android.support.test.rule.ActivityTestRule
//import android.support.test.runner.AndroidJUnit4
//import assertk.assert
//import assertk.assertAll
//import assertk.assertions.isTrue
//import org.junit.*
//import org.junit.runner.RunWith
//
//
//@RunWith(AndroidJUnit4::class)
//@LargeTest
//class LicenseActivityTests: BaseModel(){
//    @Rule @JvmField
//    val mActivityRule: LicenseAcceptanceTestRule =
//            LicenseAcceptanceTestRule(LicenseAcceptanceActivity::class.java,
//                    false, true)
//
//    val PID = android.os.Process.myPid()
//
//    private val declineDialogDescriptionText = "cannot be used unless the terms of the end user license agreement are accepted."
//    private val eulaTextFragment1 = "Licensor is not obligated to provide maintenance, support or updates to You for the Product."
//    private val eulaEmailSubjectTextFragment1 = "License Agreement"
//
//    @Before
//    fun setup(){
//        //This is run before each test method
//        //The activity is launched automatically so don't start it here
//    }
//
//    @After
//    fun cleanUp(){
//        //This is run after each test method
//        //The activity ends automatically so don't end it here
//    }
//
//    @Test
//    fun licenseIsDisplayed() {
//
//        val model = LicenseIntentModel(mActivityRule)
//        assert(model.licenseScreenIsPresent().getIt()).isTrue()
//
//    }
//
//    @Test
//    fun licenseIsNotDisplayed(){
//
//        val model = LicenseIntentModel(mActivityRule)
//
//        assertAll {
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.showAcceptDialog().getIt()).isTrue()
//            //make sure the dialog is present
//            assert(model.acceptDialogAcceptButtonIsPresent().getIt()).isTrue()
//            assert(model.acceptDialogCancelButtonIsPresent().getIt()).isTrue()
//            assert(model.acceptDialogTitleIsPresent().getIt()).isTrue()
//            assert(model.confirmLicenseAccept().getIt()).isTrue()
//            assert(model.thisPhoneScreenIsPresent().getIt()).isTrue()
//        }
//
//        model.restartActivity()
//        assertAll {
//            assert(model.thisPhoneScreenIsPresent().getIt()).isTrue()
//        }
//
//    }
//
//
//    @Test
//    fun TestLicenseAgreementTitle(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll {
//            assert(model.licenseTitleIsPresent().getIt()).isTrue()
//            assert(model.licenseTitleIs("License Agreement"))
//        }
//    }
//
//
//    @Test
//    fun testLicenseAgreementLogo(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll{
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.licenseScreenLogoIsAtTop().getIt()).isTrue()
//        }
//    }
//
//
//    @Test
//    fun testLicenseAgreementButtons(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll{
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.licenseScreenEmailEulaButtonIsPresent().getIt()).isTrue()
//        }
//    }
//
//
//
//    @Test
//    fun testLicenseAgreementEulaText(){
//    //Need a custom scroll action implemented because there is an embedded linear layout in the scrollview.
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll {
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.eulaTextIsPresent().getIt()).isTrue()
//        }
//    }
//
//
//    @Test
//    fun testLicenseAgreementScrollEula(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll {
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.scrollEula().getIt()).isTrue()
//            assert(model.scrollEula().getIt()).isTrue()
//        }
//    }
//
//
//    @Test
//    fun testLicenseAgreementDecline(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll{
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.showDeclineDialog().getIt()).isTrue()
//            assert(model.declineDialogIsPresent().getIt()).isTrue()
//        }
//    }
//
//
//    @Test
//    fun testLicenseAgreementShowDeclineDialog(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll{
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.showDeclineDialog().getIt()).isTrue()
//            assert(model.declineDialogIsPresent().getIt()).isTrue()
//            assert(model.declineDialogContentContainsText(declineDialogDescriptionText).getIt()).isTrue()
//            assert(model.declineDialogExitButtonIsPresent().getIt()).isTrue()
//            assert(model.declineDialogReviewButtonIsPresent().getIt()).isTrue()
//        }
//    }
//
//
//    @Test
//    fun testLicenseAgreementDeclineDialogExit(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll{
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.showDeclineDialog().getIt()).isTrue()
//            assert(model.declineDialogExitApp())
//            assert(isDone(mActivityRule)).isTrue()
//        }
//    }
//
//
//    @Test
//    fun testLicenseAgreementDeclineDialogReview(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll{
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.showDeclineDialog().getIt()).isTrue()
//            assert(model.declineDialogReviewEula().getIt()).isTrue()
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//
//        }
//    }
//
//
//    @Test
//    fun testLicenseAgreementShowAcceptDialog(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll{
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.showAcceptDialog().getIt()).isTrue()
//            assert(model.acceptDialogTitleIsPresent().getIt()).isTrue()
//            assert(model.acceptDialogContentIsPresent().getIt()).isTrue()
//            assert(model.acceptDialogCancelButtonIsPresent().getIt()).isTrue()
//            assert(model.acceptDialogAcceptButtonIsPresent().getIt()).isTrue()
//        }
//    }
//
//
//    @Test
//    fun testLicenseAgreementAcceptDialogButtons(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll{
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.showAcceptDialog().getIt()).isTrue()
//            assert(model.acceptDialogTitleIsPresent().getIt()).isTrue()
//            assert(model.cancelTheAcceptDialog().getIt()).isTrue()
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//        }
//    }
//
//
//    @Test
//    fun testLicenseAgreementAcceptDialogAccept(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll{
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.showAcceptDialog().getIt()).isTrue()
//            assert(model.acceptDialogTitleIsPresent().getIt()).isTrue()
//            assert(model.confirmLicenseAccept().getIt()).isTrue()
//            assert(model.thisPhoneScreenIsPresent().getIt()).isTrue()
//
//        }
//        model.restartActivity()
//        assert(model.thisPhoneScreenIsPresent().getIt()).isTrue()
//    }
//
//    /**
//    ## License Agreement - select email EULA button
//    ###   C223015
//    -    GIVEN       display License Agreement screen
//
//    -    WHEN        select Email EULA button
//
//    -    THEN        launch OS email client
//
//    -    AND     create email
//    -    AND     email Subject contains text: [AppName] - License Agreement
//
//    -    AND     email body contains EULA text
//     */
//    @Test
//    fun testLicenseAgreementEmailEula(){
//        val model = LicenseIntentModel(mActivityRule)
//        assertAll{
//            assert(model.licenseScreenIsPresent().getIt()).isTrue()
//            assert(model.emailEula().getIt()).isTrue()
//            assert(model.validateGmailEula(eulaEmailSubjectTextFragment1,
//                    eulaTextFragment1).getIt()).isTrue()
//
//        }
//    }
//
//
//    companion object {
//
//        @BeforeClass
//        fun setupSuite() {
//            //Run before any test methods are run or the @Before
//        }
//
//        @AfterClass
//        fun cleanupClass() {
//            //Run after all the test methods have run and after the @After
//        }
//
//    }
//
//
//
//}
//typealias LicenseAcceptanceTestRule = ActivityTestRule<LicenseAcceptanceActivity>
//
//
