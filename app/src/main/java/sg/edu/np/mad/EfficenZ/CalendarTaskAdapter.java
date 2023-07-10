package sg.edu.np.mad.EfficenZ;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import sg.edu.np.mad.EfficenZ.model.Data;

public class CalendarTaskAdapter extends RecyclerView.Adapter<CalendarTaskAdapter.CalendarViewHolder> {
    private List<Data> items;
    private Context context;

    public CalendarTaskAdapter(Context context, List<Data> items) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        Data item = items.get(position);
        holder.taskTitle.setText(item.getTitle());
        holder.taskNote.setText(item.getNote());
        holder.taskDueDate.setText(item.getDueDate());
        holder.taskDueTime.setText(item.getDueTime());
        holder.taskStatus.setText(item.getTask_status()?"Completed":"Pending");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskNote;
        TextView taskDueDate;
        TextView taskDueTime;
        TextView taskStatus;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.calendar_title);
            taskNote = itemView.findViewById(R.id.calendar_note);
            taskDueDate = itemView.findViewById(R.id.calendar_duedate);
            taskDueTime = itemView.findViewById(R.id.calendar_duetime);
            taskStatus = itemView.findViewById(R.id.calendar_status);
        }
    }
}

