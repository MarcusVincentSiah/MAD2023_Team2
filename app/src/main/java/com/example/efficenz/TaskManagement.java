package com.example.efficenz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.efficenz.model.Data;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class TaskManagement extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton penBtn;

    //Firebase
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    //Recycler
    private RecyclerView recyclerView;

    //Update input field
    private EditText titleUpdate;
    private EditText noteUpdate;
    private Button btnDeleteUp;
    private Button btnUpdateUp;

    //Variable
    private String title;
    private String note;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        //Toolbar crashes emulator for some reason
        // --------------------------------------------
//        toolbar = findViewById(R.id.toolbar_home);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Task Management");

        mAuth = FirebaseAuth.getInstance(); //not sure
//        FirebaseUser User = mAuth.getCurrentUser();
//        String Id = User.getid;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote");
        mDatabase.keepSynced(true);

        //Recycler
        recyclerView = findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setReverseLayout(true); //not sure
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        penBtn=findViewById(R.id.Pen_btn);
        penBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder myDialog = new AlertDialog.Builder(TaskManagement.this);
                LayoutInflater inflater = LayoutInflater.from(TaskManagement.this); //not sure
                View myview = inflater.inflate(R.layout.task_input_field, null);
                myDialog.setView(myview);
                final AlertDialog dialog = myDialog.create();

                EditText title = myview.findViewById(R.id.edit_title);
                EditText note = myview.findViewById(R.id.edit_note);

                Button btnSave = myview.findViewById(R.id.btn_save);
                btnSave.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        String mtitle = title.getText().toString().trim();
                        String mNote = note.getText().toString().trim(); //not sure

                        if (TextUtils.isEmpty(mtitle)){ //not sure
                            title.setError("Required Input");
                            return;
                        }
                        if (TextUtils.isEmpty(mNote)){ //not sure
                            title.setError("Required Input");
                            return;
                        }

                        String id = mDatabase.push().getKey();

                        String date = DateFormat.getDateInstance().format(new Date());

                        Data data = new Data(mtitle, mNote, date, id);

                        mDatabase.child(id).setValue(data);

                        Toast.makeText(getApplicationContext(), "Data Insert", Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); //Does not dismiss dialog for some reason
                    }
                });

                myDialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>() //not sure
                .setQuery(mDatabase, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position, @NonNull Data model) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());

                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //when a recycle view is clicked, updateData will be called. Which is the alert dialog

                        post_key=getRef(position).getKey(); //Getting position of the data in recycler view
                        title = model.getTitle(); //to show data in dialog when its clicked
                        note = model.getNote();


                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View myview;

        public MyViewHolder(View itemView) {
            super(itemView);
            myview = itemView;
        }

        public void setTitle(String title) {
            TextView mTitle = myview.findViewById(R.id.title);
            mTitle.setText(title);
        }

        public void setNote(String note) {
            TextView mNote = myview.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date) {
            TextView mDate = myview.findViewById(R.id.date);
            mDate.setText(date);
        }
    }

    public void updateData(){

        AlertDialog.Builder mydialog = new AlertDialog.Builder(TaskManagement.this);
        LayoutInflater inflater = LayoutInflater.from(TaskManagement.this);

        View myview = inflater.inflate(R.layout.updateinputfield, null);
        mydialog.setView(myview);

        AlertDialog dialog = mydialog.create();

        titleUpdate = myview.findViewById(R.id.edit_title_update);
        noteUpdate = myview.findViewById(R.id.edit_note_update);

        titleUpdate.setText(title);
        titleUpdate.setSelection(title.length()); //not sure

        noteUpdate.setText(note);
        noteUpdate.setSelection(note.length());

        btnDeleteUp = myview.findViewById(R.id.btn_delete_upd);
        btnUpdateUp = myview.findViewById(R.id.btn_update_upd);

        btnUpdateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                title = titleUpdate.getText().toString().trim(); //getting input info
                note = noteUpdate.getText().toString().trim();

                String mDate = DateFormat.getDateInstance().format(new Date()); //updating to new date

                Data data = new Data(title,note,mDate,post_key); //creating new data object

                mDatabase.child(post_key).setValue(data); //Changing Data for that ID to the new updated Data

                dialog.dismiss();
            }
        });

        btnDeleteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(post_key).removeValue(); //Delete value with that ID

                dialog.dismiss();
            }
        });

        dialog.show();

    }
}