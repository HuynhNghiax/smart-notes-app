package com.example.smartnotesfrontend.ui.tags;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import java.util.Map;

public class TagViewModel extends ViewModel {

    private final MutableLiveData<List<Map<String, Object>>> tagsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> messageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();

    public LiveData<List<Map<String, Object>>> getTags() {
        return tagsLiveData;
    }

    public LiveData<String> getMessage() {
        return messageLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    // Set danh sách tag
    public void setTags(List<Map<String, Object>> tags) {
        tagsLiveData.setValue(tags);
    }

    // Thêm tag
    public void addTag(String tagName) {
        isLoadingLiveData.setValue(true);
        // Logic thêm tag sẽ được implement khi Backend cung cấp API
        messageLiveData.setValue("Tag được thêm thành công");
        isLoadingLiveData.setValue(false);
    }

    // Xóa tag
    public void deleteTag(Long tagId) {
        isLoadingLiveData.setValue(true);
        // Logic xóa tag sẽ được implement khi Backend cung cấp API
        messageLiveData.setValue("Tag được xóa thành công");
        isLoadingLiveData.setValue(false);
    }
}