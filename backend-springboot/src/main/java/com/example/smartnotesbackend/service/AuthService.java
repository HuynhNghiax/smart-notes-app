package com.example.smartnotesbackend.service;

import com.example.smartnotesbackend.entity.User;
import com.example.smartnotesbackend.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender; // CHÍNH XÁC: Đã sửa lại đường dẫn import ở đây
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender; // Công cụ gửi thư của Spring

    // Inject các dependency vào Constructor
    public AuthService(UserRepository userRepository, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    // Hàm phụ trợ thực hiện gửi mail ngầm
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

    // Logic Đăng ký tài khoản mới và tự động gửi OTP có hạn 60 giây vào Email thật
    public String registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "EMAIL_ALREADY_EXISTS";
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password); 
        user.setEnabled(false); 

        String randomOtp = String.format("%06d", new Random().nextInt(999999));
        user.setOtpCode(randomOtp);
        user.setOtpExpiry(LocalDateTime.now().plusSeconds(60)); // THỜI HẠN CHÍNH XÁC 60 GIÂY

        userRepository.save(user);

        // Gọi lệnh gửi thư thực tế đến hộp thư Gmail của người dùng
        String mailSubject = "[Smart Notes] Mã OTP Kích Hoạt Tài Khoản Của Bạn";
        String mailContent = "Chào bạn,\n\nMã OTP kích hoạt tài khoản Smart Notes của bạn là: " + randomOtp 
                           + "\nMã số này có hiệu lực trong vòng 60 giây. Vui lòng không chia sẻ mã này cho ai.";
        
        sendEmail(email, mailSubject, mailContent);

        return "REGISTER_SUCCESS";
    }

    // Logic Kiểm tra mã OTP nhập từ ứng dụng điện thoại gửi lên
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

    // Logic Đăng nhập hệ thống
    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "WRONG_CREDENTIALS";
        }

        User user = userOpt.get();

        if (!user.isEnabled()) {
            return "ACCOUNT_NOT_ACTIVATED";
        }
        
        if (user.getPassword().equals(password)) {
            return "MOCK_JWT_TOKEN_FOR_SMART_NOTES_PROJECT_2026"; 
        }
        return "WRONG_CREDENTIALS";
    }

    // Xử lý yêu cầu tạo mã OTP khôi phục khi Quên mật khẩu và gửi Email thật
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

        // Gửi email khôi phục mật khẩu thật
        String mailSubject = "[Smart Notes] Yêu Cầu Đặt Lại Mật Khẩu";
        String mailContent = "Bạn vừa yêu cầu đặt lại mật khẩu.\nMã OTP xác minh khôi phục của bạn là: " + recoveryOtp 
                           + "\nMã số này có hiệu lực trong vòng 5 phút.";
        
        sendEmail(email, mailSubject, mailContent);

        return "OTP_SENT_SUCCESS";
    }

    // Kiểm tra OTP khôi phục và tiến hành Đặt lại mật khẩu mới
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

        user.setPassword(newPassword);
        user.setOtpCode(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        return "RESET_SUCCESS";
    }
}