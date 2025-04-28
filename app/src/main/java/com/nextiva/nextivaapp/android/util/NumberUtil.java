package com.nextiva.nextivaapp.android.util;

public class NumberUtil {

    public static String asciiToHexString(String string) {
        char[] chars = string.toCharArray();

        StringBuilder builder = new StringBuilder();

        for (char c : chars) {
            int i = c;
            builder.append(Integer.toHexString(i).toLowerCase());
        }

        return builder.toString();
    }

}
