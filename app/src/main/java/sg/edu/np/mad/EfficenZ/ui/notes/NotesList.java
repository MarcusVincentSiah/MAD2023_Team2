package sg.edu.np.mad.EfficenZ.ui.notes;

// NOTE TAKING
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.EfficenZ.R;

public class NotesList extends AppCompatActivity implements SearchView.OnQueryTextListener, FolderFragment.OnFolderDataPassListener {

    private RecyclerView recyclerView;
    //private NotesAdapter adapter;
    private SearchView searchView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference foldersCollection = db.collection("folders");
    private CollectionReference notesCollection = db.collection("notes");

    private Folder folder = new Folder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);


        // set fragment to FolderFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, new FolderFragment()).commit();

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        // CREATE FOLDER BUTTON
        ImageButton createFolder = findViewById(R.id.createFolder);
        createFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFolder(folder);
            }
        });


        // CREATE NOTE (floating button)

        FloatingActionButton createNoteBtn = findViewById(R.id.createNoteBtn);
        createNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesList.this, NotesEdit.class);
                intent.putExtra("FOLDERID", folder.getId());
                intent.putExtra("FOLDERNAME", folder.getName());
                startActivity(intent);

            }

        });
    }

    // CREATE FOLDER (show alert dialog to enter folder name then add folder to firestore)
    private void createFolder(Folder folder){
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        alertdialog.setTitle("Create Folder");

        EditText folderName = new EditText(this);

        // Set max length for folderName (15 characters)
        InputFilter[] editFilters = folderName.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.LengthFilter(15);
        folderName.setFilters(newFilters);

        folderName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        alertdialog.setView(folderName);

        alertdialog.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Folder folder = new Folder();
                folder.setName(folderName.getText().toString());
                folder.setId("Folder-"+String.valueOf(System.currentTimeMillis()));
                foldersCollection.document(folder.getId()).set(folder); // add folder to firestore
                Toast.makeText(getBaseContext(), "Folder created", Toast.LENGTH_SHORT).show();
            }
        });
        alertdialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertdialog.show();
    }


    /*
    private void setUpRecyclerView() {
        // FETCH DATA
        Query query = foldersCollection.orderBy("id", Query.Direction.DESCENDING);

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
                // Toast.makeText(NotesList.this, id , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(NotesList.this, NotesEdit.class);
                intent.putExtra("ID", id);
                intent.putExtra("TITLE", title);
                intent.putExtra("CONTENT", content);
                NotesList.this.startActivity(intent);
            }
        });

        adapter.setOnFolderClickListener(new NotesAdapter.OnFolderClickListener(){

            @Override
            public void onFolderClick(DocumentSnapshot documentSnapshot, int position, String folderName) {
                Query query1 = notesCollection.whereEqualTo("folder", folderName);

                FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                        .setQuery(query1, Note.class)
                        .build();

                adapter.updateOptions(options);
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


     */


    // receive folder data passed from FolderFragment
    @Override
    public void onFolderDataPass(String folderid, String folderName) {

        folder.setId(folderid);
        folder.setName(folderName);

    }

    // TODO: FIX SEARCH BOX
    // SEARCH NOTES
    /*
    private void searchNotes(String input) {

        //input = input.toLowerCase();

        Query query;

        if (input.isEmpty()) {
            // If the search input is empty, display all notes
            query = notesCollection.orderBy("title", Query.Direction.DESCENDING);
        } else {
            // Perform search
            query = notesCollection.whereGreaterThanOrEqualTo("title", input).whereLessThanOrEqualTo("title", input + "\uf8ff");
        }

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        adapter.updateOptions(options);
    }

     */

    /*
    private void searchNotes(String query) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, new NotesFragment()).commit();

        // Assuming "foldersCollection" is your Firestore collection reference for folders
        foldersCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> folders = queryDocumentSnapshots.getDocuments();
                        List<DocumentSnapshot> results = new ArrayList<>();

                        for (DocumentSnapshot folder : folders) {
                            String folderId = folder.getId();
                            CollectionReference notesCollection = foldersCollection.document(folderId).collection("notes");

                            notesCollection.whereGreaterThanOrEqualTo("title", query).whereLessThanOrEqualTo("title", query + "\uf8ff")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            results.addAll(queryDocumentSnapshots.getDocuments());
                                            // Update your adapter with the search results or perform any necessary operations
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle any errors that occur during the search
                                        }
                                    });
                        }

                        // Handle the search results after all queries have completed
                        // Update your adapter with the final search results or perform any necessary operations
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occur during the search
                    }
                });
    }

     */

    private void searchNotes(String input){ // BROKEN (for now)

        Query query = db.collectionGroup("notes").whereGreaterThanOrEqualTo("title", input).whereLessThanOrEqualTo("title", input + "\uf8ff");

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        NotesAdapter adapter = new NotesAdapter(options);

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