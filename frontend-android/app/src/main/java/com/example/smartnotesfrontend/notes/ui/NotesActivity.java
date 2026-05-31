package com.example.smartnotesfrontend.notes.ui;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotesActivity extends AppCompatActivity {

    private RecyclerView recyclerNotes;
    private FloatingActionButton fabAddNote;

    private NoteAdapter adapter;

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
    }

    private void initViews() {

        recyclerNotes = findViewById(R.id.recyclerNotes);

        fabAddNote = findViewById(R.id.fabAddNote);
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
}