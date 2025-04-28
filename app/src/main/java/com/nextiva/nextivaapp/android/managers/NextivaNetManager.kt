package com.nextiva.nextivaapp.android.managers

import android.app.Application
import com.datadog.android.okhttp.DatadogInterceptor
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.BuildConfig
import com.nextiva.nextivaapp.android.constants.Constants
import com.nextiva.nextivaapp.android.constants.Enums.Logging.UserDatas.DEVICE_URL
import com.nextiva.nextivaapp.android.constants.Enums.ResponseCodes.RedirectionResponsess.MOVED_TEMPORARILY
import com.nextiva.nextivaapp.android.core.common.api.ContactManagementPolicyApi
import com.nextiva.nextivaapp.android.core.notifications.api.SchedulesApi
import com.nextiva.nextivaapp.android.features.rooms.api.RoomsApi
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.net.AttachmentApi
import com.nextiva.nextivaapp.android.net.BroadsoftUserApi
import com.nextiva.nextivaapp.android.net.CalendarApi
import com.nextiva.nextivaapp.android.net.ContactsApi
import com.nextiva.nextivaapp.android.net.ConversationApi
import com.nextiva.nextivaapp.android.net.MediaCallApi
import com.nextiva.nextivaapp.android.net.PlatformAccessApi
import com.nextiva.nextivaapp.android.net.PlatformApi
import com.nextiva.nextivaapp.android.net.PlatformNotificationOrchestrationServiceApi
import com.nextiva.nextivaapp.android.net.PresenceApi
import com.nextiva.nextivaapp.android.net.SipApi
import com.nextiva.nextivaapp.android.net.SmsMessagesApi
import com.nextiva.nextivaapp.android.net.UsersApi
import com.nextiva.nextivaapp.android.net.VoicemailApi
import com.nextiva.nextivaapp.android.net.interceptors.BroadsoftUserApiAuthenticator
import com.nextiva.nextivaapp.android.net.interceptors.PlatformAuthenticator
import com.nextiva.nextivaapp.android.net.interceptors.PlatformNotificationOrchestrationServiceAuthenticator
import com.nextiva.nextivaapp.android.net.interceptors.UnauthorizedInterceptor
import com.nextiva.nextivaapp.android.net.interceptors.UserAgentInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.simpleframework.xml.convert.AnnotationStrategy
import org.simpleframework.xml.core.Persister
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NextivaNetManager @Inject constructor(
    var application: Application,
    var sessionManager: SessionManager,
    var sharedPreferencesManager: SharedPreferencesManager,
    var configManager: ConfigManager,
    var platformAuthenticator: PlatformAuthenticator,
    var platformNotificationOrchestrationServiceAuthenticator: PlatformNotificationOrchestrationServiceAuthenticator,
    var dialogManager: DialogManager,
    var unauthorizedInterceptor: UnauthorizedInterceptor
) : NetManager {

    private val httpLoggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
    private val datadogInterceptor: DatadogInterceptor = DatadogInterceptor()

    private var broadsoftUserApi: BroadsoftUserApi? = null
    private var platformAccessApi: PlatformAccessApi? = null
    private var platformApi: PlatformApi? = null
    private var messagesApi: SmsMessagesApi? = null
    private var platformNotificationOrchestrationServiceApi: PlatformNotificationOrchestrationServiceApi? = null
    private var voicemailApi: VoicemailApi? = null
    private var contactsApi: ContactsApi? = null
    private var roomsApi: RoomsApi? = null
    private var mediaCallApi: MediaCallApi? = null
    private var presenceApi: PresenceApi? = null
    private var calendarApi: CalendarApi? = null
    private var conversationApi: ConversationApi? = null
    private var attachmentApi: AttachmentApi? = null
    private var schedulesApi: SchedulesApi? = null
    private var usersApi: UsersApi? = null
    private var contactManagementPolicyApi: ContactManagementPolicyApi? = null
    private var sipApi: SipApi? = null

    init {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        setupBroadsoftUserApi(if (configManager.mobileConfig != null &&
            configManager.mobileConfig?.xsi != null &&
            !configManager.mobileConfig?.xsi?.xsiRoot.isNullOrEmpty() &&
            !configManager.mobileConfig?.xsi?.xsiActions.isNullOrEmpty()
        ) {
            configManager.mobileConfig?.xsi?.xsiRoot + configManager.mobileConfig?.xsi?.xsiActions
        } else {
            null
        })
    }

    override fun getPlatformAccessApi(): PlatformAccessApi? {
        platformAccessApi?.let {
            return it
        }

        setupPlatformAccessApi(null)
        return platformAccessApi
    }

    override fun getPlatformApi(): PlatformApi? {
        platformApi?.let {
            return it
        }

        setupPlatformApi(null)
        return platformApi
    }

    override fun getMessagesApi(): SmsMessagesApi {
        messagesApi?.let {
            return it
        }

        return setupMessageApi()
    }

    override fun getVoicemailApi(): VoicemailApi {
        voicemailApi?.let {
            return it
        }

        return setupVoicemailApi()
    }

    override fun getContactsApi(): ContactsApi? {
        contactsApi?.let {
            return it
        }

        setupContactsApi(null)
        return contactsApi
    }

    override fun getRoomsApi(): RoomsApi? {
        roomsApi?.let {
            return it
        }

        setupRoomsApi(null)
        return roomsApi
    }

    override fun getUsersApi(): UsersApi? {
        usersApi?.let {
            return it
        }

        setupUsersApi()
        return usersApi
    }

    override fun getPresenceApi(): PresenceApi {
        presenceApi?.let {
            return it
        }

        return setupPresenceApi()
    }

    override fun getBroadsoftUserApi(): BroadsoftUserApi? {
        broadsoftUserApi?.let {
            return it
        }

        setupBroadsoftUserApi(null)
        return broadsoftUserApi
    }

    override fun getPlatformNotificationOrchestrationServiceApi(): PlatformNotificationOrchestrationServiceApi? {
        platformNotificationOrchestrationServiceApi?.let {
            return it
        }

        setupPlatformNotificationOrchestrationServiceApi(null)
        return platformNotificationOrchestrationServiceApi
    }

    override fun getMediaCallApi(): MediaCallApi? {
        mediaCallApi?.let {
            return it
        }

        setupMediaCallApi(null)
        return mediaCallApi
    }

    override fun getCalendarApi(): CalendarApi? {
        calendarApi?.let {
            return it
        }

        setupCalendarApi(null)
        return calendarApi
    }

    override fun getConversationApi(): ConversationApi {
        conversationApi?.let { return it }

        return setupConversationApi()
    }

    override fun getAttachmentApi(url: String): AttachmentApi {
        attachmentApi?.let { return it }

        return setupAttachmentApi(url)
    }

    override fun getSchedulesApi(): SchedulesApi {
        schedulesApi?.let { return it }

        return setupSchedulesApi()
    }

    override fun getContactManagementPolicyApi(): ContactManagementPolicyApi {
        contactManagementPolicyApi?.let { return it }

        return setupContactManagementPolicyApi()
    }

    override fun getSipApi(): SipApi {
        sipApi?.let { return it }
        return setupSipApi()
    }

    override fun clearBroadsoftUserApiManager() {
        broadsoftUserApi = null
    }

    override fun clearPlatformApiManager() {
        platformAccessApi = null
    }

    override fun clearPlatformApi() {
        platformApi = null
    }

    override fun clearPlatformNotificationOrchestrationServiceApi() {
        platformNotificationOrchestrationServiceApi = null
    }

    private fun setupPlatformAccessApi(url: String?) {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            url
                ?: sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_ACCESS_API_URL,
                    BuildConfig.PLATFORM_ACCESS_API_URL), arrayOf(GsonConverterFactory.create()))

        platformAccessApi = retrofit.create(PlatformAccessApi::class.java)
    }

    private fun setupPlatformApi(url: String?) {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            url ?: sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create()))

        platformApi = retrofit.create(PlatformApi::class.java)
    }

    private fun setupMessageApi(): SmsMessagesApi {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create()))

        val api = retrofit.create(SmsMessagesApi::class.java)
        messagesApi = api
        return api
    }

    private fun setupVoicemailApi(): VoicemailApi {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create())
        )

        val api = retrofit.create(VoicemailApi::class.java)
        voicemailApi = api
        return api
    }

    private fun setupPlatformNotificationOrchestrationServiceApi(url: String?) {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformNotificationOrchestrationServiceAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            url ?: sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create()))

        platformNotificationOrchestrationServiceApi =
            retrofit.create(PlatformNotificationOrchestrationServiceApi::class.java)
    }

    private fun setupContactsApi(url: String?) {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            url ?: sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create()))

        contactsApi = retrofit.create(ContactsApi::class.java)
    }

    private fun setupRoomsApi(url: String?) {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            url ?: sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create()))

        roomsApi = retrofit.create(RoomsApi::class.java)
    }

    private fun setupPresenceApi(): PresenceApi {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create()))

        val api = retrofit.create(PresenceApi::class.java)
        presenceApi = api
        return api
    }

    override fun setupBroadsoftUserApi(url: String?) {
        sessionManager.authorizationHeader?.let { authorizationHeader ->
            val clientBuilder = newBaseOkHttpClientBuilder()
            clientBuilder.followRedirects(false)
            clientBuilder.followSslRedirects(false)
            clientBuilder.addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(chain.request())

                if (response.code == MOVED_TEMPORARILY) {
                    response.close()
                    // We're getting redirected, make sure we go to the correct URL and keep all the headers
                    val redirectUrl = response.header("Location")

                    FirebaseCrashlytics.getInstance()
                        .setCustomKey(DEVICE_URL, redirectUrl.toString())
                    val newRequest = request.newBuilder()
                        .url(redirectUrl ?: request.url.toString())
                        .method(request.method, request.body)
                        .build()

                    chain.proceed(newRequest)

                } else {
                    // Normal call, just return the response
                    response
                }
            }

            clientBuilder.authenticator(BroadsoftUserApiAuthenticator(authorizationHeader))

            val retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                url
                    ?: sharedPreferencesManager.getString(SharedPreferencesManager.BROADSOFT_API_URL,
                        BuildConfig.BROADSOFT_API_URL),
                arrayOf(SimpleXmlConverterFactory.createNonStrict(Persister(AnnotationStrategy())),
                    GsonConverterFactory.create()))

            broadsoftUserApi = retrofit.create(BroadsoftUserApi::class.java)
        }
    }

    private fun setupMediaCallApi(url: String?) {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            url ?: sharedPreferencesManager.getString(
                SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL
            ), arrayOf(GsonConverterFactory.create())
        )

        mediaCallApi = retrofit.create(MediaCallApi::class.java)
    }

    private fun setupCalendarApi(url: String?) {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            url ?: sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create()))

        calendarApi = retrofit.create(CalendarApi::class.java)
    }

    private fun setupConversationApi(): ConversationApi {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create()))

        val api = retrofit.create(ConversationApi::class.java)
        conversationApi = api
        return api
    }

    private fun setupAttachmentApi(url: String): AttachmentApi {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            url, arrayOf(GsonConverterFactory.create()))

        val api = retrofit.create(AttachmentApi::class.java)

        attachmentApi = api
        return api
    }

    private fun setupSchedulesApi(): SchedulesApi {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create()))

        val api = retrofit.create(SchedulesApi::class.java)
        schedulesApi = api
        return api
    }

    private fun setupUsersApi(): UsersApi {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create())
        )

        val api = retrofit.create(UsersApi::class.java)
        usersApi = api
        return api
    }

    private fun setupContactManagementPolicyApi(): ContactManagementPolicyApi {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create())
        )

        val api = retrofit.create(ContactManagementPolicyApi::class.java)
        contactManagementPolicyApi = api
        return api
    }

    private fun setupSipApi(): SipApi {
        val clientBuilder = newBaseOkHttpClientBuilder()
        clientBuilder.authenticator(platformAuthenticator)

        val retrofit = newBaseRetrofitInstance(
            clientBuilder.build(),
            sharedPreferencesManager.getString(SharedPreferencesManager.PLATFORM_API_URL,
                BuildConfig.PLATFORM_API_URL), arrayOf(GsonConverterFactory.create())
        )

        val api = retrofit.create(SipApi::class.java)
        sipApi = api
        return api
    }

    private fun newBaseRetrofitInstance(
        okHttpClient: OkHttpClient,
        baseUrl: String,
        converterFactories: Array<Converter.Factory>,
    ): Retrofit {

        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        for (converterFactory in converterFactories) {
            builder.addConverterFactory(converterFactory)
        }

        return builder.build()
    }

    private fun newBaseOkHttpClientBuilder(): OkHttpClient.Builder {
        val clientBuilder = OkHttpClient.Builder()
        clientBuilder.readTimeout(Constants.ONE_MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS)
        clientBuilder.writeTimeout(Constants.ONE_MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS)
        clientBuilder.connectTimeout(Constants.ONE_MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS)
        clientBuilder.callTimeout(Constants.ONE_MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS)

        clientBuilder.addInterceptor(datadogInterceptor)
        clientBuilder.addInterceptor(unauthorizedInterceptor)

        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(httpLoggingInterceptor)
        }

        clientBuilder.addInterceptor(UserAgentInterceptor())

        return clientBuilder
    }
}
