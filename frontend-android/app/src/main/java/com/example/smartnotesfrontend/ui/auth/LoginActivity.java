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

import com.example.smartnotesfrontend.MainActivity;
import com.example.smartnotesfrontend.R;
import com.example.smartnotesfrontend.utils.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private TextView tvRegisterLink, tvForgotPassword;
    private Button btnLogin;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ĐOẠN MỚI: Nếu đã có token rồi thì bỏ qua màn Login, vào thẳng Main
        if (SharedPrefManager.getInstance(this).getToken() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        // ... (Giữ nguyên đoạn ánh xạfindViewById của bạn bên dưới) ...

        // Cập nhật lại đoạn lắng nghe AuthResult để lưu Token thật vào máy
        authViewModel.getAuthResult().observe(this, result -> {
            if (result != null) {
                if (result.startsWith("LOGIN_SUCCESS")) {
                    // Tách lấy chuỗi Token phía sau dấu ":"
                    String token = result.substring(14);

                    // ĐOẠN MỚI: Lưu token vào bộ nhớ máy
                    SharedPrefManager.getInstance(this).saveToken(token);

                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            edtPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
            return;
        }

        authViewModel.loginUser(email, password);
    }
}