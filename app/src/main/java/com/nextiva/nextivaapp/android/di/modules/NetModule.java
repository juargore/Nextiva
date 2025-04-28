/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.di.modules;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.burgstaller.okhttp.AuthenticationCacheInterceptor;
import com.burgstaller.okhttp.CachingAuthenticatorDecorator;
import com.burgstaller.okhttp.digest.CachingAuthenticator;
import com.burgstaller.okhttp.digest.Credentials;
import com.burgstaller.okhttp.digest.DigestAuthenticator;
import com.datadog.android.okhttp.DatadogInterceptor;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.core.common.api.ContactManagementPolicyApi;
import com.nextiva.nextivaapp.android.core.common.api.ContactManagementPolicyApiManager;
import com.nextiva.nextivaapp.android.core.common.api.ContactManagementPolicyRepository;
import com.nextiva.nextivaapp.android.core.notifications.api.SchedulesApiManager;
import com.nextiva.nextivaapp.android.core.notifications.api.SchedulesRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.BroadsoftMobileApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.BroadsoftUmsApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.BroadsoftUserApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.CalendarApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.ConversationApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.MediaCallApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.PlatformApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.PlatformContactsApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.PlatformNotificationOrchstrationServiceApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.PlatformRoomsApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.PresenceApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.ProductsApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.SipApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.SmsApiManager;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.AuthenticationRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CalendarRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.CallManagementRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ContactManagementRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.MediaCallRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.MobileConfigRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformNotificationOrchestrationServiceRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ProductsRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SipRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;
import com.nextiva.nextivaapp.android.net.AuthenticationServiceApi;
import com.nextiva.nextivaapp.android.net.BroadsoftMobileApi;
import com.nextiva.nextivaapp.android.net.BroadsoftUmsApi;
import com.nextiva.nextivaapp.android.net.BroadsoftUserApi;
import com.nextiva.nextivaapp.android.net.CalendarApi;
import com.nextiva.nextivaapp.android.net.MediaCallApi;
import com.nextiva.nextivaapp.android.net.PendoApi;
import com.nextiva.nextivaapp.android.net.PlatformAccessApi;
import com.nextiva.nextivaapp.android.net.PlatformApi;
import com.nextiva.nextivaapp.android.net.PlatformNotificationOrchestrationServiceApi;
import com.nextiva.nextivaapp.android.net.SmsMessagesApi;
import com.nextiva.nextivaapp.android.net.interceptors.NextivaAuthenticationServiceInterceptor;
import com.nextiva.nextivaapp.android.net.interceptors.PlatformAuthenticator;
import com.nextiva.nextivaapp.android.net.interceptors.PlatformNotificationOrchestrationServiceAuthenticator;
import com.nextiva.nextivaapp.android.net.interceptors.RateLimiterInterceptor;
import com.nextiva.nextivaapp.android.net.interceptors.UmsAuthenticator;
import com.nextiva.nextivaapp.android.net.interceptors.UmsHostInterceptor;
import com.nextiva.nextivaapp.android.net.interceptors.UserAgentInterceptor;
import com.nextiva.nextivaapp.android.util.LogUtil;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by adammacdonald on 2/2/18.
 */

@Module
@InstallIn(SingletonComponent.class)
public class NetModule {

    private final Application mApplication;
    private final Credentials mDigestAuthCredentials;
    private final UmsHostInterceptor mUmsHostInterceptor;
    private final RateLimiterInterceptor mRateLimiterInterceptor;
    private final HttpLoggingInterceptor mHttpLoggingInterceptor;
    private final DatadogInterceptor mDatadogInterceptor;

    public NetModule(){
        mApplication = null;
        mDigestAuthCredentials = new Credentials("", "");
        mUmsHostInterceptor = new UmsHostInterceptor();
        mRateLimiterInterceptor = new RateLimiterInterceptor();
        mDatadogInterceptor = new DatadogInterceptor();

        mHttpLoggingInterceptor = new HttpLoggingInterceptor();
        mHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    public NetModule(Application application) {
        mApplication = application;
        mDigestAuthCredentials = new Credentials("", "");
        mUmsHostInterceptor = new UmsHostInterceptor();
        mRateLimiterInterceptor = new RateLimiterInterceptor();
        mDatadogInterceptor = new DatadogInterceptor();

        mHttpLoggingInterceptor = new HttpLoggingInterceptor();
        mHttpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Provides
    @Singleton
    AuthenticationRepository providesAuthenticationRepository(BroadsoftUserApiManager broadsoftUserApiManager) {
        return broadsoftUserApiManager;
    }

    @Provides
    @Singleton
    UserRepository providesUserRepository(BroadsoftUserApiManager broadsoftUserApiManager) {
        return broadsoftUserApiManager;
    }

    @Provides
    @Singleton
    CallManagementRepository providesCallManagementRepository(BroadsoftUserApiManager broadsoftUserApiManager) {
        return broadsoftUserApiManager;
    }

    @Provides
    @Singleton
    ContactManagementRepository providesContactManagementRepository(BroadsoftUserApiManager broadsoftUserApiManager) {
        return broadsoftUserApiManager;
    }

    @Provides
    @Singleton
    MobileConfigRepository providesMobileConfigRepository(BroadsoftMobileApiManager broadsoftMobileApiManager) {
        return broadsoftMobileApiManager;
    }

    @Provides
    @Singleton
    UmsRepository providesUmsRepository(BroadsoftUmsApiManager broadsoftUmsApiManager) {
        return broadsoftUmsApiManager;
    }

    @Provides
    @Singleton
    PlatformRepository providesPlatformRepository(PlatformApiManager platformApiManager) {
        return platformApiManager;
    }

    @Provides
    @Singleton
    SmsManagementRepository providesSmsManagementRepository(SmsApiManager smsApiManager) {
        return smsApiManager;
    }

    @Provides
    @Singleton
    SipRepository providesSipRepository(SipApiManager apiManager) {
        return apiManager;
    }

    @Provides
    @Singleton
    PlatformContactsRepository providesPlatformContactRepository(PlatformContactsApiManager platformContactsApiManager) {
        return platformContactsApiManager;
    }

    @Provides
    @Singleton
    PlatformRoomsRepository providesPlatformRoomsRepository(PlatformRoomsApiManager platformRoomsApiManager) {
        return platformRoomsApiManager;
    }

    @Provides
    @Singleton
    MediaCallRepository providesMediaCallRepository(MediaCallApiManager mediaCallApiManager) {
        return mediaCallApiManager;
    }

    @Provides
    @Singleton
    PresenceRepository providesPresenceRepository(PresenceApiManager presenceApiManager) {
        return presenceApiManager;
    }

    @Provides
    @Singleton
    ProductsRepository providesProductRepository(ProductsApiManager productsApiManager) {
        return productsApiManager;
    }

    @Provides
    @Singleton
    PlatformNotificationOrchestrationServiceRepository providesPlatformNOSRepository(PlatformNotificationOrchstrationServiceApiManager platformNOSApiManager) {
        return platformNOSApiManager;
    }

    @Provides
    @Singleton
    CalendarRepository providesCalendarRepository(CalendarApiManager calendarApiManager) {
        return calendarApiManager;
    }

    @Provides
    @Singleton
    ConversationRepository providesConversationRepository(ConversationApiManager conversationApiManager) {
        return conversationApiManager;
    }

    @Provides
    @Singleton
    SchedulesRepository providesSchedulesRepository(SchedulesApiManager schedulesApiManager) {
        return schedulesApiManager;
    }

    @Provides
    @Singleton
    ContactManagementPolicyRepository providesContactManagementPolicyRepository(ContactManagementPolicyApiManager contactManagementPolicyApiManager) {
        return contactManagementPolicyApiManager;
    }

    @Provides
    @Singleton
    BroadsoftUserApi providesBroadsoftUserApi(final SessionManager sessionManager, final SharedPreferencesManager sharedPreferencesManager) {
        OkHttpClient.Builder clientBuilder = newBaseOkHttpClientBuilder();
        clientBuilder.followRedirects(false);
        clientBuilder.followSslRedirects(false);
        clientBuilder.addInterceptor(chain -> {
            Request request = chain.request();
            Response response = chain.proceed(chain.request());

            if (response.code() == 401) {
                LogUtil.d("UnauthorizedInterceptor", "HTTP Received 401 error");
            }

            if (response.code() == Enums.ResponseCodes.RedirectionResponsess.MOVED_TEMPORARILY) {
                response.close();
                // We're getting redirected, make sure we go to the correct URL and keep all the headers
                String redirectUrl = response.header("Location");

                Request newRequest = request.newBuilder()
                        .url(!TextUtils.isEmpty(redirectUrl) ? redirectUrl : request.url().toString())
                        .method(request.method(), request.body())
                        .build();

                return chain.proceed(newRequest);

            } else {
                return response;
            }
        });
        clientBuilder.authenticator((route, response) -> {
            Request.Builder builder = response.request().newBuilder();
            builder.header("Authorization", sessionManager.getAuthorizationHeader());

            okhttp3.Response priorResponse = response.priorResponse();
            if (priorResponse != null) {
                if (priorResponse.request() != null) {
                    builder.method(priorResponse.request().method(), priorResponse.request().body());
                }

            } else if (response.code() == Enums.ResponseCodes.ClientFailureResponses.UNAUTHORIZED) {

                Exception exception = new Exception(response.toString());
                FirebaseCrashlytics.getInstance().recordException(exception);
                return null;
            }

            return builder.build();
        });

        Retrofit retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                sharedPreferencesManager.getString(SharedPreferencesManager.BROADSOFT_API_URL, BuildConfig.BROADSOFT_API_URL),
                new Converter.Factory[] {
                        SimpleXmlConverterFactory.createNonStrict(new Persister(new AnnotationStrategy())),
                        GsonConverterFactory.create()});

        return retrofit.create(BroadsoftUserApi.class);
    }

    @Provides
    @Singleton
    AuthenticationServiceApi providesAuthenticationServiceApi(SharedPreferencesManager sharedPreferencesManager) {
        OkHttpClient.Builder clientBuilder = newBaseOkHttpClientBuilder();
        clientBuilder.addInterceptor(new NextivaAuthenticationServiceInterceptor());

        Retrofit retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                sharedPreferencesManager.getString(SharedPreferencesManager.API_URL, BuildConfig.API_URL),
                new Converter.Factory[] {
                        SimpleXmlConverterFactory.createNonStrict(new Persister(new AnnotationStrategy())),
                        GsonConverterFactory.create()});

        return retrofit.create(AuthenticationServiceApi.class);
    }

    @Provides
    @Singleton
    BroadsoftMobileApi providesBroadsoftMobileApi(Application application, Credentials digestAuthCredentials) {
        DigestAuthenticator digestAuthenticator = new DigestAuthenticator(digestAuthCredentials);
        final Map<String, CachingAuthenticator> authCache = new ConcurrentHashMap<>();

        OkHttpClient.Builder clientBuilder = newBaseOkHttpClientBuilder();

        clientBuilder.authenticator(new CachingAuthenticatorDecorator(digestAuthenticator, authCache));
        clientBuilder.addInterceptor(new AuthenticationCacheInterceptor(authCache));

        Retrofit retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                application.getString(R.string.placeholder_api_url),
                new Converter.Factory[] {
                        SimpleXmlConverterFactory.createNonStrict(new Persister(new AnnotationStrategy())),
                        GsonConverterFactory.create()});

        return retrofit.create(BroadsoftMobileApi.class);
    }

    @Provides
    @Singleton
    BroadsoftUmsApi providesBroadsoftUmsApi(Application application, UmsHostInterceptor umsHostInterceptor, RateLimiterInterceptor rateLimiterInterceptor, UmsAuthenticator umsAuthenticator) {
        OkHttpClient.Builder clientBuilder = newBaseOkHttpClientBuilder();
        clientBuilder.addInterceptor(umsHostInterceptor);
        clientBuilder.addInterceptor(rateLimiterInterceptor);
        clientBuilder.authenticator(umsAuthenticator);

        Retrofit retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                application.getString(R.string.placeholder_api_url),
                new Converter.Factory[] {GsonConverterFactory.create()});

        return retrofit.create(BroadsoftUmsApi.class);
    }

    @Provides
    @Singleton
    PlatformAccessApi providesPlatformAccessApi(PlatformAuthenticator platformAuthenticator) {
        OkHttpClient.Builder clientBuilder = newBaseOkHttpClientBuilder();
        clientBuilder.authenticator(platformAuthenticator);

        Retrofit retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                BuildConfig.PLATFORM_ACCESS_API_URL,
                new Converter.Factory[] {GsonConverterFactory.create()});

        return retrofit.create(PlatformAccessApi.class);
    }

    @Provides
    @Singleton
    PlatformApi providesPlatformApi(PlatformAuthenticator platformAuthenticator) {
        OkHttpClient.Builder clientBuilder = newBaseOkHttpClientBuilder();
        clientBuilder.authenticator(platformAuthenticator);

        Retrofit retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                BuildConfig.PLATFORM_API_URL,
                new Converter.Factory[] {GsonConverterFactory.create()});

        return retrofit.create(PlatformApi.class);
    }

    @Provides
    @Singleton
    PlatformNotificationOrchestrationServiceApi providesPlatformNotificationOrchestrationServiceApi(PlatformNotificationOrchestrationServiceAuthenticator platformAuthenticator) {
        OkHttpClient.Builder clientBuilder = newBaseOkHttpClientBuilder();
        clientBuilder.authenticator(platformAuthenticator);

        Retrofit retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                BuildConfig.PLATFORM_API_URL,
                new Converter.Factory[] {GsonConverterFactory.create()});

        return retrofit.create(PlatformNotificationOrchestrationServiceApi.class);
    }

    @Provides
    @Singleton
    SmsMessagesApi providesSmsMessagesApi(PlatformAuthenticator platformAuthenticator) {
        OkHttpClient.Builder clientBuilder = newBaseOkHttpClientBuilder();
        clientBuilder.authenticator(platformAuthenticator);

        Retrofit retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                BuildConfig.PLATFORM_API_URL,
                new Converter.Factory[] {GsonConverterFactory.create()});

        return retrofit.create(SmsMessagesApi.class);
    }

    @Provides
    @Singleton
    MediaCallApi providesMediaCallApi(PlatformAuthenticator platformAuthenticator) {
        OkHttpClient.Builder clientBuilder = newBaseOkHttpClientBuilder();
        clientBuilder.authenticator(platformAuthenticator);

        Retrofit retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                BuildConfig.PLATFORM_API_URL,
                new Converter.Factory[] {GsonConverterFactory.create()});

        return retrofit.create(MediaCallApi.class);
    }

    @Provides
    @Singleton
    CalendarApi providesCalendarApi(PlatformAuthenticator platformAuthenticator) {
        OkHttpClient.Builder clientBuilder = newBaseOkHttpClientBuilder();
        clientBuilder.authenticator(platformAuthenticator);

        Retrofit retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                BuildConfig.PLATFORM_API_URL,
                new Converter.Factory[] {GsonConverterFactory.create()});

        return retrofit.create(CalendarApi.class);
    }

    @Provides
    @Singleton
    ContactManagementPolicyApi providesContactManagementPolicyApi(PlatformAuthenticator platformAuthenticator) {
        OkHttpClient.Builder clientBuilder = newBaseOkHttpClientBuilder();
        clientBuilder.authenticator(platformAuthenticator);

        Retrofit retrofit = newBaseRetrofitInstance(
                clientBuilder.build(),
                BuildConfig.PLATFORM_API_URL,
                new Converter.Factory[] {GsonConverterFactory.create()});

        return retrofit.create(ContactManagementPolicyApi.class);
    }

    @Provides
    @Singleton
    Credentials providesDigestCredentials() {
        return mDigestAuthCredentials;
    }

    @Provides
    @Singleton
    UmsHostInterceptor providesUmsHostInterceptor() {
        return mUmsHostInterceptor;
    }

    @Provides
    @Singleton
    RateLimiterInterceptor providesRateLimiterInterceptor() {
        return mRateLimiterInterceptor;
    }

    private Retrofit newBaseRetrofitInstance(
            @NonNull OkHttpClient okHttpClient,
            @NonNull String baseUrl,
            @NonNull Converter.Factory[] converterFactories) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        for (Converter.Factory converterFactory : converterFactories) {
            builder.addConverterFactory(converterFactory);
        }

        return builder.build();
    }

    private OkHttpClient.Builder newBaseOkHttpClientBuilder() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.readTimeout(Constants.ONE_MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS);
        clientBuilder.writeTimeout(Constants.ONE_MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS);
        clientBuilder.callTimeout(Constants.ONE_MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS);

        clientBuilder.addInterceptor(mDatadogInterceptor);

        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(mHttpLoggingInterceptor);
        }
        clientBuilder.addInterceptor(new UserAgentInterceptor());
        return clientBuilder;
    }

    @Provides
    PendoApi providesPendoApi() {
        return newBaseRetrofitInstance(
                new OkHttpClient.Builder()
                        .readTimeout(Constants.ONE_MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS)
                        .writeTimeout(Constants.ONE_MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS)
                        .callTimeout(Constants.ONE_MINUTE_IN_MILLIS, TimeUnit.MILLISECONDS)
                        .build(),
                "https://app.pendo.io/api/",
                new Converter.Factory[] {GsonConverterFactory.create()})
                .create(PendoApi.class);
    }
}