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

/*
* CalendarAdaper that manages the calendar days Circle UI.
* Update task count of each day, so that days with task will have indicator for user to know there is tasks.
* */
class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<String> daysOfMonth;
    private final ArrayList<String> dateInMonth;
    private final OnItemListener onItemListener;

    private final ArrayList<Data> taskList;


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

    // CalendarViewHolder is the Circle UI with the date number
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        if(daysOfMonth.get(position).isEmpty()){
            // Hide the circle that is not part of the day of Month
            holder.dayOfMonth.setBackground(null);
        }
        else{
            int count = 0;
            // how many task on this day.
            for (Data d : taskList) {
                if (d.getDueDate().equals(dateInMonth.get(position))){
                    count += 1;
                }
            }

            // update the task count to the circle and circle will self update its own color
            // indicator to the user
            holder.setNumberOfTask(count);
        }

        // update to empty or the rightful date number of the day of month.
        holder.dayOfMonth.setText(daysOfMonth.get(position));

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

}
