package com.example.smartnotesfrontend.ui.tags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartnotesfrontend.R;
import java.util.List;
import java.util.Map;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {

    private Context context;
    private List<Map<String, Object>> tags;
    private OnTagClickListener listener;

    public interface OnTagClickListener {
        void onTagClick(Map<String, Object> tag);
        void onTagRemove(Map<String, Object> tag);
    }

    public TagListAdapter(Context context, List<Map<String, Object>> tags, OnTagClickListener listener) {
        this.context = context;
        this.tags = tags;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> tag = tags.get(position);
        
        String tagName = (String) tag.get("name");
        Integer usageCount = (Integer) tag.get("usageCount");

        holder.tvTagName.setText(tagName);
        holder.tvTagCount.setText("Sử dụng: " + (usageCount != null ? usageCount : 0));

        holder.itemView.setOnClickListener(v -> listener.onTagClick(tag));
        holder.tvRemove.setOnClickListener(v -> listener.onTagRemove(tag));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTagName;
        TextView tvTagCount;
        TextView tvRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTagName = itemView.findViewById(R.id.tv_tag_name);
            tvTagCount = itemView.findViewById(R.id.tv_tag_count);
            tvRemove = itemView.findViewById(R.id.tv_remove_tag);
        }
    }
}