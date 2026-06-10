package com.example.smartnotesfrontend.notes.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartnotesfrontend.R;
import com.example.smartnotesfrontend.data.model.Note;
import com.example.smartnotesfrontend.data.remote.ApiService;
import com.example.smartnotesfrontend.data.remote.RetrofitClient;
import com.example.smartnotesfrontend.notes.adapter.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.smartnotesfrontend.data.model.AiResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotesActivity extends AppCompatActivity {

    private RecyclerView recyclerNotes;
    private FloatingActionButton fabAddNote;

    private NoteAdapter adapter;
    private FloatingActionButton btnAi;
    private List<Note> noteList; // Danh sách note của user hiện tại
    private NoteAdapter noteAdapter;

    // Tự refresh khi quay lại từ Add/Edit
    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> loadNotes()
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        initViews();

        setupRecyclerView();

        setupFab();

        loadNotes();

        setupAiButton();
    }

    private void initViews() {

        recyclerNotes = findViewById(R.id.recyclerNotes);

        fabAddNote = findViewById(R.id.fabAddNote);

        btnAi = findViewById(R.id.btnAi);
    }

    private void setupRecyclerView() {

        recyclerNotes.setLayoutManager(
                new LinearLayoutManager(this)
        );
    }

    private void setupFab() {

        fabAddNote.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            NotesActivity.this,
                            AddEditNoteActivity.class
                    );

            launcher.launch(intent);
        });
    }
    private void setupAiButton() {
        // Nếu bạn đã khai báo btnAi ở trên cùng rồi thì không cần khai báo lại
        btnAi.setOnClickListener(v -> {
            // Gọi hộp thoại nhập văn bản
            showAiInputDialog();
        });
    }

    private void loadNotes() {

        ApiService apiService =
                RetrofitClient.getApiService();

        apiService.getNotes(1L).enqueue(new Callback<List<Note>>() {

            @Override
            public void onResponse(
                    @NonNull Call<List<Note>> call,
                    @NonNull Response<List<Note>> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    List<Note> noteList = response.body();

                    adapter = new NoteAdapter(
                            noteList,

                            // CLICK -> EDIT
                            note -> {

                                Intent intent =
                                        new Intent(
                                                NotesActivity.this,
                                                AddEditNoteActivity.class
                                        );

                                intent.putExtra(
                                        "note_id",
                                        note.getId()
                                );

                                intent.putExtra(
                                        "note_title",
                                        note.getTitle()
                                );

                                intent.putExtra(
                                        "note_content",
                                        note.getContent()
                                );

                                launcher.launch(intent);
                            },

                            // LONG CLICK -> DELETE
                            note -> showDeleteDialog(note)
                    );

                    recyclerNotes.setAdapter(adapter);

                } else {

                    Toast.makeText(
                            NotesActivity.this,
                            "Cannot load notes",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<Note>> call,
                    @NonNull Throwable t
            ) {

                Toast.makeText(
                        NotesActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void showDeleteDialog(Note note) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    deleteNote(note.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteNote(Long noteId) {

        ApiService apiService =
                RetrofitClient.getApiService();

        apiService.deleteNote(noteId)
                .enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<String> call,
                            @NonNull Response<String> response
                    ) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    NotesActivity.this,
                                    "Deleted successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                            loadNotes();

                        } else {

                            Toast.makeText(
                                    NotesActivity.this,
                                    "Delete failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<String> call,
                            @NonNull Throwable t
                    ) {

                        Toast.makeText(
                                NotesActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
    private void showAiInputDialog() {
        EditText input = new EditText(this);
        input.setHint("Nhập nội dung cần tách thành ghi chú...");
        // Thêm padding cho đẹp
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
        // 1. Vô hiệu hóa nút để tránh spam API
        // btnSummarize.setEnabled(false);

        Map<String, String> request = new HashMap<>();
        request.put("content", content);

        RetrofitClient.getApiService().summarizeNote(request).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                // btnSummarize.setEnabled(true); // Bật lại nút

                if (response.isSuccessful() && response.body() != null) {
                    List<String> tasks = response.body();
                    if (tasks.isEmpty()) {
                        Toast.makeText(NotesActivity.this, "Không có việc nào để tách.", Toast.LENGTH_SHORT).show();
                    } else {
                        showAiTasksPopup(tasks);
                    }
                } else {
                    Toast.makeText(NotesActivity.this, "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                // btnSummarize.setEnabled(true); // Bật lại nút
                Toast.makeText(NotesActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Lưu các công việc")
                .setView(scrollView)
                .setPositiveButton("Lưu tất cả", (dialog, which) -> {
                    for (EditText et : editTexts) {
                        String taskContent = et.getText().toString();
                        if (!taskContent.isEmpty()) {
                            // Tự động đặt tiêu đề là "Công việc" hoặc lấy từ nội dung
                            saveNoteToBackend("Công việc mới", taskContent);
                        }
                    }
                    // Sau khi lưu xong thì tải lại danh sách trên màn hình chính
                    loadNotes();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveNoteToBackend(String title, String content) {
        // Lấy ID từ SharedPreferences để đúng user
        Long userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getLong("USER_ID", 1L);

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.createNote(userId, note).enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@NonNull Call<Note> call, @NonNull Response<Note> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (noteList != null) {
                        noteList.add(response.body());
                        if (noteAdapter != null) noteAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(NotesActivity.this, "Đã lưu: " + title, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<Note> call, @NonNull Throwable t) {
                Toast.makeText(NotesActivity.this, "Lỗi lưu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    }
