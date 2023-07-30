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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import sg.edu.np.mad.EfficenZ.model.Data;

public class ChatUserAdapter extends FirebaseRecyclerAdapter<User, ChatUserAdapter.ChatUserHolder> {

    private Context context;
    private FirebaseAuth mAuth;
    private String currentUserId; // Store the ID of the current logged-in user

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     * @param context
     * @param currentUserId
     */
    public ChatUserAdapter(@NonNull FirebaseRecyclerOptions<User> options, Context context, String currentUserId) {
        super(options);
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatUserHolder holder, int position, @NonNull User model) {
        String name = model.first_name + " " + model.last_name;
        holder.name.setText(name);

        // Get the FirebaseUser representing the current logged-in user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Skip binding the view if the current user's id matches the user's id in the model
        if (currentUser != null && currentUser.getUid().equals(model.getUserId())) {
            holder.itemView.getLayoutParams().height = 0;
            holder.itemView.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.itemView.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public ChatUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user,
                parent, false);
        return new ChatUserAdapter.ChatUserHolder(v);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    class ChatUserHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ChatUserHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        User user = getItem(position);

                        // Check if the user's ID matches the current logged-in user's ID
                        if (!user.userId.equals(currentUserId)) {
                            Intent intent = new Intent(context, ChatMessagesActivity.class);
                            // Pass data to the ChatMessagesActivity using intent extras
                            intent.putExtra("User", user);
                            context.startActivity(intent);
                        }
                    }
                }
            });
        }
    }

}
