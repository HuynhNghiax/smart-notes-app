package com.example.smartnotesfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvNotes;
    private FloatingActionButton fabAddNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ các thành phần giao diện từ XML
        toolbar = findViewById(R.id.toolbar);
        rvNotes = findViewById(R.id.rvNotes);
        fabAddNote = findViewById(R.id.fabAddNote);

        // 2. Cài đặt Toolbar thay thế cho ActionBar mặc định
        setSupportActionBar(toolbar);

        // 3. Bắt sự kiện khi bấm nút Thêm ghi chú tròn nổi (FAB)
        fabAddNote.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Mở màn hình thêm ghi chú mới (Tính năng của Thành viên 2)", Toast.LENGTH_SHORT).show();
        });
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // 1. Xóa token trong máy
            com.example.smartnotesfrontend.utils.SharedPrefManager.getInstance(this).clearToken();

            // 2. Đá người dùng về màn hình Login
            Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, com.example.smartnotesfrontend.ui.auth.LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}