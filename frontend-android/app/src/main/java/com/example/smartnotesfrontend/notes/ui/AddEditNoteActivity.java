package com.example.smartnotesfrontend.notes.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartnotesfrontend.R;
import com.example.smartnotesfrontend.data.model.Note;
import com.example.smartnotesfrontend.data.remote.ApiService;
import com.example.smartnotesfrontend.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditNoteActivity extends AppCompatActivity {

    private EditText edtTitle;
    private EditText edtContent;

    private Button btnSave;

    private ApiService apiService;

    private boolean isEditMode = false;

    private Long noteId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        apiService = RetrofitClient.getApiService();

        initViews();

        getIntentData();

        setupSaveButton();
    }

    private void initViews() {

        edtTitle = findViewById(R.id.edtTitle);

        edtContent = findViewById(R.id.edtContent);

        btnSave = findViewById(R.id.btnSave);
    }

    private void getIntentData() {

        if (getIntent().hasExtra("note_id")) {

            isEditMode = true;

            noteId =
                    getIntent().getLongExtra(
                            "note_id",
                            -1
                    );

            String title =
                    getIntent().getStringExtra(
                            "note_title"
                    );

            String content =
                    getIntent().getStringExtra(
                            "note_content"
                    );

            edtTitle.setText(title);
            edtContent.setText(content);

            setTitle("Edit Note");
        } else {
            setTitle("Add Note");
        }
    }

    private void setupSaveButton() {

        btnSave.setOnClickListener(v -> {

            String title =
                    edtTitle.getText()
                            .toString()
                            .trim();

            String content =
                    edtContent.getText()
                            .toString()
                            .trim();

            if (title.isEmpty() || content.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Note note = new Note();

            note.setTitle(title);

            note.setContent(content);

            if (isEditMode) {

                updateNote(note);

            } else {

                createNote(note);
            }
        });
    }

    // CREATE
    private void createNote(Note note) {

        Long userId = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getLong("USER_ID", 1L);
        if (userId == -1L) {
            Toast.makeText(this,
                    "Không tìm thấy thông tin người dùng",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.createNote(userId, note)
                .enqueue(new Callback<Note>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Note> call,
                            @NonNull Response<Note> response
                    ) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    AddEditNoteActivity.this,
                                    "Note created",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        } else {

                            try {

                                String error = "";

                                if (response.errorBody() != null) {
                                    error = response.errorBody().string();
                                }

                                Toast.makeText(
                                        AddEditNoteActivity.this,
                                        "Code: " + response.code(),
                                        Toast.LENGTH_LONG
                                ).show();

                                android.util.Log.e(
                                        "CREATE_NOTE",
                                        "Code=" + response.code() + "\n" + error
                                );

                            } catch (Exception e) {

                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Note> call,
                            @NonNull Throwable t
                    ) {

                        Toast.makeText(
                                AddEditNoteActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // UPDATE
    private void updateNote(Note note) {

        apiService.updateNote(noteId, note)
                .enqueue(new Callback<Note>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<Note> call,
                            @NonNull Response<Note> response
                    ) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    AddEditNoteActivity.this,
                                    "Note updated",
                                    Toast.LENGTH_SHORT
                            ).show();

                            finish();

                        } else {

                            Toast.makeText(
                                    AddEditNoteActivity.this,
                                    "Update failed: " + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Note> call,
                            @NonNull Throwable t
                    ) {

                        Toast.makeText(
                                AddEditNoteActivity.this,
                                t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}