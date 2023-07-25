package sg.edu.np.mad.EfficenZ.ui.notes;


// NOTE TAKING
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import sg.edu.np.mad.EfficenZ.R;

public class FolderAdapter extends FirestoreRecyclerAdapter<Folder, FolderAdapter.FolderViewHolder> {

    private Context context;
    private FolderAdapter.OnFolderClickListener listener;

    public FolderAdapter(@NonNull FirestoreRecyclerOptions<Folder> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull FolderAdapter.FolderViewHolder holder, int position, @NonNull Folder model) {
        String folderName = model.getName();
        holder.folderName.setText(folderName);
    }

    @NonNull
    @Override
    public FolderAdapter.FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notes_folder, parent, false);
        Log.v("VIEW_FOLDER", "CREATED");
        return new FolderViewHolder(view);
    }


    class FolderViewHolder extends RecyclerView.ViewHolder {
        TextView folderName;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
            ImageButton folderOptions = itemView.findViewById(R.id.folderOptions);

            // handle folder click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onFolderClick(getSnapshots().getSnapshot(position), position);


                    }
                }
            });

            // handle folder menu click (3 vertical dots)
            folderOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int positon = getAbsoluteAdapterPosition();
                    String folderid = getSnapshots().getSnapshot(positon).getString("id");
                    showFolderOptions(v, folderid);
                }
            });
        }
    }

    // handle folder clicks
    public interface OnFolderClickListener {
        void onFolderClick(DocumentSnapshot documentSnapshot, int position);
    }
    public void setOnFolderClickListener(FolderAdapter.OnFolderClickListener listener){
        this.listener = listener;
    }

    // handle folder menu (display edit and delete)
    private void showFolderOptions(View view, String folderid){
        PopupMenu folderOptions = new PopupMenu(view.getContext(), view);
        folderOptions.inflate(R.menu.folder_option);

        folderOptions.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.optionEdit){
                    editFolder(folderid);
                    return true;
                }
                else if (item.getItemId() == R.id.optionDelete){
                    deleteFolder(folderid);
                    return true;
                }
                return false;
            }
        });
        folderOptions.show();
    }

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId = mAuth.getCurrentUser().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollection = db.collection("users");
    private CollectionReference foldersCollection = userCollection.document(userId).collection("folders");

    // EDIT FOLDER NAME
    private void editFolder(String folderid) {
        // show AlertDialog with EditText
        AlertDialog.Builder editDialog = new AlertDialog.Builder(context);
        editDialog.setTitle("Rename Folder");

        EditText folderName = new EditText(context);

        // Set max length for folderName (15 characters)
        InputFilter[] editFilters = folderName.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.LengthFilter(15);
        folderName.setFilters(newFilters);

        folderName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        editDialog.setView(folderName);

        editDialog.setPositiveButton("RENAME", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // update folder name in FireStore database
                foldersCollection.document(folderid)
                        .update("name", folderName.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Renamed folder successfully! :)", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        editDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        editDialog.show();
    }

    // DELETE FOLDER
    private void deleteFolder(String folderid) {

        // AlertDialog to show warning and receive user's confirmation
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
        deleteDialog.setTitle("CAUTION");
        deleteDialog.setMessage("Deleting this folder will also delete all the contents inside. Proceed?");
        deleteDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete folder and notes from FireStore database
                foldersCollection.document(folderid)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Folder deleted successfully!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to delete folder :(", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        deleteDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        deleteDialog.show();

    }
}
