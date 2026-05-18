package com.example.smartnotesfrontend.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "SmartNotesPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private static SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    // Hàm lưu Token khi đăng nhập thành công
    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    // Hàm lấy Token ra để xài (Thành viên 2 và 3 sẽ gọi hàm này)
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // Hàm xóa Token khi Đăng xuất
    public void clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply();
    }
}