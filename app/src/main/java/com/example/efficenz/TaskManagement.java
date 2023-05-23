package com.example.efficenz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskManagement extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton penBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        //Toolbar crashes emulator for some reason
        // --------------------------------------------
//        toolbar = findViewById(R.id.toolbar_home);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Task Management");

        penBtn=findViewById(R.id.Pen_btn);

        penBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });
    }
}