package com.example.smartnotesfrontend.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartnotesfrontend.data.model.AuthResponse;
import com.example.smartnotesfrontend.data.model.LoginRequest;
import com.example.smartnotesfrontend.data.model.RegisterRequest;
import com.example.smartnotesfrontend.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    private final MutableLiveData<String> authResult = new MutableLiveData<>();
    public LiveData<String> getAuthResult() { return authResult; }

    public void loginUser(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        RetrofitClient.getApiService().login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    authResult.setValue("LOGIN_SUCCESS:" + response.body().getToken());
                } else {
                    authResult.setValue("Lỗi: Sai tài khoản hoặc mật khẩu");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                authResult.setValue("Lỗi kết nối Server: " + t.getMessage());
            }
        });
    }

    public void registerUser(String email, String password) {
        RegisterRequest request = new RegisterRequest(email, password);
        RetrofitClient.getApiService().register(request).enqueue(new Callback<RegisterRequest>() {
            @Override
            public void onResponse(Call<RegisterRequest> call, Response<RegisterRequest> response) {
                if (response.isSuccessful()) {
                    authResult.setValue("REGISTER_SUCCESS");
                } else {
                    authResult.setValue("Lỗi: Email đã được sử dụng");
                }
            }

            @Override
            public void onFailure(Call<RegisterRequest> call, Throwable t) {
                authResult.setValue("Lỗi kết nối Server: " + t.getMessage());
            }
        });
    }

    public void requestOtp(String email) {
        RetrofitClient.getApiService().forgotPassword(email).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    authResult.setValue("OTP_SENT_SUCCESS");
                } else {
                    authResult.setValue("Lỗi: Email không tồn tại trên hệ thống");
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                authResult.setValue("Lỗi kết nối Server: " + t.getMessage());
            }
        });
    }
}