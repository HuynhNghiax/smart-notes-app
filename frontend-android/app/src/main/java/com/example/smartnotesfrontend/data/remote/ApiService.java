package com.example.smartnotesfrontend.data.remote;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/register")
    Call<Map<String, String>> register(@Body Map<String, String> body);

    @POST("api/auth/verify-otp")
    Call<Map<String, String>> verifyOtp(@Body Map<String, String> body);

    @POST("api/auth/login")
    Call<Map<String, String>> login(@Body Map<String, String> body);

    @POST("api/auth/forgot-password")
    Call<Map<String, String>> forgotPassword(@Body Map<String, String> body);

    @POST("api/auth/reset-password")
    Call<Map<String, String>> resetPassword(@Body Map<String, String> body);

    @POST("api/auth/google")
    Call<Map<String, String>> loginWithGoogle(@Body Map<String, String> body);
}