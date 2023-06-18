package com.example.efficenz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

public class TimeManagementTaskAdapter extends FirebaseRecyclerAdapter<TaskManagementData, TimeManagementTaskAdapter.TimeManagementTaskHolder> {

    private OnItemClickListener listener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TimeManagementTaskAdapter(@NonNull FirebaseRecyclerOptions<TaskManagementData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TimeManagementTaskHolder holder, int position, @NonNull TaskManagementData model) {
        holder.textviewTask.setText(model.getTitle());
    }

    @NonNull
    @Override
    public TimeManagementTaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_management_task_item,
                parent,false);
        return new TimeManagementTaskHolder(v);
    }

    class TimeManagementTaskHolder extends RecyclerView.ViewHolder {
        TextView textviewTask;

        public TimeManagementTaskHolder(@NonNull View itemView) {
            super(itemView);
            textviewTask = itemView.findViewById(R.id.task_item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {

                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private void showAlertDialog() {
        Context context = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Profile")
                .setMessage("Hello")
                .setPositiveButton("View", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open user profile activity
                        // Replace UserProfileActivity.class with your actual activity class
                        Intent intent = new Intent(context, MainActivity.class);
                        // Pass any necessary data to the profile activity using intent extras
                        intent.putExtra("USER_OBJECT", 1);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Close", null)
                .show();
    }
}
