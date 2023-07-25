package sg.edu.np.mad.EfficenZ.ui.notes;

// NOTE TAKING
import android.content.Context;
import android.content.SharedPreferences;
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

    private SharedPreferences prefs; // = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
    //private String userId = prefs.getString("userId", null);
    private RecyclerView recyclerView;
    private FolderAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollection = db.collection("users");
    private CollectionReference foldersCollection; //= userCollection.document(userId).collection("folders");

    private OnFolderDataPassListener dataPassListener;
    private OnFragmentChangeListener fragmentChangeListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        foldersCollection = userCollection.document(userId).collection("folders");

        setUpRecyclerView(view, foldersCollection);

        // show create folder button
        ImageButton createFolder = getActivity().findViewById(R.id.createFolder);
        createFolder.setVisibility(View.VISIBLE);
    }

    private void setUpRecyclerView(View view, CollectionReference foldersCollection) {
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

        // Handle folder click
        adapter.setOnFolderClickListener(new FolderAdapter.OnFolderClickListener() {
            @Override
            public void onFolderClick(DocumentSnapshot documentSnapshot, int position) {
                String folderid = documentSnapshot.getId();
                String folderName = documentSnapshot.getString("name");

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

    @Override
    public void onResume() {
        super.onResume();
        // Pass fragment name to parent activity (NotesList) (used for search)
        fragmentChangeListener.onFragmentChanged("FolderFragment");
    }

    // Pass folderName and folderID to parent activity (NotesList)
    public interface OnFolderDataPassListener {
        void onFolderDataPass(String folderid, String folderName);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPassListener = (OnFolderDataPassListener) context;
        fragmentChangeListener = (OnFragmentChangeListener) context;
    }

}