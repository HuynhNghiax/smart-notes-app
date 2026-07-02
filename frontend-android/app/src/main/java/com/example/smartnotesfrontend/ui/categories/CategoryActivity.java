package com.example.smartnotesfrontend.ui.categories;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartnotesfrontend.R;
import com.example.smartnotesfrontend.data.model.Category;
import com.example.smartnotesfrontend.utils.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private FloatingActionButton fabAddCategory;
    private ProgressBar progressBar;
    private CategoryViewModel viewModel;
    private CategoryListAdapter adapter;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Ánh xạ UI
        rvCategories = findViewById(R.id.rv_categories);
        fabAddCategory = findViewById(R.id.fab_add_category);
        progressBar = findViewById(R.id.progress_bar);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Khởi tạo adapter (chỉ tạo 1 lần)
        adapter = new CategoryListAdapter(
                this,
                new ArrayList<>(),
                new CategoryListAdapter.OnCategoryClickListener() {
                    @Override
                    public void onCategoryClick(Category category) {
                        Toast.makeText(
                                CategoryActivity.this,
                                "Chọn: " + category.getName(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onCategoryLongClick(Category category) {
                        showCategoryMenu(category);
                    }
                }
        );

        // Setup RecyclerView
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(adapter);

        // Lấy token đăng nhập
        token = SharedPrefManager.getInstance(this).getToken();

        // Lắng nghe danh sách category
        viewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                adapter.updateData(categories);
            }
        });

        // Lắng nghe message từ ViewModel
        viewModel.getMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        // Lắng nghe trạng thái loading
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE);
        });

        // Thêm category mới
        fabAddCategory.setOnClickListener(v -> showCreateCategoryDialog());

        // Load danh sách category
        viewModel.fetchCategories(token);
    }

    // Dialog tạo category mới
    private void showCreateCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tạo Danh Mục Mới");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        EditText etName = new EditText(this);
        etName.setHint("Tên danh mục");
        layout.addView(etName);

        EditText etDesc = new EditText(this);
        etDesc.setHint("Mô tả");
        layout.addView(etDesc);

        EditText etColor = new EditText(this);
        etColor.setHint("Mã màu (#FFFFFF)");
        layout.addView(etColor);

        builder.setView(layout);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String color = etColor.getText().toString().trim();

            if (!name.isEmpty()) {
                viewModel.createCategory(token, name, desc, color);
            } else {
                Toast.makeText(this, "Tên danh mục không được để trống", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Menu khi giữ lâu item category
    private void showCategoryMenu(Category category) {
        String[] options = {"Sửa", "Xóa"};

        new AlertDialog.Builder(this)
                .setTitle("Tuỳ chọn")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditCategoryDialog(category);
                    } else {
                        viewModel.deleteCategory(token, category.getId());
                    }
                })
                .show();
    }

    // Dialog sửa category
    private void showEditCategoryDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Danh Mục");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        EditText etName = new EditText(this);
        etName.setText(category.getName());
        layout.addView(etName);

        EditText etDesc = new EditText(this);
        etDesc.setText(category.getDescription() != null ? category.getDescription() : "");
        layout.addView(etDesc);

        EditText etColor = new EditText(this);
        etColor.setText(category.getColorCode() != null ? category.getColorCode() : "");
        layout.addView(etColor);

        builder.setView(layout);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            viewModel.updateCategory(
                    token,
                    category.getId(),
                    etName.getText().toString().trim(),
                    etDesc.getText().toString().trim(),
                    etColor.getText().toString().trim()
            );
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}