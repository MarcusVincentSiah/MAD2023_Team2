package sg.edu.np.mad.EfficenZ;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class NotificationAdapter extends FirestoreRecyclerAdapter<NotificationModel, NotificationAdapter.NotificationViewHolder> {

    Context context;

    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<NotificationModel> options, @NonNull Context context) {
        super(options);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull NotificationModel notificationModel) {
        holder.title.setText(notificationModel.getTitle());
        holder.content.setText(notificationModel.getContent());
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        Log.v("CCCCCCCCBBBB", "view created");
        return new NotificationViewHolder(view);
    }


    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleText_item);
            content = itemView.findViewById(R.id.contentText_item);
        }
    }


}
