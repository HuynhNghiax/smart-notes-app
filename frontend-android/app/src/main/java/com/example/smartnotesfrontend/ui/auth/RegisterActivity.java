package com.example.smartnotesfrontend.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartnotesfrontend.R;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvLoginLink;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ thành phần từ XML
        tvLoginLink = findViewById(R.id.tvLoginLink);
        btnRegister = findViewById(R.id.btnRegister);

        // Click để quay lại màn hình Đăng nhập
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình Đăng ký, tự động quay về Login
            }
        });

        // Nút đăng ký (tạm thời để trống để nối API sau)
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý gọi API Đăng ký ở đây
            }
        });
    }
}