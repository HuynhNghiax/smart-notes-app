package com.example.smartnotesfrontend.ui.categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartnotesfrontend.data.model.Category;
import com.example.smartnotesfrontend.data.remote.ApiService;
import com.example.smartnotesfrontend.data.remote.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends ViewModel {

    private final ApiService apiService;

    private final MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();

    public CategoryViewModel() {
        this.apiService = RetrofitClient.getApiService();
    }

    public LiveData<List<Category>> getCategories() {
        return categoriesLiveData;
    }

    public LiveData<String> getMessage() {
        return messageLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    // API: Lấy danh sách category
    public void fetchCategories(String token) {
        isLoadingLiveData.setValue(true);

        apiService.getCategories("Bearer " + token).enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call,
                                   Response<List<Category>> response) {
                isLoadingLiveData.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    categoriesLiveData.setValue(response.body());
                } else {
                    messageLiveData.setValue("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                isLoadingLiveData.setValue(false);
                messageLiveData.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // API: Tạo category mới
    public void createCategory(String token, String name, String description, String colorCode) {
        isLoadingLiveData.setValue(true);

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("description", description != null ? description : "");
        body.put("colorCode", colorCode != null ? colorCode : "");

        apiService.createCategory("Bearer " + token, body)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {
                        isLoadingLiveData.setValue(false);

                        if (response.isSuccessful()) {
                            messageLiveData.setValue("Tạo danh mục thành công");
                            fetchCategories(token);
                        } else {
                            messageLiveData.setValue("Lỗi: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        isLoadingLiveData.setValue(false);
                        messageLiveData.setValue("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // API: Cập nhật category
    public void updateCategory(String token, Long categoryId,
                               String name, String description, String colorCode) {

        isLoadingLiveData.setValue(true);

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("description", description != null ? description : "");
        body.put("colorCode", colorCode != null ? colorCode : "");

        apiService.updateCategory("Bearer " + token, categoryId, body)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {
                        isLoadingLiveData.setValue(false);

                        if (response.isSuccessful()) {
                            messageLiveData.setValue("Cập nhật danh mục thành công");
                            fetchCategories(token);
                        } else {
                            messageLiveData.setValue("Lỗi: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        isLoadingLiveData.setValue(false);
                        messageLiveData.setValue("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // API: Xóa category
    public void deleteCategory(String token, Long categoryId) {
        isLoadingLiveData.setValue(true);

        apiService.deleteCategory("Bearer " + token, categoryId)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call,
                                           Response<Map<String, String>> response) {
                        isLoadingLiveData.setValue(false);

                        if (response.isSuccessful()) {
                            messageLiveData.setValue("Xóa danh mục thành công");
                            fetchCategories(token);
                        } else {
                            messageLiveData.setValue("Lỗi: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        isLoadingLiveData.setValue(false);
                        messageLiveData.setValue("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }
}