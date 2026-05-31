package com.example.smartnotesfrontend.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartnotesfrontend.R;
import com.example.smartnotesfrontend.utils.SharedPrefManager;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearchKeyword;
    private RecyclerView rvSearchResults;
    private ProgressBar progressBar;
    private SearchResultAdapter adapter;
    private SearchViewModel viewModel;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Ánh xạ UI
        etSearchKeyword = findViewById(R.id.et_search_keyword);
        rvSearchResults = findViewById(R.id.rv_search_results);
        progressBar = findViewById(R.id.progress_bar);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        // Setup RecyclerView
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultAdapter(this, java.util.Collections.emptyList());
        rvSearchResults.setAdapter(adapter);

        // Lấy token
        token = SharedPrefManager.getInstance(this).getToken();

        // Observe kết quả tìm kiếm
        viewModel.getSearchResults().observe(this, results -> {
            adapter = new SearchResultAdapter(SearchActivity.this, results);
            rvSearchResults.setAdapter(adapter); // BUG FIX: cập nhật adapter khi có kết quả mới
        });

        // Observe thông báo
        viewModel.getMessage().observe(this, message -> {
            Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
        });

        // Observe loading
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE);
        });

        // Bắt sự kiện gõ phím — tìm kiếm realtime
        etSearchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                viewModel.searchNotes(token, keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}