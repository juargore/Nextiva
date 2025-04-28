package com.nextiva.nextivaapp.android.managers.apimanagers.repositories

import com.nextiva.nextivaapp.android.net.buses.RxEvents
import io.reactivex.Single

interface ProductsRepository {
    fun refreshLicenses(): Single<RxEvents.PhoneInformationResponseEvent>

    fun getProducts(): Single<RxEvents.ProductsResponseEvent>

    fun getPhoneNumberInformation(): Single<RxEvents.PhoneInformationResponseEvent>
}