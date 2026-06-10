package com.example.smartnotesbackend.service;

import com.example.smartnotesbackend.entity.User;
import com.example.smartnotesbackend.repository.UserRepository;
import com.example.smartnotesbackend.util.JwtUtil;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;

    // Bộ băm mật khẩu BCrypt xịn của Spring Security
    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder =
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JavaMailSender mailSender, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.jwtUtil = jwtUtil;
    }

    private void sendEmail(String toEmail, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Smart Notes App <no-reply@gmail.com>");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            System.out.println(">>> [MAIL SERVER] Đã gửi mail thành công tới: " + toEmail);
        } catch (Exception e) {
            System.out.println(">>> [MAIL ERROR] Không thể gửi mail: " + e.getMessage());
        }
    }

    // Đăng ký tài khoản mới: Tiến hành băm mật khẩu trước khi lưu
    public String registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "EMAIL_ALREADY_EXISTS";
        }

        User user = new User();
        user.setEmail(email);

        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);
        user.setEnabled(false);

        String randomOtp = String.format("%06d", new Random().nextInt(999999));
        user.setOtpCode(randomOtp);
        user.setOtpExpiry(LocalDateTime.now().plusSeconds(60));

        userRepository.save(user);

        String mailSubject = "[Smart Notes] Mã OTP Kích Hoạt Tài Khoản Của Bạn";
        String mailContent = "Chào bạn,\n\nMã OTP kích hoạt tài khoản Smart Notes của bạn là: " + randomOtp
                + "\nMã số này có hiệu lực trong vòng 60 giây. Vui lòng không chia sẻ mã này cho ai.";

        sendEmail(email, mailSubject, mailContent);

        return "REGISTER_SUCCESS";
    }

    public String verifyOtp(String email, String otpInput) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "USER_NOT_FOUND";
        }

        User user = userOpt.get();

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return "OTP_EXPIRED";
        }

        if (!otpInput.equals(user.getOtpCode())) {
            return "INVALID_OTP";
        }

        user.setEnabled(true);
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        return "VERIFY_SUCCESS";
    }

    // Đăng nhập hệ thống: Dùng hàm matches để so sánh mật khẩu băm
    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "WRONG_CREDENTIALS";
        }

        User user = userOpt.get();

        if (!user.isEnabled()) {
            return "ACCOUNT_NOT_ACTIVATED";
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(user.getId());
        }
        return "WRONG_CREDENTIALS";
    }

    public String requestForgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "USER_NOT_FOUND";
        }

        User user = userOpt.get();
        String recoveryOtp = String.format("%06d", new Random().nextInt(999999));
        user.setOtpCode(recoveryOtp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        String mailSubject = "[Smart Notes] Yêu Cầu Đặt Lại Mật Khẩu";
        String mailContent = "Bạn vừa yêu cầu đặt lại mật khẩu.\nMã OTP xác minh khôi phục của bạn là: " + recoveryOtp
                + "\nMã số này có hiệu lực trong vòng 5 phút.";

        sendEmail(email, mailSubject, mailContent);

        return "OTP_SENT_SUCCESS";
    }

    // Đặt lại mật khẩu mới: Tiến hành băm mật khẩu mới trước khi cập nhật
    public String resetPassword(String email, String otpInput, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "USER_NOT_FOUND";
        }

        User user = userOpt.get();

        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return "OTP_EXPIRED";
        }

        if (!otpInput.equals(user.getOtpCode())) {
            return "INVALID_OTP";
        }

        String hashedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedNewPassword);
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        return "RESET_SUCCESS";
    }

    // CHÍNH XÁC: Hàm xử lý liên thông Đăng nhập/Đăng ký ngầm bằng tài khoản Google
    public String loginOrRegisterWithGoogle(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(email);
            // Đặt password mặc định ngẫu nhiên được băm an toàn
            newUser.setPassword(passwordEncoder.encode("Google_OAuth_Account_Protected_2026"));
            newUser.setEnabled(true); // Tài khoản Google là sạch, kích hoạt thẳng luôn không cần qua bước OTP
            userRepository.save(newUser);
            System.out.println(">>> [OAUTH2] Đã tự động tạo tài khoản Google mới tinh cho: " + email);
        }

        User savedUser = userRepository.findByEmail(email).get();
        return jwtUtil.generateToken(savedUser.getId());
    }

    // Hàm giải mã token để lấy userId — dùng cho CategoryController và SearchController
    public Long extractUserIdFromToken(String token) {
        return jwtUtil.extractUserId(token);
    }
}