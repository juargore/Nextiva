package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.constants.Enums

class ConnectContactDetailTeamsListItem(var uiName: String?,
                                        var title: String?,
                                        var teamsList: ArrayList<String>,
                                        var isClickable: Boolean,
                                        var iconId: Int,
                                        @Enums.FontAwesomeIconType.Type var iconType: Int): BaseListItem()