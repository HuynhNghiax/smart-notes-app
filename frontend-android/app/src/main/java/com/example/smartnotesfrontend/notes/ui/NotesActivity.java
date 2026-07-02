package com.example.smartnotesfrontend.notes.ui;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartnotesfrontend.R;
import com.example.smartnotesfrontend.data.model.Note;
import com.example.smartnotesfrontend.data.remote.ApiService;
import com.example.smartnotesfrontend.data.remote.RetrofitClient;
import com.example.smartnotesfrontend.notes.adapter.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotesActivity extends AppCompatActivity {

    private RecyclerView recyclerNotes;
    private FloatingActionButton fabAddNote;
    private FloatingActionButton btnAi;

    private List<Note> noteList = new ArrayList<>();
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
        setupAiButton();

        loadNotes();           // Load ban đầu
    }

    private void initViews() {
        recyclerNotes = findViewById(R.id.recyclerNotes);
        fabAddNote = findViewById(R.id.fabAddNote);
        btnAi = findViewById(R.id.btnAi);
    }

    private void setupRecyclerView() {
        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupFab() {
        fabAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(NotesActivity.this, AddEditNoteActivity.class);
            launcher.launch(intent);
        });
    }

    private void setupAiButton() {
        btnAi.setOnClickListener(v -> showAiInputDialog());
    }

    private void loadNotes() {
        ApiService apiService = RetrofitClient.getApiService();

        Long userId = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getLong("USER_ID", 1L);

        apiService.getNotes(userId).enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(@NonNull Call<List<Note>> call,
                                   @NonNull Response<List<Note>> response) {

                android.util.Log.d("NOTE_DEBUG", "HTTP = " + response.code());

                if (response.body() != null) {
                    android.util.Log.d("NOTE_DEBUG", "Size = " + response.body().size());

                    for (Note note : response.body()) {
                        android.util.Log.d("NOTE_DEBUG",
                                "Title = " + note.getTitle() +
                                        " | Content = " + note.getContent());
                    }
                }

                if (response.isSuccessful() && response.body() != null) {

                    noteList.clear();
                    noteList.addAll(response.body());

                    android.util.Log.d("NOTE_DEBUG",
                            "Recycler Size = " + noteList.size());

                    if (noteAdapter == null) {

                        noteAdapter = new NoteAdapter(
                                noteList,
                                note -> {
                                    Intent intent = new Intent(
                                            NotesActivity.this,
                                            AddEditNoteActivity.class
                                    );

                                    intent.putExtra("note_id", note.getId());
                                    intent.putExtra("note_title", note.getTitle());
                                    intent.putExtra("note_content", note.getContent());

                                    launcher.launch(intent);
                                },
                                note -> showDeleteDialog(note)
                        );

                        recyclerNotes.setAdapter(noteAdapter);

                    } else {

                        noteAdapter.notifyDataSetChanged();
                    }

                } else {

                    Toast.makeText(
                            NotesActivity.this,
                            "Cannot load notes",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Note>> call, @NonNull Throwable t) {
                Toast.makeText(NotesActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
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

        apiService.deleteNote(noteId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NotesActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    loadNotes();
                } else {
                    Toast.makeText(NotesActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(NotesActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==================== PHẦN AI (giữ nguyên) ====================
    private void showAiInputDialog() {
        EditText input = new EditText(this);
        input.setHint("Nhập nội dung cần tách thành ghi chú...");
        input.setPadding(40, 40, 40, 40);

        new AlertDialog.Builder(this)
                .setTitle("AI Smart Note")
                .setView(input)
                .setPositiveButton("Tách việc", (dialog, which) -> {
                    String content = input.getText().toString().trim();
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
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
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

        new AlertDialog.Builder(this)
                .setTitle("Lưu các công việc")
                .setView(scrollView)
                .setPositiveButton("Lưu tất cả", (dialog, which) -> {
                    for (EditText et : editTexts) {
                        String taskContent = et.getText().toString().trim();
                        if (!taskContent.isEmpty()) {
                            saveNoteToBackend("Công việc mới", taskContent);
                        }
                    }
                    loadNotes();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveNoteToBackend(String title, String content) {
        Long userId = getSharedPreferences("AppPrefs", MODE_PRIVATE).getLong("USER_ID", 1L);

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);

        ApiService apiService = RetrofitClient.getApiService();   // ← Sửa ở đây

        apiService.createNote(userId, note).enqueue(new Callback<Note>() {
            @Override
            public void onResponse(@NonNull Call<Note> call, @NonNull Response<Note> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(NotesActivity.this, "Đã lưu: " + title, Toast.LENGTH_SHORT).show();
                    loadNotes();                    // ← Tải lại danh sách
                }
            }

            @Override
            public void onFailure(@NonNull Call<Note> call, @NonNull Throwable t) {
                Toast.makeText(NotesActivity.this, "Lỗi lưu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}