package com.example.smartnotesfrontend.ui.auth;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        edtRegisterEmail = findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        tvLoginLink.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> handleRegister());

        authViewModel.getAuthResult().observe(this, result -> {
            if (result != null) {
                if (result.equals("REGISTER_SUCCESS")) {
                    Toast.makeText(this, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void handleRegister() {
        String email = edtRegisterEmail.getText().toString().trim();
        String password = edtRegisterPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtRegisterEmail.setError("Email không hợp lệ");
            return;
        }
        if (password.length() < 6) {
            edtRegisterPassword.setError("Mật khẩu phải từ 6 ký tự");
            return;
        }
        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không trùng khớp");
            return;
        }

        authViewModel.registerUser(email, password);
    }
}