package com.example.smartnotesbackend.controller;

import com.example.smartnotesbackend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // API tiếp nhận yêu cầu Đăng ký tài khoản mới
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String result = authService.registerUser(request.get("email"), request.get("password"));
        if (result.equals("EMAIL_ALREADY_EXISTS")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email này đã được đăng ký trên hệ thống"));
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

    // API tiếp nhận yêu cầu Đăng nhập hệ thống
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String result = authService.loginUser(request.get("email"), request.get("password"));
        if (result.equals("ACCOUNT_NOT_ACTIVATED")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản của bạn chưa được kích hoạt bằng mã OTP"));
        }
        if (result.equals("WRONG_CREDENTIALS")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tài khoản hoặc mật khẩu không chính xác"));
        }
        return ResponseEntity.ok(Map.of("token", result));
    }

    // BỔ SUNG CHÍNH XÁC: API tiếp nhận yêu cầu Quên mật khẩu (Sinh mã OTP khôi phục)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String result = authService.requestForgotPassword(request.get("email"));
        if (result.equals("USER_NOT_FOUND")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email này chưa được đăng ký trong hệ thống!"));
        }
        return ResponseEntity.ok(Map.of("message", "Mã OTP khôi phục mật khẩu đã được tạo thành công!"));
    }

    // BỔ SUNG CHÍNH XÁC: API tiếp nhận yêu cầu Đặt lại mật khẩu mới tinh
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
}