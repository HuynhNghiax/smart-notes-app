package com.example.smartnotesfrontend.ui.notes.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.smartnotesfrontend.R;
import com.example.smartnotesfrontend.data.model.Note;
import com.example.smartnotesfrontend.data.model.ScheduleMode;
import com.example.smartnotesfrontend.data.remote.ApiService;
import com.example.smartnotesfrontend.data.remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditNoteActivity extends AppCompatActivity {

    private EditText edtTitle;
    private EditText edtContent;
    private Button btnSave;

    private SwitchCompat switchSchedule;
    private LinearLayout layoutScheduleOptions;
    private Spinner spinnerMode;
    private Button btnPickDate, btnPickTime;

    private String selectedDate = "";
    private String selectedTime = "";

    private boolean isEditMode = false;

    private Long noteId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        initViews();

        getIntentData();

        setupSaveButton();
    }

    private void initViews() {
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        btnSave = findViewById(R.id.btnSave);

        switchSchedule = findViewById(R.id.switchSchedule);
        layoutScheduleOptions = findViewById(R.id.layoutScheduleOptions);
        spinnerMode = findViewById(R.id.spinnerMode);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnPickTime = findViewById(R.id.btnPickTime);

        // Setup Spinner
        String[] modes = {"Mỗi ngày", "Chọn ngày"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMode.setAdapter(adapter);

        // Handle Switch
        switchSchedule.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutScheduleOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Handle Pickers
        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnPickTime.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            btnPickDate.setText(selectedDate);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d:00", hourOfDay, minute);
            btnPickTime.setText(selectedTime);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
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

            if (getIntent().hasExtra("note_schedule_mode")) {
                String modeStr = getIntent().getStringExtra("note_schedule_mode");
                if (modeStr != null) {
                    switchSchedule.setChecked(true);
                    layoutScheduleOptions.setVisibility(View.VISIBLE);
                    if (modeStr.equals("EVERYDAY")) {
                        spinnerMode.setSelection(0);
                    } else {
                        spinnerMode.setSelection(1);
                    }
                }
            }

            if (getIntent().hasExtra("note_schedule_date")) {
                selectedDate = getIntent().getStringExtra("note_schedule_date");
                btnPickDate.setText(selectedDate);
            }

            if (getIntent().hasExtra("note_notify_time")) {
                selectedTime = getIntent().getStringExtra("note_notify_time");
                btnPickTime.setText(selectedTime);
            }
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

            if (switchSchedule.isChecked()) {
                int pos = spinnerMode.getSelectedItemPosition();
                note.setScheduleMode(pos == 0 ? ScheduleMode.EVERYDAY : ScheduleMode.SPECIFIC_DAY);
                note.setScheduleDate(selectedDate.isEmpty() ? null : selectedDate);
                note.setNotifyTime(selectedTime.isEmpty() ? null : selectedTime);
            } else {
                note.setScheduleMode(null);
                note.setScheduleDate(null);
                note.setNotifyTime(null);
            }

            if (isEditMode) {

                updateNote(note);

            } else {

                createNote(note);
            }
        });
    }

    // CREATE
    private void createNote(Note note) {

        ApiService apiService =
                RetrofitClient
                        .getRetrofitInstance()
                        .create(ApiService.class);

        apiService.createNote(1L, note)
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

                            Toast.makeText(
                                    AddEditNoteActivity.this,
                                    "Create failed",
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

    // UPDATE
    private void updateNote(Note note) {

        ApiService apiService =
                RetrofitClient
                        .getRetrofitInstance()
                        .create(ApiService.class);

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
                                    "Update failed",
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