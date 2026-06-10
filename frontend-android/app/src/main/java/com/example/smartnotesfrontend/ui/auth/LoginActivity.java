package com.example.smartnotesfrontend.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.smartnotesfrontend.MainActivity;
import com.example.smartnotesfrontend.R;
import com.example.smartnotesfrontend.utils.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private MaterialButton btnGoogleLogin;
    private TextView tvRegisterLink, tvForgotPassword;
    private AuthViewModel authViewModel;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // CẤU HÌNH ĐĂNG NHẬP GOOGLE VỚI ID THẬT CỦA BẠN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("740919720022-7r318pifh78uvflu9l2scsl2mmad3hd5.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Sự kiện bấm nút Google mở bảng chọn tài khoản
        btnGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        // Sự kiện chuyển sang màn hình Đăng ký
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Sự kiện chuyển sang màn hình Quên mật khẩu
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Sự kiện bấm nút Đăng nhập thông thường bằng tài khoản
        btnLogin.setOnClickListener(v -> handleLogin());

        // LẮNG NGHE TÍN HIỆU PHẢN HỒI (Áp dụng chung cho cả Đăng nhập thường và Đăng nhập Google)
        authViewModel.getAuthResult().observe(this, result -> {
            if (result != null) {
                // BUG FIX: Token JWT thật không bắt đầu bằng "LOGIN_SUCCESS"
                // Chỉ từ chối khi là lỗi rõ ràng, còn lại đều là token hợp lệ
                if (!result.equals("WRONG_CREDENTIALS") && !result.equals("ACCOUNT_NOT_ACTIVATED")) {
                    // Lưu token vào SharedPref để các màn hình khác dùng
                    SharedPrefManager.getInstance(LoginActivity.this).saveToken(result);
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    authViewModel.clearResult();
                    finish();
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Hứng dữ liệu sau khi người dùng chọn xong tài khoản Google công khai
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    Toast.makeText(this, "Xác thực Google thành công! Đang đồng bộ...", Toast.LENGTH_SHORT).show();

                    // CHÍNH XÁC: Đã mở khóa lệnh kích hoạt - Bắn trực tiếp idToken này lên endpoint mạng thời gian thực
                    authViewModel.loginWithGoogle(idToken);
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Lỗi kết nối tài khoản Google: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Vui lòng nhập đúng định dạng Email");
            return;
        }
        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
            return;
        }

        authViewModel.loginUser(email, password);
    }
}