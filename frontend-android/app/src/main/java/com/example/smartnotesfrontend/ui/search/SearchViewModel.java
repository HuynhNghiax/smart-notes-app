package com.example.smartnotesfrontend.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.smartnotesfrontend.data.remote.ApiService;
import com.example.smartnotesfrontend.data.remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SearchViewModel extends ViewModel {

    private final ApiService apiService;
    private final MutableLiveData<List<Map<String, Object>>> searchResultsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Map<String, Object>>> pinnedNotesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();

    public SearchViewModel() {
        this.apiService = RetrofitClient.getApiService();
    }

    public LiveData<List<Map<String, Object>>> getSearchResults() {
        return searchResultsLiveData;
    }

    public LiveData<List<Map<String, Object>>> getPinnedNotes() {
        return pinnedNotesLiveData;
    }

    public LiveData<String> getMessage() {
        return messageLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    // Tìm kiếm ghi chú theo từ khóa
    public void searchNotes(String token, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            searchResultsLiveData.setValue(Collections.emptyList());
            return;
        }

        isLoadingLiveData.setValue(true);
        apiService.searchNotes("Bearer " + token, keyword).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                isLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    searchResultsLiveData.setValue(response.body());
                    messageLiveData.setValue("Tìm thấy " + response.body().size() + " ghi chú");
                } else {
                    searchResultsLiveData.setValue(Collections.emptyList());
                    messageLiveData.setValue("Không tìm thấy ghi chú nào");
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                isLoadingLiveData.setValue(false);
                messageLiveData.setValue("Lỗi: " + t.getMessage());
            }
        });
    }

    // Lấy ghi chú ghim
    public void fetchPinnedNotes(String token) {
        isLoadingLiveData.setValue(true);
        apiService.getPinnedNotes("Bearer " + token).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                isLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    pinnedNotesLiveData.setValue(response.body());
                } else {
                    messageLiveData.setValue("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                isLoadingLiveData.setValue(false);
                messageLiveData.setValue("Lỗi: " + t.getMessage());
            }
        });
    }

    // Lấy ghi chú theo danh mục
    public void fetchNotesByCategory(String token, Long categoryId) {
        isLoadingLiveData.setValue(true);
        apiService.getNotesByCategory("Bearer " + token, categoryId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                isLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    searchResultsLiveData.setValue(response.body());
                } else {
                    messageLiveData.setValue("Lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                isLoadingLiveData.setValue(false);
                messageLiveData.setValue("Lỗi: " + t.getMessage());
            }
        });
    }
}