package sg.edu.np.mad.EfficenZ;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.EfficenZ.model.Data;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<String> daysOfMonth;
    private final ArrayList<String> dateInMonth;
    private final OnItemListener onItemListener;

    private final ArrayList<Data> taskList;

    private int prevClickPosition;

    private DatabaseReference mDatabase;

    public CalendarAdapter(ArrayList<String> daysOfMonth, ArrayList<String> dateInMonth, OnItemListener onItemListener, ArrayList<Data> taskList)
    {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.taskList = taskList;
        this.dateInMonth = dateInMonth;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        if(daysOfMonth.get(position).isEmpty()){
            holder.dayOfMonth.setBackground(null);
        }
        else{
            int count = 0;
            for (Data d : taskList) {
                if (d.getDueDate().equals(dateInMonth.get(position))){
                    count += 1;
                }
            }
            holder.setNumberOfTask(count);
        }

        holder.dayOfMonth.setText(daysOfMonth.get(position));


//        String dayText = daysOfMonth.get(position);
//        if (dayText.isEmpty()) {
//            holder.dayOfMonth.setBackground(null);
//        } else {
//            holder.dayOfMonth.setText(dayText);
//            // Check if there is a task for this day
//            boolean hasTask = checkIfDayHasTask(dayText);
//            // Set the background color based on the task availability
//            if (hasTask) {
//                holder.dayOfMonth.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rounded_corner_task));
//            } else {
//                holder.dayOfMonth.setBackground(null);
//            }
//        }

    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, String dayText, CalendarViewHolder holder);
    }

//    private boolean checkIfDayHasTask(String dayText) {
//        for (Data task : tasks) {
//            if (dayText.equals(task.getDueDate())) {
//                return true;
//            }
//        }
//        return false;
//    }
}
