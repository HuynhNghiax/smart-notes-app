package com.example.smartnotesbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = false; // Mặc định tài khoản mới tạo sẽ bị khóa cho đến khi nhập đúng OTP

    @Column(name = "otp_code")
    private String otpCode; // Lưu mã OTP 6 số tạm thời để đối chiếu

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry; // Mốc thời gian hết hạn của OTP (Thời gian tạo + 60 giây)
}