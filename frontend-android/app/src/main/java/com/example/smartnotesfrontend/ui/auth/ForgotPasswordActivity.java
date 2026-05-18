package com.example.smartnotesfrontend.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartnotesfrontend.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView tvBackToLoginLink;
    private Button btnSendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Ánh xạ thành phần từ XML
        tvBackToLoginLink = findViewById(R.id.tvBackToLoginLink);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        // Click để quay lại màn hình Đăng nhập
        tvBackToLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình Quên mật khẩu, quay về Login
            }
        });

        // Nút gửi OTP (tạm thời để trống)
        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý gọi API gửi OTP ở đây
            }
        });
    }
}