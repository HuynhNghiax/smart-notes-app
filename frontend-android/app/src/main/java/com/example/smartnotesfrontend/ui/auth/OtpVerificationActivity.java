package com.example.smartnotesfrontend.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.smartnotesfrontend.R;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText edtOtpInput;
    private TextView tvCountdown;
    private Button btnVerifyOtp, btnResendOtp;
    private CountDownTimer countDownTimer;
    private AuthViewModel authViewModel;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        userEmail = getIntent().getStringExtra("email"); // Lấy email hứng từ màn Register

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        edtOtpInput = findViewById(R.id.edtOtpInput);
        tvCountdown = findViewById(R.id.tvCountdown);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnResendOtp = findViewById(R.id.btnResendOtp);

        startCountdownTimer();

        btnVerifyOtp.setOnClickListener(v -> {
            String otp = edtOtpInput.getText().toString().trim();
            if (otp.length() < 6) {
                edtOtpInput.setError("Mã OTP phải đủ 6 số");
            } else {
                Toast.makeText(this, "Đang gửi mã xác nhận...", Toast.LENGTH_SHORT).show();
                authViewModel.verifyOtp(userEmail, otp); // Bắn API thật xuống Spring Boot
            }
        });

        btnResendOtp.setOnClickListener(v -> {
            btnResendOtp.setVisibility(View.GONE);
            btnVerifyOtp.setEnabled(true);
            startCountdownTimer();
            authViewModel.registerUser(userEmail, "dummyPassword123"); // Gọi ngầm để refresh sinh mã mới
            Toast.makeText(this, "Yêu cầu Server cấp lại mã OTP mới thành công!", Toast.LENGTH_SHORT).show();
        });

        authViewModel.getAuthResult().observe(this, result -> {
            if (result != null) {
                if (result.equals("VERIFY_SUCCESS")) {
                    Toast.makeText(this, "Kích hoạt tài khoản thành công! Mời đăng nhập.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(OtpVerificationActivity.this, LoginActivity.class));
                    authViewModel.clearResult();
                    finish();
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText("Mã OTP hết hạn sau: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("Mã OTP đã hết hạn!");
                btnVerifyOtp.setEnabled(false);
                btnResendOtp.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}