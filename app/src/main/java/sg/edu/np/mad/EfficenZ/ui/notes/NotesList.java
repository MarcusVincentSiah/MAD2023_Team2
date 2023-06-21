package sg.edu.np.mad.EfficenZ.ui.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import sg.edu.np.mad.EfficenZ.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class NotesList extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Note> noteList;
    private ArrayList<Note> filteredNoteList;
    private SearchView searchView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notesCollection = db.collection("notes");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        setUpRecyclerView();

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        //noteList = new ArrayList<>();

        FloatingActionButton createNoteBtn = findViewById(R.id.createNoteBtn);
        createNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesList.this, NotesEdit.class);
                startActivity(intent);

            }

        });
    }

    private void setUpRecyclerView() {
        Query query = notesCollection.orderBy("id", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        adapter = new NotesAdapter(options);

        recyclerView = findViewById(R.id.notesRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(DocumentSnapshot documentSnapshot, int position) {
                //Note note = documentSnapshot.toObject(Note.class);
                String id = documentSnapshot.getId();
                String title = documentSnapshot.getString("title");
                String content = documentSnapshot.getString("content");
                //Toast.makeText(NotesList.this, id , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(NotesList.this, NotesEdit.class);
                intent.putExtra("ID", id);
                intent.putExtra("TITLE", title);
                intent.putExtra("CONTENT", content);
                NotesList.this.startActivity(intent);
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // SEARCH NOTES
    private void searchNotes(String input) {

        input = input.toLowerCase();

        Query query;

        if (input.isEmpty()) {
            // If the search input is empty, display all notes
            query = notesCollection.orderBy("id", Query.Direction.DESCENDING);
        } else {
            // Perform a case-insensitive search by the 'title' field
            query = notesCollection.whereGreaterThanOrEqualTo("title", input).whereLessThanOrEqualTo("title", input + "\uf8ff");
        }

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        adapter.updateOptions(options);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchNotes(newText);
        return true;
    }




}