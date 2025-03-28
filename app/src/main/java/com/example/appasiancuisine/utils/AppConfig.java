package com.example.appasiancuisine.utils;

public class AppConfig {

    public static final String BASE_URL = "http://192.168.1.12:8080";

    public static final String REGISTER_URL = BASE_URL + "/api/auth/register";
    // Đường dẫn đầy đủ API login
    public static final String LOGIN_URL = BASE_URL + "/api/auth/login";
    // Đường dẫn đầy đủ API refresh token
    public static final String REFRESH_TOKEN_URL = BASE_URL + "/api/auth/refresh-token";
    // Đường dẫn đầy đủ API home
    public static final String Home_URL = BASE_URL + "/api/home";
    // Đường dẫn đầy đủ API home
    public static final String ProductDetail_URL = BASE_URL + "/api/products";

    // ✅ Reset Password APIs
    public static final String SEND_OTP_URL = BASE_URL + "/api/reset/send-otp";
    public static final String VERIFY_OTP_URL = BASE_URL + "/api/reset/verify-otp";
    public static final String RESET_PASSWORD_URL = BASE_URL + "/api/reset/reset-password";
}
