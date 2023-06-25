package sg.edu.np.mad.EfficenZ.ui.notes;

// NOTE TAKING
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import sg.edu.np.mad.EfficenZ.R;

public class NotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotesAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notesCollection;
    private OnFragmentChangeListener fragmentChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        //setUpRecyclerView(view);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // receive folderid from FolderFragment
        Bundle bundle = this.getArguments();
        String folderid = bundle.getString("FOLDERID");
        String folderName = bundle.getString("FOLDERNAME");

        setUpRecyclerView(view, folderid, folderName);

        // hide create folder button
        ImageButton createFolder = getActivity().findViewById(R.id.createFolder);
        createFolder.setVisibility(View.GONE);
    }

    private void setUpRecyclerView(View view, String folderid, String folderName) {
        notesCollection = db.collection("folders").document(folderid).collection("notes");

        Query query = notesCollection.orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        adapter = new NotesAdapter(options);

        recyclerView = view.findViewById(R.id.notesRecyclerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        /* DEBUGGING
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Note note = documentSnapshot.toObject(Note.class);
                        Log.d("NotesFragment_2", "Note: " + note.getTitle() + ", " + note.getContent());
                    }
                } else {
                    Log.d("NotesFragment_2", "No notes found");
                }
            }
        });

         */

        adapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(DocumentSnapshot documentSnapshot, int position) {
                //Note note = documentSnapshot.toObject(Note.class);
                String id = documentSnapshot.getId();
                String title = documentSnapshot.getString("title");
                String content = documentSnapshot.getString("content");
                //String folderid = documentSnapshot.getString("folderid");
                //Toast.makeText(NotesList.this, id , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), NotesEdit.class);
                intent.putExtra("ID", id);
                intent.putExtra("TITLE", title);
                intent.putExtra("CONTENT", content);

                intent.putExtra("FOLDERID", folderid);
                intent.putExtra("FOLDERNAME", folderName);

                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentChangeListener.onFragmentChanged("NotesFragment");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentChangeListener = (OnFragmentChangeListener) context;
    }

}