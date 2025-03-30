package com.example.appasiancuisine.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final String PREF_NAME = "intro_pref";
    private static final String KEY_FIRST_TIME = "isFirstTimeLaunch";

    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_AVATAR_URI = "avatarUri";

    private static final String KEY_RESET_PHONE = "reset_phone"; // 👈 Thêm key

    // 🔑 Thêm các key mới để lưu trạng thái sinh trắc học
    private static final String KEY_FINGERPRINT_ENABLED = "fingerprint_enabled";
    private static final String KEY_FACE_RECOGNITION_ENABLED = "face_recognition_enabled";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ---------- First time launch ----------
    public boolean isFirstTimeLaunch() {
        return prefs.getBoolean(KEY_FIRST_TIME, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(KEY_FIRST_TIME, isFirstTime);
        editor.apply();
    }

    // ---------- Save login info ----------
    public void saveLoginData(String accessToken, String refreshToken, String username, String email, String role) {
        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, null);
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null && !getAccessToken().isEmpty();
    }

    public void clearLoginData() {
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_REFRESH_TOKEN);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_ROLE);
        editor.apply();
    }

    // ---------- Avatar ----------
    public void saveAvatarUri(String uri) {
        editor.putString(KEY_AVATAR_URI, uri);
        editor.apply();
    }

    public String getAvatarUri() {
        return prefs.getString(KEY_AVATAR_URI, null);
    }

    // ---------- Reset Password ----------
    public void saveResetPhone(String phone) {
        editor.putString(KEY_RESET_PHONE, phone);
        editor.apply();
    }

    public String getResetPhone() {
        return prefs.getString(KEY_RESET_PHONE, null);
    }

    public void clearResetPhone() {
        editor.remove(KEY_RESET_PHONE);
        editor.apply();
    }

    public String getPhone() {
        return prefs.getString("reset_phone", null);
    }

    // ==============================
    // 🔐 Quản lý trạng thái sinh trắc học
    // ==============================

    // Lưu trạng thái vân tay
    public void setFingerprintEnabled(boolean isEnabled) {
        editor.putBoolean(KEY_FINGERPRINT_ENABLED, isEnabled);
        editor.apply();
    }

    public boolean isFingerprintEnabled() {
        return prefs.getBoolean(KEY_FINGERPRINT_ENABLED, false);
    }

    // Lưu trạng thái nhận diện khuôn mặt
    public void setFaceRecognitionEnabled(boolean isEnabled) {
        editor.putBoolean(KEY_FACE_RECOGNITION_ENABLED, isEnabled);
        editor.apply();
    }

    public boolean isFaceRecognitionEnabled() {
        return prefs.getBoolean(KEY_FACE_RECOGNITION_ENABLED, false);
    }
}
