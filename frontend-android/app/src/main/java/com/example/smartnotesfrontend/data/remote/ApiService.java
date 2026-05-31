package com.example.smartnotesfrontend.data.remote;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;

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

     // Category APIs (Thành viên 3)
    @POST("api/categories/create")
    Call<Map<String, Object>> createCategory(@Header("Authorization") String token, @Body Map<String, String> body);

    @GET("api/categories/list")
    Call<List<Map<String, Object>>> getCategories(@Header("Authorization") String token);

    @PUT("api/categories/update/{categoryId}")
    Call<Map<String, Object>> updateCategory(@Header("Authorization") String token, @Path("categoryId") Long categoryId, @Body Map<String, String> body);

    @DELETE("api/categories/delete/{categoryId}")
    Call<Map<String, String>> deleteCategory(@Header("Authorization") String token, @Path("categoryId") Long categoryId);

    // Search APIs (Thành viên 3)
    @GET("api/search/notes")
    Call<List<Map<String, Object>>> searchNotes(@Header("Authorization") String token, @Query("keyword") String keyword);

    @GET("api/search/pinned")
    Call<List<Map<String, Object>>> getPinnedNotes(@Header("Authorization") String token);

    @GET("api/search/category/{categoryId}")
    Call<List<Map<String, Object>>> getNotesByCategory(@Header("Authorization") String token, @Path("categoryId") Long categoryId);

}