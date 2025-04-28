package com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems

import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.models.Voicemail

class VoicemailListItem(
    var voicemail: Voicemail,
    var isChecked: Boolean? = null,
    var isBlocked: Boolean = false
) : SimpleBaseListItem<Voicemail>(voicemail) {
    var nextivaContact: NextivaContact? = null
    var strippedNumber: String? = null
}
