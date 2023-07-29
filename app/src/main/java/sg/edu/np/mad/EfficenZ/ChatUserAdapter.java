package sg.edu.np.mad.EfficenZ;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;

import sg.edu.np.mad.EfficenZ.model.Data;

public class ChatUserAdapter extends FirebaseRecyclerAdapter<User, ChatUserAdapter.ChatUserHolder> {

    private Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatUserAdapter(@NonNull FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatUserHolder holder, int position, @NonNull User model) {
        String name = model.first_name + " " + model.last_name;
        holder.name.setText(name);
        holder.email.setText(model.email);
    }

    @NonNull
    @Override
    public ChatUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user,
                parent,false);
        return new ChatUserAdapter.ChatUserHolder(v);
    }

    class ChatUserHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView email;
        public ChatUserHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            email = itemView.findViewById(R.id.textEmail);

            itemView.setOnClickListener(v -> {
                //When user clicks on a task
                int position = getLayoutPosition();
                if (position != RecyclerView.NO_POSITION) {
                    DataSnapshot dataSnapshot = getSnapshots().getSnapshot(position);
                    User user = dataSnapshot.getValue(User.class);

                    Intent intent = new Intent(context, ChatMessagesActivity.class);
                    // Pass data to the profile activity using intent extras
                    intent.putExtra("User", user);
                    context.startActivity(intent);
                }
            });
        }
    }
}
