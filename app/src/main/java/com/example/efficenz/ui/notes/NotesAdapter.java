package com.example.efficenz.ui.notes;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.efficenz.R;

import java.util.ArrayList;
import java.util.Random;

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Note> noteList;

    public NotesAdapter(ArrayList<Note> noteList, Context context){
        this.noteList = noteList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notes, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotesViewHolder viewHolder = (NotesViewHolder) holder;
        String title = noteList.get(position).getTitle();
        String content = noteList.get(position).getContent();
        viewHolder.title.setText(title);
        viewHolder.content.setText(content);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    class NotesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView content;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleText_item);
            content = itemView.findViewById(R.id.contentText_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Note notes = noteList.get(position);

                Intent intent = new Intent(context, NotesEdit.class);
                intent.putExtra("TITLE", notes.title);
                intent.putExtra("CONTENT", notes.content);
                context.startActivity(intent);
            }
        }
    }

}