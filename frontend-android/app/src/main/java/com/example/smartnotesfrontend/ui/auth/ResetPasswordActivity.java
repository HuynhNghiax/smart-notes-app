package com.example.smartnotesfrontend.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartnotesfrontend.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtResetOtpCode, edtNewPassword, edtConfirmNewPassword;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Ánh xạ toàn bộ giao diện từ XML sang Java
        edtResetOtpCode = findViewById(R.id.edtResetOtpCode);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        // Bắt sự kiện khi người dùng nhấn nút Đổi mật khẩu
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleResetPassword();
            }
        });
    }

    private void handleResetPassword() {
        String otp = edtResetOtpCode.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmNewPassword.getText().toString().trim();

        // Kiểm tra bắt lỗi bỏ trống dữ liệu
        if (TextUtils.isEmpty(otp) || otp.length() < 6) {
            edtResetOtpCode.setError("Vui lòng nhập đúng mã OTP");
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

        // Thông báo giả lập thành công, sau này sẽ kết nối API gửi lên Spring Boot Server
        Toast.makeText(this, "Đổi mật khẩu thành công! Mời bạn đăng nhập lại.", Toast.LENGTH_LONG).show();

        // Quay về màn hình Đăng nhập và đóng màn hình này lại
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}