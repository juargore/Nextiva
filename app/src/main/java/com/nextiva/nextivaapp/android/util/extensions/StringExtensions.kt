package com.nextiva.nextivaapp.android.util.extensions

import android.util.Log
import com.nextiva.nextivaapp.android.util.PhoneFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.regex.Pattern

fun String.getEmojiCount(): Int {
    var emojiCount = 0

    for (i in 0 until this.length) {
        val type: Int = Character.getType(this[i])
        if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
            emojiCount++
        }
    }

    return emojiCount / 2
}

fun String.removeWhitespaces() = replace(" ", "")

fun String.containsLetter(): Boolean {
    return this.matches(".*[a-zA-Z]+.*".toRegex())
}

fun String.nullIfEmpty(): String? {
    if (this.isEmpty() || this.isBlank()) {
        return null
    }

    return this
}

fun String.equalsIgnoringEmpty(string: String?): Boolean {
    string?.let {
        if (string.isNotEmpty() && string.isNotBlank() && this.isNotEmpty() && this.isNotBlank()) {
            return string == this
        }
    }

    return false
}

fun String.redactByPattern(regex: String, replacement: String): String {
    var redacted = this
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(this)

    while (matcher.find()) {
        matcher.group(1)?.let { matched ->
            redacted = redacted.replace(matched, replacement)
        }
    }

    return redacted
}

suspend fun String.downloadFileAsByteArray(sessionId: String, corpAcctNumber: String): ByteArray = withContext(Dispatchers.IO) {
    val url = this@downloadFileAsByteArray
    val connection = URL(url).openConnection()
    connection.setRequestProperty("x-api-key", sessionId)
    connection.setRequestProperty("nextiva-context-corpAcctNumber", corpAcctNumber)
    connection.connect()
    connection.getInputStream().use { inputStream ->
        inputStream.readBytes()
    }
}

fun String.extractFirstNumberWithPosition() : Pair<String, Int>? {
    // ',' & ';' signal the start of a DTMF code, the 'x' means extension

    val special = setOf('*', '#')
    val codes = setOf(',', ';', 'x')
    val buff = StringBuilder()

    var specialChar = StringBuilder()
    var i : Int = 0
    while(i < this.length && !this[i].isDigit()) {
        if(special.contains(this[i]) && specialChar.length < 2) specialChar.append(this[i])
        i++
    }
    while(i < this.length) {
        val c = this[i]
        if (c.isDigit()) {
            buff.append(c)
        } else if(special.contains(c)) {
            if(buff.length in 2..3) {
                buff.append(c)
                i++
            }
            break
        } else if (codes.contains(c)) {
            break
        }
        i++
    }
    if(specialChar.notNull() && buff.length in 2..4) buff.insert(0, specialChar)
    Log.d("StringExtension", "Extracted Dial Number: [$buff]")
    return buff.toString().nullIfEmpty()?.let { Pair(it, i) }
}

fun String.extractFirstNumber() : String? {
    return this.extractFirstNumberWithPosition()?.first
}

/**
 * Use this function only after calling extractFirstNumber
 */
fun String.formatPhoneNumber(
    format: PhoneCustomFormat = PhoneCustomFormat.Start,
    addDefaultCountryCode: Boolean = false
): String {

    // - Google default format:
    // return PhoneNumberUtils.formatNumber(this, Locale.getDefault().country)

    // -Custom formatting
    return when (format) {
        PhoneCustomFormat.Start -> PhoneFormatter.formatFromStart(this, addDefaultCountryCode)
        PhoneCustomFormat.End -> PhoneFormatter.formatFromEnd(this, addDefaultCountryCode)
    }
}

enum class PhoneCustomFormat {
    Start, End
}

fun String.extractDtfmTone() : String? {
    return this.extractFirstNumberWithPosition()?.let { prefix ->
        this.substring(prefix.second).let { number ->
            number.toCharArray().let { charArray ->
                val codes = setOf(',', ';', 'x')
                val buff = StringBuilder()
                val valid = setOf('0','1','2','3','4','5','6','7','8','9','*','#')
                var index = -1
                run loop@{
                    charArray.forEachIndexed { _index, c ->
                        if (codes.contains(c) && buff.isEmpty()) {
                            index = _index
                        } else if (index != -1 && valid.contains(c)) {
                            buff.append(c)
                        } else if (index != -1) {
                            return@loop
                        }
                    }
                }
                Log.d("StringExtension", "Extracted DTMF: [${buff}]")
                buff.toString()
            }
        }
    }
}

fun String.removeCountryCode() : String? {
    return this.extractFirstNumberWithPosition()?.let { prefix ->
        this.substring(prefix.second).let { suffix ->
            if(prefix.first.length == 11) {
                 prefix.first.substring(1) + suffix
            } else {
                prefix.first + suffix
            }
        }
    }
}