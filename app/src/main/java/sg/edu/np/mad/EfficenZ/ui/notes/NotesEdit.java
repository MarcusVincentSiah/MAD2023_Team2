package sg.edu.np.mad.EfficenZ.ui.notes;

// NOTE TAKING
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;

import sg.edu.np.mad.EfficenZ.R;

public class NotesEdit extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference notesCollection;
    private CollectionReference foldersCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_edit);

        TextView pageTitle = findViewById(R.id.pageTitle);
        EditText titleText = findViewById(R.id.titleText);
        EditText contentText = findViewById(R.id.contentText);
        ImageButton saveBtn = findViewById(R.id.saveBtn);
        ImageButton deleteBtn = findViewById(R.id.deleteBtn);

        db = FirebaseFirestore.getInstance();
        //notesCollection = db.collection("notes");
        foldersCollection = db.collection("folders");

        // receive data from NotesList/NotesAdapter
        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        String content = intent.getStringExtra("CONTENT");
        String id = intent.getStringExtra("ID");
        String folderid = intent.getStringExtra("FOLDERID");
        String folderName = intent.getStringExtra("FOLDERNAME");

        // set EditText
        titleText.setText(title);
        contentText.setText(content);


        // check whether new note or existing note
        String noteid;
        if (id == null){
            pageTitle.setText("New Note");
            deleteBtn.setVisibility(View.GONE); // remove delete button in new note
            noteid = "Note-"+String.valueOf(System.currentTimeMillis()); // generate id for new note

        } else {
            pageTitle.setText("Edit Note");
            noteid = id;
        }

        // SAVE button
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validate empty notes (cannot save empty notes)
                if (titleText.getText().toString().isEmpty() && contentText.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(), "Failed to save: Please enter some text.", Toast.LENGTH_SHORT).show();
                }
                else {
                    saveNote(titleText, contentText, noteid, folderid, folderName);
                }
            }
        });

        // DELETE button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote(folderid, noteid);
            }
        });
    }

    // SAVE NOTE
    void saveNote(EditText titleText, EditText contentText, String id, String folderid, String folderName)
    {
        // convert to string
        String title = titleText.getText().toString();
        String content = contentText.getText().toString();

        // create note object
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setContent(content);


        // create folder object
        Folder folder = new Folder();
        if (folderid == null){ // if note is created outside a folder, note will be placed in "Others" folder
            folder.setName("Others");
            folder.setId("Folder-0");
            note.setFolderId("Folder-0");
        }
        else {
            folder.setName(folderName);
            folder.setId(folderid);
            note.setFolderId(folderid);
        }

        /*
        notesCollection = db.collection("notes");
        // save note to firestore
        notesCollection.document(id)
                .set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(NotesEdit.this, "Note saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotesEdit.this, e+"\nFailed to save note :(", Toast.LENGTH_SHORT).show();
                    }
                }); */

        notesCollection = foldersCollection.document(note.getFolderId()).collection("notes"); // sub-collection

        // parent-collection
        foldersCollection.document(folder.getId())
                .set(folder)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // sub-collection
                        notesCollection.document(note.getId())
                                .set(note)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(NotesEdit.this, "Note saved in " + folder.getName() + "! :D", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(NotesEdit.this, e+"\nFailed to save note :(", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotesEdit.this, e+"\nFailed to save note :(", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // DELETE NOTE
    private void deleteNote(String folderid, String noteid) {

        // folders > folderid > notes > noteid
        foldersCollection.document(folderid).collection("notes").document(noteid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(NotesEdit.this, "Note deleted", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotesEdit.this, e+"\nFailed to delete note", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}