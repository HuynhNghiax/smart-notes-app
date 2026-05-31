package com.example.smartnotesfrontend.ui.search;

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

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private Context context;
    private List<Map<String, Object>> searchResults;

    public SearchResultAdapter(Context context, List<Map<String, Object>> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> note = searchResults.get(position);
        
        String title = (String) note.get("title");
        String content = (String) note.get("content");

        holder.tvTitle.setText(title);
        holder.tvContent.setText(content != null ? content.substring(0, Math.min(100, content.length())) + "..." : "");
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_search_title);
            tvContent = itemView.findViewById(R.id.tv_search_content);
        }
    }
}