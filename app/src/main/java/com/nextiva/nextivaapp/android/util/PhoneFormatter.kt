package com.nextiva.nextivaapp.android.util

class PhoneFormatter {

    companion object {
        /**
         *     15554567890 = +1 (555) 456-7890
         *     5554567890 = (555) 456-7890
         *     54567890 = (5) 456-7890
         *     4567890 = 456-7890
         *     123456 = 12-3456
         */
        fun formatFromEnd(
            s: String,
            addDefaultCountryCode: Boolean
        ): String {
            if (s.length < 6) return s
            val reversed = s.reversed()
            return StringBuilder().apply {
                append(reversed.substring(0, 4) + '-')
                append(reversed.substring(4, reversed.length.coerceAtMost(7)))

                if (length > 7) {
                    append(" )" + reversed.substring(7, reversed.length.coerceAtMost(10)) + "( ")
                }

                if (reversed.length == 10 && addDefaultCountryCode) {
                    append(" 1+")
                } else if (reversed.length in 11..13) {
                    append(reversed.substring(10, reversed.length.coerceAtMost(13)))
                    append("+")
                }
            }.reverse().toString()
        }

        /**
         *     15554567890 = +1 (555) 456-7890
         *     5554567890 = (555) 456-7890
         *     54567890 = (545) 678-90
         *     4567890 = 456-7890
         *     123456 = 123-456
         */
        fun formatFromStart(
            s: String,
            addDefaultCountryCode: Boolean
        ): String {
            if (s.length < 6) return s
            return StringBuilder().apply {
                when {
                    s.length <= 7 -> {
                        append(s.substring(0, 3) + "-")
                        append(s.substring(3, s.length))
                    }

                    s.length in 8..10 -> {
                        if (s.length == 10 && addDefaultCountryCode) append("+1")
                        append("(" + s.substring(0, 3) + ") ")
                        append(s.substring(3, 6) + "-")
                        append(s.substring(6, s.length))
                    }

                    s.length == 11 -> {
                        append("+" + s[0])
                        append(" (" + s.substring(1, 4) + ") ")
                        append(s.substring(4, 7) + "-")
                        append(s.substring(7, s.length))
                    }

                    s.length == 12 -> {
                        append("+" + s.substring(0, 2))
                        append(" (" + s.substring(2, 5) + ") ")
                        append(s.substring(5, 8) + "-")
                        append(s.substring(8, s.length))
                    }
                }
            }.toString()
        }
    }
}