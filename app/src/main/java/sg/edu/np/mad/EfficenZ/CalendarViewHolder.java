package sg.edu.np.mad.EfficenZ;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnItemListener onItemListener;

    private int numberOfTask;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener)
    {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if(dayOfMonth.getText().toString().isEmpty()){
            return;
        }
        selectHolder();
        onItemListener.onItemClick(getBindingAdapterPosition(), (String) dayOfMonth.getText(), this);
    }

    public void selectHolder(){
        dayOfMonth.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.rounded_corner_solid));
    }
    public void unselectHolder(){
        updateUnselectHolder();
    }

    public void setNumberOfTask(int numTask) {
        numberOfTask = numTask;
        updateUnselectHolder();
    }

    public void updateUnselectHolder(){
        if(numberOfTask == 0){
            dayOfMonth.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.rounded_corner));
        }
        else if(numberOfTask > 0){
            dayOfMonth.setBackground(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.rounded_corner_task));
        }
    }
}
