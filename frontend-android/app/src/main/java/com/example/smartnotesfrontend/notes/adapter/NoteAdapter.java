package com.example.smartnotesfrontend.notes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartnotesfrontend.R;
import com.example.smartnotesfrontend.data.model.Note;

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

    static class NoteViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtTitle;

        TextView txtContent;

        CardView cardNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle =
                    itemView.findViewById(R.id.txtTitle);

            txtContent =
                    itemView.findViewById(R.id.txtContent);

            cardNote =
                    itemView.findViewById(R.id.cardNote);
        }
    }
}