package com.example.smartnotesfrontend.data.remote;

import com.example.smartnotesfrontend.data.model.RegisterRequest;
import com.example.smartnotesfrontend.data.model.AiResponse;
import com.example.smartnotesfrontend.data.model.Note;

import java.util.Map;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @POST("api/auth/register")
    Call<Map<String, String>> register(@Body RegisterRequest body);

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

   // CATEGORY APIs
    @POST("api/categories/create")
    Call<Map<String, Object>> createCategory(@Header("Authorization") String token, @Body Map<String, String> body);

    @GET("api/categories/list")
    Call<List<Map<String, Object>>> getCategories(@Header("Authorization") String token);

    @PUT("api/categories/update/{categoryId}")
    Call<Map<String, Object>> updateCategory(@Header("Authorization") String token, @Path("categoryId") Long categoryId, @Body Map<String, String> body);

    @DELETE("api/categories/delete/{categoryId}")
    Call<Map<String, String>> deleteCategory(@Header("Authorization") String token, @Path("categoryId") Long categoryId);

    // SEARCH APIs
    @GET("api/search/notes")
    Call<List<Map<String, Object>>> searchNotes(@Header("Authorization") String token, @Query("keyword") String keyword);

    @GET("api/search/pinned")
    Call<List<Map<String, Object>>> getPinnedNotes(@Header("Authorization") String token);

    @GET("api/search/category/{categoryId}")
    Call<List<Map<String, Object>>> getNotesByCategory(@Header("Authorization") String token, @Path("categoryId") Long categoryId);

    // Trong file ApiService.java
    @POST("api/ai/summarize")
    Call<List<String>> summarizeNote(@Body Map<String, String> request);
}