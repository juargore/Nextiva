/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.di.modules;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

import com.burgstaller.okhttp.digest.Credentials;
import com.nextiva.nextivaapp.android.core.common.api.ContactManagementPolicyRepository;
import com.nextiva.nextivaapp.android.core.notifications.api.SchedulesRepository;
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
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UmsRepository;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.UserRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer;
import com.nextiva.nextivaapp.android.mocks.FakeCallManagementRepository;
import com.nextiva.nextivaapp.android.mocks.managers.FakeAuthenticationApiManager;
import com.nextiva.nextivaapp.android.mocks.managers.FakeMobileConfigApiManager;
import com.nextiva.nextivaapp.android.mocks.managers.FakeUserApiManager;
import com.nextiva.nextivaapp.android.net.AuthenticationServiceApi;
import com.nextiva.nextivaapp.android.net.BroadsoftMobileApi;
import com.nextiva.nextivaapp.android.net.BroadsoftUmsApi;
import com.nextiva.nextivaapp.android.net.BroadsoftUserApi;
import com.nextiva.nextivaapp.android.net.PendoApi;
import com.nextiva.nextivaapp.android.net.PlatformAccessApi;
import com.nextiva.nextivaapp.android.net.interceptors.UmsHostInterceptor;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.mockito.Mockito;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


/**
 * Created by adammacdonald on 2/9/18.
 */
@Module
@TestInstallIn(
        components = SingletonComponent.class,
        replaces = NetModule.class
)
public class TestNetModule {

    private final MockWebServer mMockWebServer;
    private final Credentials mDigestAuthCredentials;
    private final UmsHostInterceptor mUmsHostInterceptor;

    public TestNetModule() {
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        mMockWebServer = new MockWebServer();
        try {
            mMockWebServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDigestAuthCredentials = mock(Credentials.class);
        mUmsHostInterceptor = mock(UmsHostInterceptor.class);
    }

    public MockWebServer getMockWebServer() {
        return mMockWebServer;
    }

    @Provides
    @Singleton
    MockWebServer provideMockWebServer() {
        MockWebServer mockWebServer = new MockWebServer();
        try {
            mockWebServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mockWebServer;
    }

    @Provides
    @Singleton
    public AuthenticationRepository providesAuthenticationRepository() {
        return Mockito.spy(new FakeAuthenticationApiManager());
    }

    @Provides
    @Singleton
    public UserRepository providesUserRepository() {
        return Mockito.spy(new FakeUserApiManager());
    }

    @Provides
    @Singleton
    public CallManagementRepository providesCallManagementRepository() {
        return Mockito.spy(new FakeCallManagementRepository());
    }

    @Provides
    @Singleton
    public PlatformContactsRepository providesPlatformContactRepository() {
        return mock(PlatformContactsRepository.class);
    }

    @Provides
    @Singleton
    public ContactManagementRepository providesContactManagementRepository() {
        return mock(ContactManagementRepository.class);
    }

    @Provides
    @Singleton
    public PresenceRepository providesPresenceRepository() {
        return mock(PresenceRepository.class);
    }

    @Provides
    @Singleton
    public MobileConfigRepository providesMobileConfigRepository() {
        return Mockito.spy(new FakeMobileConfigApiManager());
    }

    @Provides
    @Singleton
    public UmsRepository providesUmsRepository() {
        return mock(UmsRepository.class);
    }

    @Provides
    @Singleton
    public PlatformRepository providesPlatformRepository() {
        return mock(PlatformRepository.class);
    }

    @Provides
    @Singleton
    public PlatformRoomsRepository providesPlatformRoomsRepository() {
        return mock(PlatformRoomsRepository.class);
    }

    @Provides
    @Singleton
    public ProductsRepository providesProductsRepository() {
        return mock(ProductsRepository.class);
    }

    @Provides
    @Singleton
    public ConversationRepository providesConversationRepository() {
        return mock(ConversationRepository.class);
    }

    @Provides
    @Singleton
    public SmsManagementRepository providesSmsManagementRepository() {
        return mock(SmsManagementRepository.class);
    }

    @Provides
    @Singleton
    public SchedulesRepository providesSchedulesRepository() {
        return mock(SchedulesRepository.class);
    }

    @Provides
    @Singleton
    public PlatformNotificationOrchestrationServiceRepository providesPlatformNotificationOrchestrationServiceRepository() {
        return mock(PlatformNotificationOrchestrationServiceRepository.class);
    }

    @Provides
    @Singleton
    public ContactManagementPolicyRepository providesContactManagementPolicyRepository() {
        return mock(ContactManagementPolicyRepository.class);
    }

    @Provides
    @Singleton
    public NextivaMediaPlayer providesNextivaMediaPlayer() {
        return mock(NextivaMediaPlayer.class);
    }

    @Provides
    @Singleton
    public CalendarRepository providesCalendarRepository() {
        return mock(CalendarRepository.class);
    }

    @Provides
    @Singleton
    public MediaCallRepository providesMediaCallRepository() {
        return mock(MediaCallRepository.class);
    }

    @Provides
    @Singleton
    public AuthenticationServiceApi providesAuthenticationServiceApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMockWebServer.url("/").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(AuthenticationServiceApi.class);
    }

    @Provides
    @Singleton
    public BroadsoftUserApi providesBroadsoftUserApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMockWebServer.url("/").toString())
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(new Persister(new AnnotationStrategy())))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(BroadsoftUserApi.class);
    }

    @Provides
    @Singleton
    public BroadsoftMobileApi providesBroadsoftMobileApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMockWebServer.url("/").toString())
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict(new Persister(new AnnotationStrategy())))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(BroadsoftMobileApi.class);
    }

    @Provides
    @Singleton
    public PlatformAccessApi providesPlatformApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMockWebServer.url("/").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return spy(retrofit.create(PlatformAccessApi.class));
    }

    @Provides
    @Singleton
    public BroadsoftUmsApi providesBroadsoftUmsApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMockWebServer.url("/").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return spy(retrofit.create(BroadsoftUmsApi.class));
    }

    @Provides
    @Singleton
    public PendoApi providesPendoServiceApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMockWebServer.url("/").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(PendoApi.class);
    }

    @Provides
    @Singleton
    public Credentials providesDigestCredentials() {
        return mDigestAuthCredentials;
    }

    @Provides
    @Singleton
    public UmsHostInterceptor providesUmsHostInterceptor() {
        return mUmsHostInterceptor;
    }
}