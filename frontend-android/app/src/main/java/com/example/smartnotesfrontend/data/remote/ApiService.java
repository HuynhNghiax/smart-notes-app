package com.example.smartnotesfrontend.data.remote;

import com.example.smartnotesfrontend.data.model.Note;

import java.util.Map;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;
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

    // NOTES APIs

    @GET("api/notes/{userId}")
    Call<List<Note>> getNotes(@Path("userId") Long userId);

    @POST("api/notes/{userId}")
    Call<Note> createNote(
            @Path("userId") Long userId,
            @Body Note note
    );

    @PUT("api/notes/{id}")
    Call<Note> updateNote(
            @Path("id") Long id,
            @Body Note note
    );

    @DELETE("api/notes/{id}")
    Call<String> deleteNote(@Path("id") Long id);
}