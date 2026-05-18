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
import com.example.smartnotesfrontend.R;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText edtOtpInput;
    private TextView tvCountdown;
    private Button btnVerifyOtp, btnResendOtp;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Ánh xạ các thành phần từ XML
        edtOtpInput = findViewById(R.id.edtOtpInput);
        tvCountdown = findViewById(R.id.tvCountdown);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnResendOtp = findViewById(R.id.btnResendOtp);

        // Bắt đầu đếm ngược 60 giây ngay khi vào màn hình
        startCountdownTimer();

        // Xử lý khi bấm nút Xác thực
        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = edtOtpInput.getText().toString().trim();
                if (otp.length() < 6) {
                    edtOtpInput.setError("Mã OTP phải đủ 6 số");
                } else {
                    // Sau này Leader sẽ gọi API xác thực với Spring Boot tại đây
                    Toast.makeText(OtpVerificationActivity.this, "Đang xác thực mã OTP...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Xử lý khi bấm nút Gửi lại mã (sau khi hết 60s)
        btnResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnResendOtp.setVisibility(View.GONE);
                btnVerifyOtp.setEnabled(true);
                startCountdownTimer(); // Chạy lại đồng hồ đếm ngược mới
                Toast.makeText(OtpVerificationActivity.this, "Đã gửi lại mã OTP mới vào Email!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(60000, 1000) { // 60000ms = 60 giây
            @Override
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText("Mã OTP hết hạn sau: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("Mã OTP đã hết hạn!");
                btnVerifyOtp.setEnabled(false); // Khóa không cho bấm Xác thực nữa
                btnResendOtp.setVisibility(View.VISIBLE); // Hiện nút để gửi lại mã mới
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Hủy đếm ngược nếu thoát màn hình để tránh rò rỉ bộ nhớ
        }
    }
}