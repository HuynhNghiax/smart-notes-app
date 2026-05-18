package com.example.smartnotesfrontend.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.smartnotesfrontend.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtForgotEmail;
    private Button btnSendOtp;
    private TextView tvBackToLoginLink;
    private AuthViewModel authViewModel;
    private String savedEmail; // Biến lưu email để truyền sang màn hình đặt lại mật khẩu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        edtForgotEmail = findViewById(R.id.edtForgotEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        tvBackToLoginLink = findViewById(R.id.tvBackToLoginLink);

        tvBackToLoginLink.setOnClickListener(v -> finish());
        btnSendOtp.setOnClickListener(v -> handleForgotPassword());

        authViewModel.getAuthResult().observe(this, result -> {
            if (result != null) {
                if (result.equals("OTP_SENT_SUCCESS")) {
                    Toast.makeText(this, "Mã OTP khôi phục đã được gửi vào Email của bạn!", Toast.LENGTH_LONG).show();

                    // CHỈNH SỬA: Chuyển hướng thông minh sang màn hình ResetPasswordActivity kèm theo Email dữ liệu
                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", savedEmail);
                    startActivity(intent);

                    authViewModel.clearResult(); // Giải phóng trạng thái thông báo LiveData
                    finish(); // Khép vòng đời màn hình này lại
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void handleForgotPassword() {
        String email = edtForgotEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtForgotEmail.setError("Vui lòng nhập chính xác định dạng Email");
            return;
        }

        savedEmail = email; // Lưu trữ email tạm thời
        Toast.makeText(this, "Đang gửi yêu cầu mã xác thực...", Toast.LENGTH_SHORT).show();

        // Gọi hàm kết nối mạng thật từ ViewModel
        authViewModel.requestOtp(email);
    }
}