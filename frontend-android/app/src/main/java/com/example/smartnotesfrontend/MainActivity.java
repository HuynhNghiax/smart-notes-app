package com.example.smartnotesfrontend;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartnotesfrontend.data.model.Note;
import com.example.smartnotesfrontend.data.remote.ApiService;
import com.example.smartnotesfrontend.data.remote.RetrofitClient;
import com.example.smartnotesfrontend.ui.notes.adapter.NoteAdapter;
import com.example.smartnotesfrontend.ui.notes.ui.AddEditNoteActivity;
import com.example.smartnotesfrontend.utils.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvNotes;
    private FloatingActionButton fabAddNote;
    private FloatingActionButton btnAi;
    private FloatingActionButton fabCategory;

    private NoteAdapter adapter;
    private List<Note> noteList = new ArrayList<>();

    // Tự refresh khi quay lại từ Add/Edit
    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> loadNotes()
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        setSupportActionBar(toolbar);

        setupRecyclerView();

        setupFab();

        setupAiButton();

        setupCategoryButton();

        loadNotes();

        askNotificationPermission();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvNotes = findViewById(R.id.rvNotes);
        fabAddNote = findViewById(R.id.fabAddNote);
        btnAi = findViewById(R.id.btnAi);
        fabCategory = findViewById(R.id.fabCategory);
    }

    private void setupRecyclerView() {
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupFab() {
        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
            launcher.launch(intent);
        });
    }

    private void setupAiButton() {
        btnAi.setOnClickListener(v -> showAiInputDialog());
    }

    private void setupCategoryButton() {
        fabCategory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.smartnotesfrontend.ui.categories.CategoryActivity.class);
            startActivity(intent);
        });
    }

    private void loadNotes() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getNotes(1L).enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(@NonNull Call<List<Note>> call, @NonNull Response<List<Note>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    noteList = response.body();
                    adapter = new NoteAdapter(
                            noteList,
                            note -> { // CLICK -> EDIT
                                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                                intent.putExtra("note_id", note.getId());
                                intent.putExtra("note_title", note.getTitle());
                                intent.putExtra("note_content", note.getContent());
                                if (note.getCategoryId() != null) {
                                    intent.putExtra("note_category_id", note.getCategoryId());
                                }
                                if (note.getScheduleMode() != null) {
                                    intent.putExtra("note_schedule_mode", note.getScheduleMode().name());
                                }
                                intent.putExtra("note_schedule_date", note.getScheduleDate());
                                intent.putExtra("note_notify_time", note.getNotifyTime());
                                launcher.launch(intent);
                            },
                            note -> showDeleteDialog(note) // LONG CLICK -> DELETE
                    );
                    rvNotes.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Cannot load notes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Note>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDeleteDialog(Note note) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> deleteNote(note.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteNote(Long noteId) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.deleteNote(noteId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    loadNotes();
                } else {
                    Toast.makeText(MainActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAiInputDialog() {
        EditText input = new EditText(this);
        input.setHint("Nhập nội dung cần tách thành ghi chú...");
        input.setPadding(40, 40, 40, 40);

        new AlertDialog.Builder(this)
                .setTitle("AI Smart Note")
                .setView(input)
                .setPositiveButton("Tách việc", (dialog, which) -> {
                    String content = input.getText().toString();
                    if (!content.isEmpty()) {
                        callAiApi(content);
                    } else {
                        Toast.makeText(this, "Vui lòng nhập nội dung!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void callAiApi(String content) {
        Map<String, String> request = new HashMap<>();
        request.put("content", content);

        RetrofitClient.getApiService().summarizeNote(request).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> tasks = response.body();
                    if (tasks.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Không có việc nào để tách.", Toast.LENGTH_SHORT).show();
                    } else {
                        showAiTasksPopup(tasks);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAiTasksPopup(List<String> taskList) {
        ScrollView scrollView = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(40, 40, 40, 40);

        List<EditText> editTexts = new ArrayList<>();
        for (String task : taskList) {
            EditText et = new EditText(this);
            et.setText(task);
            et.setTextSize(16);
            et.setPadding(0, 20, 0, 20);
            container.addView(et);
            editTexts.add(et);
        }
        scrollView.addView(container);

        new AlertDialog.Builder(this)
                .setTitle("Lưu các công việc")
                .setView(scrollView)
                .setPositiveButton("Lưu tất cả", (dialog, which) -> {
                    for (EditText et : editTexts) {
                        String taskContent = et.getText().toString();
                        if (!taskContent.isEmpty()) {
                            saveNoteToBackend("Công việc mới", taskContent);
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveNoteToBackend(String title, String content) {
        Long userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getLong("USER_ID", 1L);

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);

        RetrofitClient.getApiService().createNote(userId, note).enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@NonNull Call<Note> call, @NonNull Response<Note> response) {
                if (response.isSuccessful()) {
                    loadNotes();
                    Toast.makeText(MainActivity.this, "Đã lưu: " + title, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Note> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi lưu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "Thông báo đã được bật", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Bạn đã từ chối nhận thông báo", Toast.LENGTH_SHORT).show();
                }
            });

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SharedPrefManager.getInstance(this).clearToken();
            Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, com.example.smartnotesfrontend.ui.auth.LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
