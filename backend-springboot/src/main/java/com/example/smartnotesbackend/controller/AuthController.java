package com.example.smartnotesbackend.controller;

import com.example.smartnotesbackend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private static final String GOOGLE_CLIENT_ID = "648616238662-u9uuojremvv9leppin6pm3daqu16416j.apps.googleusercontent.com";

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // API tiếp nhận yêu cầu Đăng ký tài khoản mới (với device token tùy chọn)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String deviceToken = request.get("deviceToken");

        String result = authService.registerUser(email, password);
        if (result.equals("EMAIL_ALREADY_EXISTS")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email này đã được đăng ký trên hệ thống"));
        }

        // Lưu device token nếu có
        if (deviceToken != null && !deviceToken.isEmpty()) {
            authService.saveDeviceTokenForNewUser(email, deviceToken);
        }

        return ResponseEntity.ok(Map.of("message", "Đăng ký thành công, mã kích hoạt đã được khởi tạo!"));
    }

    // API tiếp nhận mã OTP để Kích hoạt tài khoản (Bắt lỗi quá hạn 60 giây)
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String result = authService.verifyOtp(request.get("email"), request.get("otp"));
        if (result.equals("OTP_EXPIRED")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã OTP đã hết hạn 60 giây! Vui lòng bấm gửi lại mã mới."));
        }
        if (result.equals("INVALID_OTP")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã kích hoạt nhập vào không chính xác"));
        }
        return ResponseEntity.ok(Map.of("message", "Tài khoản của bạn đã được kích hoạt thành công!"));
    }

    // API tiếp nhận yêu cầu Đăng nhập hệ thống (với device token tùy chọn)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String deviceToken = request.get("deviceToken");

        String result = authService.loginUser(email, password);
        if (result.equals("ACCOUNT_NOT_ACTIVATED")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản của bạn chưa được kích hoạt bằng mã OTP"));
        }
        if (result.equals("WRONG_CREDENTIALS")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản hoặc mật khẩu không chính xác"));
        }

        // Lưu device token nếu có
        if (deviceToken != null && !deviceToken.isEmpty()) {
            authService.saveDeviceTokenForEmail(email, deviceToken);
        }

        return ResponseEntity.ok(Map.of("token", result));
    }

    // API tiếp nhận yêu cầu Quên mật khẩu (Sinh mã OTP khôi phục)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String result = authService.requestForgotPassword(request.get("email"));
        if (result.equals("USER_NOT_FOUND")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email này chưa được đăng ký trong hệ thống!"));
        }
        return ResponseEntity.ok(Map.of("message", "Mã OTP khôi phục mật khẩu đã được tạo thành công!"));
    }

    // API tiếp nhận yêu cầu Đặt lại mật khẩu mới tinh
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String result = authService.resetPassword(request.get("email"), request.get("otp"), request.get("newPassword"));
        if (result.equals("USER_NOT_FOUND")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Không tìm thấy thông tin tài khoản người dùng!"));
        }
        if (result.equals("OTP_EXPIRED")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã OTP xác minh khôi phục mật khẩu đã hết hạn!"));
        }
        if (result.equals("INVALID_OTP")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã OTP nhập vào để khôi phục không chính xác!"));
        }
        return ResponseEntity.ok(Map.of("message", "Mật khẩu của bạn đã được thay đổi thành công!"));
    }

    //HÀM NHẬN TOKEN GOOGLE TỪ ANDROID GỬI LÊN VÀ XÁC THỰC TOKEN NÀY VỚI GOOGLE, NẾU HỢP LỆ THÌ TỰ ĐỘNG ĐĂNG NHẬP/ĐĂNG KÝ NGẦM CHO NGƯỜI DÙNG
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        String idTokenString = request.get("idToken");
        String deviceToken = request.get("deviceToken");

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();

                // Liên thông xuống AuthService tự động đăng nhập/đăng ký ngầm
                String mockToken = authService.loginOrRegisterWithGoogle(email);

                // Lưu device token nếu có
                if (deviceToken != null && !deviceToken.isEmpty()) {
                    authService.saveDeviceTokenForEmail(email, deviceToken);
                }

                return ResponseEntity.ok(Map.of("token", mockToken));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Chứng chỉ Token Google gửi lên không hợp lệ!"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi xử lý xác thực hệ thống Google: " + e.getMessage()));
        }
    }

    // API quản lý device token (thêm/cập nhật)
    @PostMapping("/device-token")
    public ResponseEntity<?> saveDeviceToken(@RequestHeader("Authorization") String token,
                                            @RequestBody com.example.smartnotesbackend.dto.DeviceTokenDTO dto) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = authService.extractUserIdFromToken(jwtToken);

            authService.saveOrUpdateDeviceToken(userId, dto.getDeviceToken());
            return ResponseEntity.ok(Map.of("message", "Device token saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save device token: " + e.getMessage()));
        }
    }

    // API xóa device token
    @DeleteMapping("/device-token")
    public ResponseEntity<?> deleteDeviceToken(@RequestHeader("Authorization") String token,
                                              @RequestBody com.example.smartnotesbackend.dto.DeviceTokenDTO dto) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Long userId = authService.extractUserIdFromToken(jwtToken);

            authService.deleteDeviceToken(userId, dto.getDeviceToken());
            return ResponseEntity.ok(Map.of("message", "Device token deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete device token: " + e.getMessage()));
        }
    }
}