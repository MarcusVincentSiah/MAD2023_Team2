package com.example.efficenz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recycler_view_adapter extends RecyclerView.Adapter<recycler_view_adapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    ArrayList<song> songs;

    public recycler_view_adapter(Context context, ArrayList<song> songs, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.songs = songs;
        this.recyclerViewInterface = recyclerViewInterface;

    }

    @NonNull
    @Override
    public recycler_view_adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.recycler_view_row, parent,false);



        return new recycler_view_adapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull recycler_view_adapter.MyViewHolder holder, int position) {
        holder.songName.setText(songs.get(position).getSongName());

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView musicImage;
        TextView songName;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            musicImage = itemView.findViewById(R.id.imageView);
            songName = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });



        }
    }
}