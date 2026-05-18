package com.example.smartnotesfrontend.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.smartnotesfrontend.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtOtpCode, edtNewPassword, edtConfirmNewPassword;
    private Button btnConfirmReset;
    private AuthViewModel authViewModel;
    private String userEmail;

    @Override
    protected void onCreate(Bundle Bundle) {
        super.onCreate(Bundle);
        setContentView(R.layout.activity_reset_password);

        // Hứng Email được truyền từ màn hình ForgotPasswordActivity sang
        userEmail = getIntent().getStringExtra("email");

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        edtOtpCode = findViewById(R.id.edtOtpCode);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);
        btnConfirmReset = findViewById(R.id.btnConfirmReset);

        // Bấm nút thực hiện đổi mật khẩu hệ thống
        btnConfirmReset.setOnClickListener(v -> handleResetPassword());

        // Lắng nghe tín hiệu trả về từ Server thật
        authViewModel.getAuthResult().observe(this, result -> {
            if (result != null) {
                if (result.equals("RESET_SUCCESS")) {
                    Toast.makeText(this, "Đổi mật khẩu thành công! Mời đăng nhập lại.", Toast.LENGTH_LONG).show();

                    // Đưa người dùng quay ngược lại màn hình đăng nhập ban đầu
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    authViewModel.clearResult();
                    finish();
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void handleResetPassword() {
        String otp = edtOtpCode.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(otp) || otp.length() < 6) {
            edtOtpCode.setError("Vui lòng nhập đúng mã xác thực 6 số");
            return;
        }
        if (newPassword.length() < 6) {
            edtNewPassword.setError("Mật khẩu mới phải từ 6 ký tự trở lên");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            edtConfirmNewPassword.setError("Mật khẩu xác nhận không trùng khớp");
            return;
        }

        Toast.makeText(this, "Đang gửi yêu cầu thay đổi mật khẩu...", Toast.LENGTH_SHORT).show();

        // Gọi API kết nối mạng thời gian thực xuống Server Spring Boot
        authViewModel.resetPassword(userEmail, otp, newPassword);
    }
}