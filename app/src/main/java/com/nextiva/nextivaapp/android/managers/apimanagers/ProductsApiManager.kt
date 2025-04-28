package com.nextiva.nextivaapp.android.managers.apimanagers

import android.app.Application
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ProductsRepository
import com.nextiva.nextivaapp.android.managers.interfaces.LogManager
import com.nextiva.nextivaapp.android.managers.interfaces.NetManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.net.platform.Product
import com.nextiva.nextivaapp.android.models.net.platform.Products
import com.nextiva.nextivaapp.android.net.buses.RxEvents
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ProductsApiManager @Inject constructor(
    var application: Application,
    var logManager: LogManager,
    var netManager: NetManager,
    var schedulerProvider: SchedulerProvider,
    var sessionManager: SessionManager,
    var dbManager: DbManager
) : BaseApiManager(application, logManager), ProductsRepository {

    override fun refreshLicenses(): Single<RxEvents.PhoneInformationResponseEvent> {
        return getProducts()
            .flatMap { response ->
                if (response.isSuccessful) {
                    if (sessionManager.userDetails?.telephoneNumber != null) {
                        getPhoneNumberInformation()
                    } else {
                        Single.just(RxEvents.PhoneInformationResponseEvent(response.isSuccessful))
                    }
                } else {
                    Single.just(RxEvents.PhoneInformationResponseEvent(false))
                }
            }
    }

    override fun getProducts(): Single<RxEvents.ProductsResponseEvent> {
        return Single.fromCallable { sessionManager.sessionId }
            .subscribeOn(schedulerProvider.io())
            .flatMap { sessionId ->
                if (netManager.getMessagesApi() != null) {
                    netManager.getMessagesApi()!!.getProducts(
                        sessionManager.sessionId,
                        sessionManager.userInfo?.comNextivaCorpAccountNumber.toString()
                    )
                        .subscribeOn(schedulerProvider.io())
                        .map { response ->
                            if (response.isSuccessful && response.body() != null) {
                                logServerSuccess(response)
                                val products = Products(response.body() as ArrayList<Product>?)
                                sessionManager.products = products
                                RxEvents.ProductsResponseEvent(true, products)
                            } else {
                                logServerParseFailure(response)
                                RxEvents.ProductsResponseEvent(false, null)
                            }
                        }
                } else {
                    Single.just(RxEvents.ProductsResponseEvent(false, null))
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                RxEvents.ProductsResponseEvent(false, null)
            }
    }

    override fun getPhoneNumberInformation(): Single<RxEvents.PhoneInformationResponseEvent> {
        if (sessionManager.userDetails == null || (sessionManager.userDetails != null && sessionManager.userDetails?.telephoneNumber == null)) {
            return Single.just(RxEvents.PhoneInformationResponseEvent(false))
        }

        return Single.fromCallable { sessionManager.sessionId }
            .subscribeOn(schedulerProvider.io())
            .flatMap { sessionId ->
                if (netManager.getMessagesApi() != null) {
                    netManager.getMessagesApi()!!.getPhoneNumberInformation(
                        sessionManager.sessionId,
                        sessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                        sessionManager.userDetails?.telephoneNumber!!
                    )
                        .subscribeOn(schedulerProvider.io())
                        .map { response ->
                            if (response.isSuccessful) {
                                logServerSuccess(response)
                                sessionManager.phoneNumberInformation = response.body()
                                RxEvents.PhoneInformationResponseEvent(true)
                            } else {
                                logServerParseFailure(response)
                                RxEvents.PhoneInformationResponseEvent(false)
                            }
                        }
                } else {
                    Single.just(RxEvents.PhoneInformationResponseEvent(false))
                }
            }
            .onErrorReturn { throwable ->
                logServerResponseError(throwable)
                RxEvents.PhoneInformationResponseEvent(false)
            }
    }
}
