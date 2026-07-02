package com.example.smartnotesfrontend.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartnotesfrontend.MainActivity;
import com.example.smartnotesfrontend.R;
import com.example.smartnotesfrontend.utils.SharedPrefManager;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private MaterialButton btnGoogleLogin;
    private TextView tvRegisterLink, tvForgotPassword;
    private AuthViewModel authViewModel;
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // KIỂM TRA PHIÊN ĐĂNG NHẬP (Session Persistence)
        // Nếu đã có token, chuyển thẳng vào MainActivity
        String existingToken = SharedPrefManager.getInstance(this).getToken();
        if (existingToken != null && !existingToken.isEmpty()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        credentialManager = CredentialManager.create(this);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Sự kiện bấm nút Google sử dụng Credential Manager mới
        btnGoogleLogin.setOnClickListener(v -> loginWithGoogleManager());

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
                // Kiểm tra nếu là tín hiệu đăng nhập thành công
                if (result.startsWith("LOGIN_SUCCESS:")) {
                    // Cắt bỏ tiền tố "LOGIN_SUCCESS:" để lấy JWT thực tế
                    String jwtToken = result.substring("LOGIN_SUCCESS:".length());
                    
                    // Lưu JWT vào SharedPref
                    SharedPrefManager.getInstance(LoginActivity.this).saveToken(jwtToken);
                    
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    authViewModel.clearResult();
                    finish();
                } else if (result.equals("WRONG_CREDENTIALS") || result.equals("ACCOUNT_NOT_ACTIVATED") || result.startsWith("Lỗi:")) {
                    // Hiển thị thông báo lỗi
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loginWithGoogleManager() {
        // LƯU Ý: serverClientId này đang có project number 648616238662
        // Trong khi google-services.json của bạn là 748114122843.
        // Đây có thể là nguyên nhân gây lỗi "No credentials available".
        String serverClientId = "648616238662-u9uuojremvv9leppin6pm3daqu16416j.apps.googleusercontent.com";

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false) // Tắt tự động chọn để hiện bảng chọn tài khoản
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                ContextCompat.getMainExecutor(this),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(@NonNull GetCredentialResponse result) {
                        handleGoogleSignInResult(result);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        String errorMsg = e.getMessage();
                        if (errorMsg != null && errorMsg.contains("No credentials available")) {
                            errorMsg = "Không tìm thấy tài khoản Google phù hợp. Vui lòng kiểm tra SHA-1 và Client ID.";
                        }
                        Toast.makeText(LoginActivity.this, "Lỗi xác thực: " + errorMsg, Toast.LENGTH_LONG).show();
                        android.util.Log.e("LoginActivity", "Google login error", e);
                    }
                }
        );
    }

    private void handleGoogleSignInResult(GetCredentialResponse result) {
        Credential credential = result.getCredential();

        if (credential instanceof CustomCredential &&
                credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            try {
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                String idToken = googleIdTokenCredential.getIdToken();

                Toast.makeText(this, "Xác thực thành công! Đang đăng nhập...", Toast.LENGTH_SHORT).show();
                // GỬI TOKEN LÊN BACKEND
                authViewModel.loginWithGoogle(idToken);

            } catch (Exception e) {
                Toast.makeText(this, "Lỗi xử lý dữ liệu Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Loại xác thực không được hỗ trợ", Toast.LENGTH_SHORT).show();
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