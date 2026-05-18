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

public class RegisterActivity extends AppCompatActivity {

    private EditText edtRegisterEmail, edtRegisterPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private AuthViewModel authViewModel;
    private String savedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo ViewModel kết nối mạng
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Ánh xạ các thành phần giao diện từ XML
        edtRegisterEmail = findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Bấm nút quay lại màn hình đăng nhập
        tvLoginLink.setOnClickListener(v -> finish());

        // Bấm nút tiến hành đăng ký tài khoản
        btnRegister.setOnClickListener(v -> handleRegister());

        // Lắng nghe kết quả trả về từ API Backend thật
        authViewModel.getAuthResult().observe(this, result -> {
            if (result != null) {
                if (result.equals("REGISTER_SUCCESS")) {
                    Toast.makeText(this, "Mã kích hoạt OTP đã được khởi tạo!", Toast.LENGTH_SHORT).show();

                    // Chuyển sang màn hình nhập mã OTP và truyền kèm email sang để xác thực
                    Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("email", savedEmail);
                    startActivity(intent);

                    authViewModel.clearResult(); // Dọn dẹp bộ nhớ LiveData
                    finish(); // Đóng màn hình đăng ký này lại
                } else {
                    // Hiển thị thông báo lỗi nếu Email đã tồn tại hoặc mất mạng
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void handleRegister() {
        String email = edtRegisterEmail.getText().toString().trim();
        String password = edtRegisterPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Kiểm tra tính hợp lệ của dữ liệu đầu vào (Validation)
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtRegisterEmail.setError("Email không hợp lệ");
            return;
        }
        if (password.length() < 6) {
            edtRegisterPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
            return;
        }
        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không trùng khớp");
            return;
        }

        // Lưu lại email tạm thời để lát truyền sang màn hình OTP
        savedEmail = email;

        Toast.makeText(this, "Đang gửi yêu cầu đăng ký...", Toast.LENGTH_SHORT).show();

        // Kích hoạt gọi API Đăng ký ngầm lên Server Spring Boot
        authViewModel.registerUser(email, password);
    }
}