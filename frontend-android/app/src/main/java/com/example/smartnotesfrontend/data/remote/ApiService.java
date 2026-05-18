package com.example.smartnotesfrontend.data.remote;

import com.example.smartnotesfrontend.data.model.AuthResponse;
import com.example.smartnotesfrontend.data.model.LoginRequest;
import com.example.smartnotesfrontend.data.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<RegisterRequest> register(@Body RegisterRequest request);

    @POST("api/auth/forgot-password")
    Call<AuthResponse> forgotPassword(@Query("email") String email);
}