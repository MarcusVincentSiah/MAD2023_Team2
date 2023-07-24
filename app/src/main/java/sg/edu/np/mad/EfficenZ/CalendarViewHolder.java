package sg.edu.np.mad.EfficenZ;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

/*
* The Circle UI for the day representation in each month.
* */
public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnItemListener onItemListener;

    private int numberOfTask;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener)
    {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);

        // bind to parent listener, to indicate which day is being click
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if(dayOfMonth.getText().toString().isEmpty()){
            return; // if the empty invalid day is being click, do nothing.
        }
        selectHolder(); // updated UI to show circle as being selected

        // trigger back to parent adapter which position is clicked and the day value.
        // So that parent will know how to open dialog.
        onItemListener.onItemClick(getBindingAdapterPosition(), (String) dayOfMonth.getText(), this);
    }

    // when being selected
    public void selectHolder(){
        // on selected, background will be blue highlight.
        dayOfMonth.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.rounded_corner_solid));
    }

    // when being unselected
    public void unselectHolder(){
        updateUnselectHolder();
    }

    public void setNumberOfTask(int numTask) {
        numberOfTask = numTask; // required for updateUnselectHolder to determine the default unselected bg color.
        updateUnselectHolder();
    }

    public void updateUnselectHolder(){
        if(numberOfTask == 0){
            // No task on this day, white bg color
            dayOfMonth.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.rounded_corner));
        }
        else if(numberOfTask <= 2) {
            dayOfMonth.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.rounded_corner_task));
        }
        else if(numberOfTask >= 3){
            // There is  task on this day, orange bg color
            dayOfMonth.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.rounded_corner_more_task));
        }
    }
}
