package com.example.efficenz.ui.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.efficenz.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

public class NotesEdit extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference notesCollection;

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
        notesCollection = db.collection("notes");

        // receive data from NotesList/NotesAdapter
        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        String content = intent.getStringExtra("CONTENT");
        String id = intent.getStringExtra("ID");

        // set EditText
        titleText.setText(title);
        contentText.setText(content);


        boolean newNote;
        if (id == null){ // check whether new note or existing note
            pageTitle.setText("New Note");
            deleteBtn.setVisibility(View.GONE); // remove delete button in new note
            newNote = true;
        } else {
            pageTitle.setText("Edit Note");
            newNote = false;
        }

        // save button
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(newNote){
                    saveNote(titleText, contentText);
                }
                else{
                    updateNote(titleText,contentText, id);
                }

            }
        });

        // delete button
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote(id);
            }
        });
    }

    void saveNote(EditText titleText, EditText contentText)
    {
        // save note
        String title = titleText.getText().toString();
        String content = contentText.getText().toString();
        String id = String.valueOf(System.currentTimeMillis());

        // create note object
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setContent(content);


        // Save the note to the Firestore database
        notesCollection.document(id)
                .set(note)
                .addOnSuccessListener(documentReference -> {
                    // Note saved successfully
                    Toast.makeText(NotesEdit.this, "Note saved", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity
                })
                .addOnFailureListener(e -> {
                    // Failed to save note
                    Toast.makeText(NotesEdit.this, "Failed to save note", Toast.LENGTH_SHORT).show();
                });



        //Intent intent = new Intent(NotesEdit.this, NotesList.class);
        //NotesEdit.this.startActivity(intent);
    }

    private void updateNote(EditText titleText, EditText contentText, String noteId) {
        // Update an existing note in Firestore
        String updatedTitle = titleText.getText().toString();
        String updatedContent = contentText.getText().toString();

        Note note = new Note();
        note.setTitle(updatedTitle);
        note.setContent(updatedContent);
        note.setId(noteId);

        notesCollection.document(noteId)
                .set(note)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NotesEdit.this, "Note updated", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NotesEdit.this, "Failed to update note", Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteNote(String noteId) {
        notesCollection.document(noteId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NotesEdit.this, "Note deleted", Toast.LENGTH_SHORT).show();
                    finish(); // Finish the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NotesEdit.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                });
    }
}