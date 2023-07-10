package sg.edu.np.mad.EfficenZ;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sg.edu.np.mad.EfficenZ.model.Data;

// Define a DialogList class that extends Dialog
public abstract class CalendarDialog extends Dialog {
    private ArrayList<Data> list;
    private CalendarTaskAdapter adapter;
    private DatabaseReference mDatabase;
    RecyclerView recyclerView;
    private String selectedDate;

    public TextView dayOfMonth;
    public CalendarDialog(Context context, String selectedDate)
    {
        super(context);
        this.list = new ArrayList<>();
        this.selectedDate = selectedDate;

    }

    // This method is called when the Dialog is created
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState != null
                ? savedInstanceState
                : new Bundle());

        // Use the LayoutInflater to inflate the dialog_list layout file into a View object
        View view = LayoutInflater.from(getContext()).inflate(R.layout.calendar_data_dialog, null);

        // Set the dialog's content view to the newly created View object
        setContentView(view);

        // Allow the dialog to be dismissed by touching outside of it
        setCanceledOnTouchOutside(true);

        // Allow the dialog to be canceled by pressing the back button
        setCancelable(true);

        // Set up the RecyclerView in the dialog
        setUpRecyclerView(view);

        View view2 = LayoutInflater.from(getContext()).inflate(R.layout.calendar_cell, null);
        dayOfMonth = view2.findViewById(R.id.cellDayText);
    }

    // This method sets up the RecyclerView in the dialog
    private void setUpRecyclerView(View view)
    {
        // Find the RecyclerView in the layout file and set
        // its layout manager to a LinearLayoutManager
        RecyclerView recyclerView
                = view.findViewById(R.id.calendar_recycler);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));
        adapter = new CalendarTaskAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote");
        mDatabase.keepSynced(true);

        Query q = mDatabase.orderByChild("timestamp");
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                boolean hasTasks = false;
                for(DataSnapshot childItem: snapshot.getChildren()){
                    Data task = childItem.getValue(Data.class);
                    if(selectedDate.equals(task.getDueDate())){
                        list.add(task);
                        hasTasks = true;
                        //HolderWithTask(view);
                    }
                }
                if (list.size() == 0){
                    list.add(new Data("No task", "You are free today", selectedDate,
                            0, selectedDate, selectedDate, "", "", "", false));
                }
                list.sort((lhs, rhs)->{
                    if(lhs.getTask_status() == false && rhs.getTask_status() == true){
                        return -1;
                    }
                    else if(lhs.getTask_status() == true && rhs.getTask_status() == false){
                        return 1;
                    }
                    else{
                        // tied between status
                        // sort by due time
                        return Long.compare(lhs.getTimestamp(), rhs.getTimestamp());
                    }
                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.w(TAG, "load ")
            }
        });
    }

//    public void HolderWithTask(@NonNull View view){
//        dayOfMonth.setBackground(AppCompatResources.getDrawable(view.getContext(), R.drawable.rounded_corner_task));
//    }

}

