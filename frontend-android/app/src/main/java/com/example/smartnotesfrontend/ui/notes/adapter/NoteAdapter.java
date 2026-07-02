package com.example.smartnotesfrontend.ui.notes.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartnotesfrontend.R;
import com.example.smartnotesfrontend.data.model.Category;
import com.example.smartnotesfrontend.data.model.Note;
import com.example.smartnotesfrontend.data.model.ScheduleMode;

import java.util.List;

public class NoteAdapter
        extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final List<Note> noteList;

    private final OnNoteClickListener clickListener;

    private final OnNoteLongClickListener longClickListener;

    // CLICK
    public interface OnNoteClickListener {
        void onClick(Note note);
    }

    // LONG CLICK
    public interface OnNoteLongClickListener {
        void onLongClick(Note note);
    }

    public NoteAdapter(
            List<Note> noteList,
            OnNoteClickListener clickListener,
            OnNoteLongClickListener longClickListener
    ) {

        this.noteList = noteList;

        this.clickListener = clickListener;

        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_note,
                        parent,
                        false
                );

        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull NoteViewHolder holder,
            int position
    ) {

        Note note = noteList.get(position);

        holder.txtTitle.setText(note.getTitle());

        holder.txtContent.setText(note.getContent());

        // Binding Category Badge
        Category category = note.getCategory();
        holder.txtCategoryBadge.setVisibility(View.VISIBLE);
        
        String colorCode;
        if (category != null) {
            holder.txtCategoryBadge.setText(category.getName());
            colorCode = category.getColorCode();
        } else {
            holder.txtCategoryBadge.setText("Non-category");
            colorCode = "#D3D3D3"; // Light Gray
        }

        int color = Color.LTGRAY;
        if (colorCode != null && !colorCode.isEmpty()) {
            try {
                color = Color.parseColor(colorCode);
            } catch (IllegalArgumentException ignored) {}
        }
        holder.txtCategoryBadge.setBackgroundTintList(ColorStateList.valueOf(color));
        
        if (isColorDark(color)) {
            holder.txtCategoryBadge.setTextColor(Color.WHITE);
        } else {
            holder.txtCategoryBadge.setTextColor(Color.BLACK);
        }

        // Binding Schedule Badge
        if (note.getScheduleMode() != null) {
            holder.txtScheduleBadge.setVisibility(View.VISIBLE);
            
            String dateStr = "";
            if (note.getScheduleMode() == ScheduleMode.EVERYDAY) {
                dateStr = "Hàng ngày";
            } else {
                dateStr = note.getScheduleDate() != null ? note.getScheduleDate() : "Chọn ngày";
            }
            
            String timeStr = note.getNotifyTime() != null ? " lúc " + note.getNotifyTime() : "";
            holder.txtScheduleBadge.setText("⏰ " + dateStr + timeStr);
        } else {
            holder.txtScheduleBadge.setVisibility(View.GONE);
        }

        holder.cardNote.setOnClickListener(v -> {

            clickListener.onClick(note);
        });

        holder.cardNote.setOnLongClickListener(v -> {

            longClickListener.onLongClick(note);

            return true;
        });
    }

    @Override
    public int getItemCount() {

        return noteList.size();
    }

    private boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    static class NoteViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtTitle;

        TextView txtContent;

        TextView txtCategoryBadge;
        TextView txtScheduleBadge;

        CardView cardNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle =
                    itemView.findViewById(R.id.txtTitle);

            txtContent =
                    itemView.findViewById(R.id.txtContent);

            txtCategoryBadge =
                    itemView.findViewById(R.id.txtCategoryBadge);

            txtScheduleBadge =
                    itemView.findViewById(R.id.txtScheduleBadge);

            cardNote =
                    itemView.findViewById(R.id.cardNote);
        }
    }
}