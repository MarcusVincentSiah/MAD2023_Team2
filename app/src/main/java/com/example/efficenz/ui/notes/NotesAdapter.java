package com.example.efficenz.ui.notes;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.efficenz.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Random;

public class NotesAdapter extends FirestoreRecyclerAdapter<Note, NotesAdapter.NotesViewHolder> {

    private Context context;
    private OnNoteClickListener listener;
    public NotesAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotesAdapter.NotesViewHolder holder, int position, @NonNull Note model) {
        String title = model.getTitle();
        String content = model.getContent();
        String preview;

        // preview content of notes
        if (content.length() > 100){
            preview = content.substring(0, 100) + "...";
        } else {
            preview = content;
        }

        holder.title.setText(title);
        holder.content.setText(preview);
    }

    @NonNull
    @Override
    public NotesAdapter.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notes, parent, false);
        return new NotesViewHolder(view);
    }

    class NotesViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView content;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleText_item);
            content = itemView.findViewById(R.id.contentText_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onNoteClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }
    public interface OnNoteClickListener {
        void onNoteClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnNoteClickListener(OnNoteClickListener listener){
        this.listener = listener;
    }
}