/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.models.CallLogEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by adammacdonald on 3/28/18.
 */

public class CallUtil {

    public static String cleanPhoneNumber(@NonNull String phoneNumber) {
        return phoneNumber.replaceAll("[^0-9*#]", "");
    }
    public static String cleanDTMFToneNumber(@NonNull String phoneNumber) {
        return phoneNumber.replaceAll("[^0-9,;*#ABCD]", "");
    }

    public static String cleanPhoneNumberAndRemoveUSCountryCode(@NonNull String phoneNumber) {
        if(phoneNumber.isEmpty() || phoneNumber.length() < 10)
            return phoneNumber;

        boolean containsCountryCode = true;
        while (containsCountryCode) {
            if ((phoneNumber.charAt(0) == '+' && !phoneNumber.substring(1).isEmpty()) || (phoneNumber.charAt(0) == '1' && !phoneNumber.substring(1).isEmpty())) {
                phoneNumber = phoneNumber.substring(1);
            } else {
                containsCountryCode = false;
            }
        }
        return phoneNumber.replaceAll("[^0-9*#]", "");
    }

    public static String getStrippedPhoneNumber(@NonNull String phoneNumber) {
        if(phoneNumber == null || phoneNumber.isEmpty())
            return "";

        return phoneNumber.replaceAll("\\D", "");
    }

    public static String removeCountryCodeFromFormattedPhoneNumber(String input) {
        String phoneNumber = input;

        if (isCountryCodeAdded(input)) {
            phoneNumber = input.substring(1).trim();
        }
        String formattedNumber = PhoneNumberUtils.formatNumber(phoneNumber);

        return formattedNumber != null ? formattedNumber.trim() : input;
    }

    public static String[] separatePhoneNumberFromDTMFTones(@NonNull String phoneNumber) {
        String[] number = phoneNumber.split("[,;]", 2);
        number[0] = cleanPhoneNumber(number[0]);
        if(number.length > 1)
            number[1] = cleanDTMFToneNumber(number[1]);
        return number;
    }

    /**
     * Cleans all non-functional characters from the passed in phone number.
     * <br>
     * <br>
     * <p>
     * Non-Functional Characters: '(', ')', '-', ' '
     *
     * @param phoneNumber The phone number to clean
     * @return The cleaned phone number
     */
    @NonNull
    public static String cleanForTextWatcher(@Nullable String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return "";
        } else {
            return phoneNumber.replaceAll("[()\\s-]", "");
        }
    }

    public static boolean isValidTextWatcherCharacter(char character) {
        for (int i = 0; i < Constants.VALID_PHONE_SPECIAL_CHARACTERS.length(); i++) {
            if (character == Constants.VALID_PHONE_SPECIAL_CHARACTERS.charAt(i)) {
                return true;
            }
        }

        return Character.isDigit(character);
    }

    public static String getSearchFormattedPhoneNumber(@NonNull String phoneNumber) {
        String formattedPhoneNumber = phoneNumber.replaceAll("[^\\d+]", "");
        return formattedPhoneNumber.replaceAll("\\+1", "");
    }

    public static void sortCallLogEntries(ArrayList<CallLogEntry> listCallLogEntries) {
        Collections.sort(listCallLogEntries, new Comparator<CallLogEntry>() {

            @Override
            public int compare(CallLogEntry lhs, CallLogEntry rhs) {
                if (rhs.getCallInstant() != null && lhs.getCallInstant() != null) {
                    return rhs.getCallInstant().compareTo(lhs.getCallInstant());
                } else {
                    return 0;
                }
            }
        });
    }

//    func phoneNumberEqualOurs() -> Bool {
//        var matches = false
//
//        if let countryCode = SessionManager.shared.sessionInfo?.profile.countryCode, let number = SessionManager.shared.sessionInfo?.profile.number {
//            matches = self.phoneRaw == (countryCode + number).phoneRaw
//
//        } else if let number = SessionManager.shared.sessionInfo?.profile.number {
//            if self.phoneRaw.count == 11 && self.phoneRaw.starts(with: "1") && number.phoneRaw.count == 10 {
//                matches = self.phoneRaw == "1" + number.phoneRaw
//
//            } else {
//                matches = self.phoneRaw == number.phoneRaw
//            }
//        }
//
//        return matches
//    }


    public static boolean isCountryCodeAdded(String number) {
        if (!TextUtils.isEmpty(number)) {
            number = CallUtil.getStrippedPhoneNumber(number);
            if (number.length() == 10) {
                return false;
            } else if ((number.length() >= 11 && number.charAt(0) == '1') || number.length() < 10) {
                return true;
            }
        }
        return false;
    }

    public static String getCountryCode(){
        return "1";
    }

    public static String getPlusCountryCode(){
        return "+" + getCountryCode();
    }

    public static String getFormattedNumber(@NonNull String phoneNumber) {
        return isCountryCodeAdded(phoneNumber) ? phoneNumber : "1" + phoneNumber;
    }

    public static boolean isRingerSilent(Context context)
    {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if(am.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
            return true;

        return false;
    }

    public static boolean isUserVibrateOn(Context context) {

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (Objects.requireNonNull(am).getRingerMode()) {
            case AudioManager.RINGER_MODE_VIBRATE:
            case AudioManager.RINGER_MODE_NORMAL:
                return true;
            case AudioManager.RINGER_MODE_SILENT:
            default:
                return false;
        }
    }

    public static boolean isValidSMSNumber(String phoneNumber){
        if (TextUtils.isEmpty(phoneNumber) || CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(phoneNumber).isEmpty() || CallUtil.cleanPhoneNumberAndRemoveUSCountryCode(phoneNumber).charAt(0) == '0') {
            return false;
        }

        if(phoneNumber.length() < 5 ||
                cleanPhoneNumberAndRemoveUSCountryCode(phoneNumber).length() > 10){
            return false;
        }

        Pattern digitsWithPlus = Pattern.compile("[^\\d+]");
        Matcher matcher = digitsWithPlus.matcher(cleanPhoneNumberAndRemoveUSCountryCode(phoneNumber));
        // boolean b = m.matches();
        if(matcher.find())
            return false;


        if((phoneNumber.charAt(0) == '1' && phoneNumber.length() < 9) || (phoneNumber.charAt(0) == '+' && phoneNumber.length() < 9))
        {
            return false;
        }


        if(phoneNumber.length() == 5 ||
        phoneNumber.length() == 6 ||
                cleanPhoneNumberAndRemoveUSCountryCode(phoneNumber).length() == 10)
        {
            return true;
        }


        return false;
    }

    public static boolean arePhoneNumbersEqual(String phoneNumberOne, String phoneNumberTwo) {
        if (phoneNumberOne != null) {
            phoneNumberOne = CallUtil.getStrippedNumberWithCountryCode(phoneNumberOne);
        }

        if (phoneNumberTwo != null) {
            phoneNumberTwo = CallUtil.getStrippedNumberWithCountryCode(phoneNumberTwo);
        }

        return TextUtils.equals(phoneNumberOne, phoneNumberTwo) ||
                TextUtils.equals(CallUtil.getCountryCode() + phoneNumberOne, phoneNumberTwo) ||
                TextUtils.equals(phoneNumberOne, CallUtil.getCountryCode() + phoneNumberTwo);
    }

    public static String getStrippedNumberWithCountryCode(String phoneNumber) {
        if (phoneNumber != null) {
            phoneNumber = CallUtil.getStrippedPhoneNumber(phoneNumber);
        }

        if (CallUtil.isCountryCodeAdded(phoneNumber)) {
            return phoneNumber;
        } else {
            return CallUtil.getCountryCode() + phoneNumber;
        }
    }

    public static String localeDefaultCountry(){

        String defaultCountry = (Locale.getDefault().getCountry() != null)? Locale.getDefault().getCountry() : Locale.ENGLISH.getCountry();
        return defaultCountry;
    }

    public static String phoneNumberFormatNumberDefaultCountry(String phoneNumber){
        if (TextUtils.isEmpty(phoneNumber) || !isValidSMSNumber(phoneNumber)) {
            return "";
        }

        return PhoneNumberUtils.formatNumber(phoneNumber, localeDefaultCountry());
    }

    public static String phoneNumberFormattedDefaultCountry(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber) || !isValidSMSNumber(phoneNumber)) {
            return "";
        }

        String formattedNumber;
        if (phoneNumber.length() == 10) {
            formattedNumber = "+1 " + PhoneNumberUtils.formatNumber(phoneNumber, localeDefaultCountry());
        } else if (phoneNumber.length() == 11) {
            String areaCode = phoneNumber.substring(1, 4);
            String centralOfficeCode = phoneNumber.substring(4, 7);
            String lineNumber = phoneNumber.substring(7);
            formattedNumber = "+1 (" + areaCode + ") " + centralOfficeCode + "-" + lineNumber;
        } else {
            formattedNumber = PhoneNumberUtils.formatNumber(phoneNumber, localeDefaultCountry());
        }

        return formattedNumber;
    }

    public static String getNumberWithExtensionFormatted(@NonNull String fullNumber) {
        if (TextUtils.isEmpty(fullNumber)) {
            return "";
        }

        String[] numbers = fullNumber.split("x", 2);
        String number = numbers[0].trim();
        String extension = numbers.length > 1 ? numbers[1] : "";

        String formattedNumber = PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry());
        if (TextUtils.isEmpty(formattedNumber)) {
            formattedNumber = number;
        }
        if (!TextUtils.isEmpty(extension)) {
            formattedNumber += "x" + extension;
        }
        LogUtil.d("CallUtil", "getNumberWithExtensionFormatted: " + formattedNumber);
        return formattedNumber;
    }

    /**
     * Formats a phone number already formatted by user. For example:
     * Input:  (763) 123 1234 || (562)5991853 || (562)599-1853
     * Output: +1 (763) 123-1234 || +1 (562) 599-1853 || +1 (562) 599-1853
     *
     * @param phoneNumber The phone number to format
     * @return The formatted phone number or null if the number is not valid
     */
    public static String formatPhoneNumberAlreadyFormattedByUser(String phoneNumber) {
        String phoneNumberRegex = "^\\(\\d{3}\\)\\s?\\d{3}[-\\s]?\\d{4}$";

        if (phoneNumber.matches(phoneNumberRegex)) {
            String cleanedNumber = phoneNumber.replaceAll("[^\\d]", "");
            String areaCode = cleanedNumber.substring(0, 3);
            String firstThreeDigits = cleanedNumber.substring(3, 6);
            String lastFourDigits = cleanedNumber.substring(6, 10);

            return "+1 (" + areaCode + ") " + firstThreeDigits + "-" + lastFourDigits;
        }
        return null;
    }

    public static boolean isExtensionNumber(String number) {
        if (number == null) {
            return false;
        }
        String cleanedNumber = number.replaceAll("\\s+", "");
        return cleanedNumber.matches("^(x|\\+)?\\d{2,7}$");
    }
}