package com.example.smartnotesfrontend.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartnotesfrontend.data.model.RegisterRequest;
import com.example.smartnotesfrontend.data.remote.RetrofitClient;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel {

    private final MutableLiveData<String> authResult = new MutableLiveData<>();
    public LiveData<String> getAuthResult() { return authResult; }

    public void clearResult() {
        authResult.setValue(null);
    }

    public void loginUser(String email, String password) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String deviceToken = task.isSuccessful() ? task.getResult() : "";
            Map<String, String> body = new HashMap<>();
            body.put("email", email);
            body.put("password", password);
            body.put("deviceToken", deviceToken);

            RetrofitClient.getApiService().login(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().get("token");
                        authResult.setValue("LOGIN_SUCCESS:" + token);
                    } else {
                        authResult.setValue("Lỗi: Tài khoản/mật khẩu sai hoặc tài khoản chưa kích hoạt!");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    authResult.setValue("Thất bại: Server Spring Boot chưa bật hoặc sai IP!");
                }
            });
        });
    }

    public void registerUser(String email, String password) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String deviceToken = task.isSuccessful() ? task.getResult() : "";
            RegisterRequest request = new RegisterRequest(email, password, deviceToken);

            RetrofitClient.getApiService().register(request).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful()) {
                        authResult.setValue("REGISTER_SUCCESS");
                    } else {
                        authResult.setValue("Lỗi: Email này đã tồn tại trong Database!");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    authResult.setValue("Thất bại: Không thể kết nối tới Server Spring Boot!");
                }
            });
        });
    }

    public void verifyOtp(String email, String otp) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("otp", otp);

        RetrofitClient.getApiService().verifyOtp(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) { // CHÍNH XÁC: Đã xóa chữ String thừa tại đây
                if (response.isSuccessful()) {
                    authResult.setValue("VERIFY_SUCCESS");
                } else {
                    authResult.setValue("Lỗi: Mã OTP không chính xác hoặc đã quá hạn 60 giây!");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                authResult.setValue("Thất bại: Mất kết nối mạng lên Server!");
            }
        });
    }

    public void requestOtp(String email) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        RetrofitClient.getApiService().forgotPassword(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    authResult.setValue("OTP_SENT_SUCCESS");
                } else {
                    authResult.setValue("Lỗi: Email này chưa được đăng ký trong hệ thống!");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                authResult.setValue("Thất bại: Không thể kết nối tới Server mạng local!");
            }
        });
    }

    public void resetPassword(String email, String otp, String newPassword) {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("otp", otp);
        body.put("newPassword", newPassword);

        RetrofitClient.getApiService().resetPassword(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    authResult.setValue("RESET_SUCCESS");
                } else {
                    authResult.setValue("Lỗi: Mã OTP khôi phục không đúng hoặc đã hết hạn!");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                authResult.setValue("Thất bại: Không thể kết nối đường truyền tới Server!");
            }
        });
    }

    public void loginWithGoogle(String idToken) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String deviceToken = task.isSuccessful() ? task.getResult() : "";
            Map<String, String> body = new HashMap<>();
            body.put("idToken", idToken);
            body.put("deviceToken", deviceToken);

            RetrofitClient.getApiService().loginWithGoogle(body).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().get("token");
                        authResult.setValue("LOGIN_SUCCESS:" + token);
                    } else {
                        authResult.setValue("Lỗi: Server Google từ chối xác thực Token này!");
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    authResult.setValue("Thất bại: Không thể gửi Token Google lên Server Spring Boot!");
                }
            });
        });
    }
}