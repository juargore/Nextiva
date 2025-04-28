package com.nextiva.nextivaapp.android.models.net.platform

data class PhoneNumberInformation(var areaCode: String? = "",
                                  var city: String? = "",
                                  var corpAcctNbr: Int? = 0,
                                  var metadata: PhoneNumberInformationMetaData? = null,
                                  var owningCarrier: String? = "",
                                  var phoneNumber: String? = "",
                                  var status: String? = "",
                                  var subscriberId: Int? = 0,
                                  var type: String? = "",
                                  @Deprecated("We recommend using the UUID from CurrentUser in SessionManager, as this will not exist for users without phone numbers.",
                                      level = DeprecationLevel.WARNING)
                                  var userUuid: String? = "",
                                  var zipCode: String? = "")