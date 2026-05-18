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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtForgotEmail;
    private Button btnSendOtp;
    private TextView tvBackToLoginLink;
    private AuthViewModel authViewModel;

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
                    Toast.makeText(this, "Mã mật khẩu mới/OTP đã gửi vào Email của bạn!", Toast.LENGTH_LONG).show();
                    finish();
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

        authViewModel.requestOtp(email);
    }
}