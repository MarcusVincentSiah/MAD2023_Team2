package sg.edu.np.mad.EfficenZ.ui.notes;

// NOTE TAKING
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import sg.edu.np.mad.EfficenZ.R;

public class FolderFragment extends Fragment {

    private RecyclerView recyclerView;
    private FolderAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference foldersCollection = db.collection("folders");

    private OnFolderDataPassListener dataPassListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView(view);
    }

    private void setUpRecyclerView(View view) {
        // FETCH DATA
        Query query = foldersCollection.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Folder> options = new FirestoreRecyclerOptions.Builder<Folder>()
                .setQuery(query, Folder.class)
                .build();

        adapter = new FolderAdapter(options, view.getContext());

        recyclerView = view.findViewById(R.id.notesRecyclerView1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnFolderClickListener(new FolderAdapter.OnFolderClickListener() {
            @Override
            public void onFolderClick(DocumentSnapshot documentSnapshot, int position) {
                String folderid = documentSnapshot.getId();
                String folderName = documentSnapshot.getString("name");

                // remove create folder button
                //ImageButton createFolder = view.findViewById(R.id.createFolder);
                //createFolder.setVisibility(View.GONE);

                // pass foldername and folderid to NotesFragment (needed for NotesEdit)
                Bundle bundle = new Bundle();
                bundle.putString("FOLDERNAME", folderName);
                bundle.putString("FOLDERID", folderid);
                NotesFragment notesFragment = new NotesFragment();
                notesFragment.setArguments(bundle);

                // pass folderName and folderid to NotesList (needed for the create note button)
                dataPassListener.onFolderDataPass(folderid, folderName);


                // start NotesFragment
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment, notesFragment)
                        .addToBackStack(notesFragment.toString()) // return to prev fragment when back button is pressed.
                        .commit();
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

    public interface OnFolderDataPassListener {
        void onFolderDataPass(String folderid, String folderName);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPassListener = (OnFolderDataPassListener) context;
    }

}