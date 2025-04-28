package com.nextiva.nextivaapp.android.models.net.broadsoft.voicemail

import org.simpleframework.xml.Element
import java.io.Serializable

//<messageMediaContent>
//<description></description> <mediaType>WAV</mediaType> <content>
//BASE 64 content omitted for reasons of space.
//</content> </messageMediaContent>

data class BroadsoftVoicemailMessageMediaContent(@field:Element(name = "description", required = false) var description: String? = null,
                                                 @field:Element(name = "mediaType", required = false) var mediaType: String? = null,
                                                 @field:Element(name = "content", required = false) var content: String? = null) : Serializable