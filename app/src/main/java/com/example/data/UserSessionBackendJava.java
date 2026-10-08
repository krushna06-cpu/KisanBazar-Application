package com.example.data;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Random;

/**
 * Pure Java Backend Manager for Persistent User Account, Role Routing & OTP Generation.
 * Ensures the user creates their account only once on initial install and opens directly
 * to their Seller or Consumer main dashboard on subsequent launches.
 */
public final class UserSessionBackendJava {

    private static final String KEY_HAS_ACCOUNT = "has_created_account";
    private static final String KEY_FULL_NAME = "user_full_name";
    private static final String KEY_PHONE = "user_phone";
    private static final String KEY_LOCATION = "user_location";
    private static final String KEY_ROLE = "user_role";
    private static final String KEY_LANG = "preferred_lang";

    private static final Random OTP_RANDOM = new Random();

    private UserSessionBackendJava() {
        // Static utility class
    }

    @NonNull
    public static AppLanguage loadSavedLanguage(@NonNull SharedPreferences prefs) {
        String code = prefs.getString(KEY_LANG, "mr");
        if ("en".equalsIgnoreCase(code)) {
            return AppLanguage.ENGLISH;
        } else if ("hi".equalsIgnoreCase(code)) {
            return AppLanguage.HINDI;
        }
        return AppLanguage.MARATHI;
    }

    @Nullable
    public static UserSession loadSavedSessionFromPrefs(@NonNull SharedPreferences prefs) {
        boolean hasAccount = prefs.getBoolean(KEY_HAS_ACCOUNT, false);
        if (!hasAccount) {
            return null;
        }
        String name = prefs.getString(KEY_FULL_NAME, null);
        String phone = prefs.getString(KEY_PHONE, null);
        if (name == null || name.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
            return null;
        }
        String location = prefs.getString(KEY_LOCATION, "महाराष्ट्र");
        if (location == null || location.trim().isEmpty()) {
            location = "महाराष्ट्र";
        }
        String roleStr = prefs.getString(KEY_ROLE, "FARMER");
        UserRole role = "CONSUMER".equalsIgnoreCase(roleStr) ? UserRole.CONSUMER : UserRole.FARMER;

        return new UserSession(
                name,
                phone,
                location,
                role,
                true
        );
    }

    public static void saveSessionToPrefs(
            @NonNull SharedPreferences prefs,
            @NonNull UserSession session,
            @NonNull AppLanguage lang
    ) {
        prefs.edit()
                .putBoolean(KEY_HAS_ACCOUNT, true)
                .putString(KEY_FULL_NAME, session.getFullName())
                .putString(KEY_PHONE, session.getPhoneNumber())
                .putString(KEY_LOCATION, session.getVillageOrCity())
                .putString(KEY_ROLE, session.getRole().name())
                .putString(KEY_LANG, lang.getCode())
                .apply();
    }

    public static void savePreferredLanguage(
            @NonNull SharedPreferences prefs,
            @NonNull AppLanguage lang
    ) {
        prefs.edit().putString(KEY_LANG, lang.getCode()).apply();
    }

    public static void clearSessionOnLogout(
            @NonNull SharedPreferences prefs,
            @NonNull AppLanguage currentLang
    ) {
        prefs.edit()
                .clear()
                .putString(KEY_LANG, currentLang.getCode())
                .apply();
    }

    @NonNull
    public static UserProfileEntity toUserProfileEntity(
            @NonNull UserSession session,
            @NonNull AppLanguage lang
    ) {
        return new UserProfileEntity(
                1,
                session.getFullName(),
                session.getPhoneNumber(),
                session.getVillageOrCity(),
                session.getRole().name(),
                lang.getCode(),
                true,
                System.currentTimeMillis()
        );
    }

    @NonNull
    public static UserSession fromUserProfileEntity(@NonNull UserProfileEntity entity) {
        UserRole role = "CONSUMER".equalsIgnoreCase(entity.getRole())
                ? UserRole.CONSUMER
                : UserRole.FARMER;
        return new UserSession(
                entity.getFullName(),
                entity.getPhoneNumber(),
                entity.getVillageOrCity(),
                role,
                entity.getVerifiedByOtp()
        );
    }

    /**
     * Generates a random 6-digit OTP code for phone number verification.
     */
    @NonNull
    public static String generateSixDigitOtp() {
        int code = 100000 + OTP_RANDOM.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Formats the bilingual SMS OTP text sent to the user's mobile number.
     */
    @NonNull
    public static String buildOtpSmsText(@NonNull String otpCode, boolean isMarathi) {
        if (isMarathi) {
            return "किसानबाजार: तुमचा ६-अंकी लॉगिन OTP " + otpCode + " आहे. कोणाशीही शेअर करू नका.";
        } else {
            return "KisanBazar: Your 6-digit login OTP is " + otpCode + ". Do not share it with anyone.";
        }
    }
}
