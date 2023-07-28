package sg.edu.np.mad.EfficenZ;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class achievements_recyclerview_adapter extends RecyclerView.Adapter<achievements_recyclerview_adapter.ViewHolder> {
    private final Achievement_RecyclerViewInterface achievementRecyclerViewInterface;

    ArrayList<Achievement> achievements;
    Context context;



    public achievements_recyclerview_adapter(Context context, ArrayList<Achievement> achievements, Achievement_RecyclerViewInterface achievementRecyclerViewInterface) {
        this.context = context;
        this.achievements = achievements;
        this.achievementRecyclerViewInterface = achievementRecyclerViewInterface;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView achievementName;
        CardView cardView;


        public ViewHolder(View itemView, Achievement_RecyclerViewInterface achievementRecyclerViewInterface) {
            super(itemView);
            achievementName = itemView.findViewById(R.id.achievementName);
            cardView = itemView.findViewById(R.id.achievementCardView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (achievementRecyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            achievementRecyclerViewInterface.onItemClick(pos);
                        }

                    }
                }
            });

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.achievement_row, parent, false);
        return new ViewHolder(view, achievementRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Achievement currentAchievement = achievements.get(position);
        holder.achievementName.setText(currentAchievement.getName());
        Log.d("Adapter", "Achievement at position " + position + " isCompleted: " + currentAchievement.isCompleted);

        // Update the card view's background color based on the completion status
        if (currentAchievement.isCompleted) {
            // Set completed background color
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_color_light));
        } else {
            // Set incomplete background color
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.defaultAchievementColor));
        }
    }



    @Override
    public int getItemCount() {
        return achievements.size();
    }
}
