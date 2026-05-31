package com.example.smartnotesfrontend.ui.categories;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartnotesfrontend.R;

import java.util.List;
import java.util.Map;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private Context context;
    private List<Map<String, Object>> categories;
    private OnCategoryClickListener listener;

    // Interface xử lý click và long click item category
    public interface OnCategoryClickListener {
        void onCategoryClick(Map<String, Object> category);
        void onCategoryLongClick(Map<String, Object> category);
    }

    public CategoryListAdapter(Context context,
                               List<Map<String, Object>> categories,
                               OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    // Cập nhật dữ liệu danh sách category
    public void updateData(List<Map<String, Object>> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> category = categories.get(position);

        String name = (String) category.get("name");
        String description = (String) category.get("description");
        String colorCode = (String) category.get("colorCode");

        // Set tên category
        holder.tvCategoryName.setText(name);

        // Set mô tả (nếu null thì để rỗng)
        holder.tvCategoryDesc.setText(
                description != null ? description : ""
        );

        // Set màu nền category
        if (colorCode != null && !colorCode.isEmpty()) {
            try {
                holder.itemView.setBackgroundColor(Color.parseColor(colorCode));
            } catch (IllegalArgumentException e) {
                holder.itemView.setBackgroundColor(Color.GRAY);
            }
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        // Click item
        holder.itemView.setOnClickListener(v ->
                listener.onCategoryClick(category)
        );

        // Long click item
        holder.itemView.setOnLongClickListener(v -> {
            listener.onCategoryLongClick(category);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    // ViewHolder chứa view của item category
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCategoryName;
        TextView tvCategoryDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvCategoryDesc = itemView.findViewById(R.id.tv_category_desc);
        }
    }
}