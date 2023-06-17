package com.example.efficenz.ui.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

    //FirebaseFirestore db = FirebaseFirestore.getInstance();
    //EditText titleText, contentText;
    private FirebaseFirestore db;
    private CollectionReference notesCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_edit);

        db = FirebaseFirestore.getInstance();
        notesCollection = db.collection("notes");

        EditText titleText = findViewById(R.id.titleText);
        EditText contentText = findViewById(R.id.contentText);

        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        String content = intent.getStringExtra("CONTENT");

        titleText.setText(title);
        contentText.setText(content);

        FloatingActionButton saveBtn =findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(titleText, contentText);
            }
        });
    }

    void saveNote(EditText titleText, EditText contentText)
    {
        // save note
        String title = titleText.getText().toString();
        String content = contentText.getText().toString();

        Note note = new Note();
        //note.setId();
        note.setTitle(title);
        note.setContent(content);

        notesCollection.add(note)
                .addOnSuccessListener(documentReference -> {
                    // Note saved successfully
                    Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error occurred while saving the note
                    Toast.makeText(this, "Error saving note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}