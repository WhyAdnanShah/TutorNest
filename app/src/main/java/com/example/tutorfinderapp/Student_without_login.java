package com.example.tutorfinderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorfinderapp.databinding.ActivityStudentWithoutLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Student_without_login extends AppCompatActivity {
    private ActivityStudentWithoutLoginBinding binding;
    private RecyclerView recyclerView;
    private TutorAdapter tutorAdapter;
    private List<Tutor> tutorList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_student_without_login);
        binding.setLogin(this);

        //Check if the student is already logged in
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isStudentLoggedIn", false);
        if (isLoggedIn) {
            Intent intent = new Intent(Student_without_login.this, StudentActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_student_without_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tutorList = new ArrayList<>();
        tutorAdapter = new TutorAdapter(this, tutorList);
        recyclerView.setAdapter(tutorAdapter);

        // 🔥 Fetch data from Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Tutors");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tutorList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tutor tutor = dataSnapshot.getValue(Tutor.class);
                    tutorList.add(tutor);
                }
                tutorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database error: " + error.getMessage());
                Toast.makeText(Student_without_login.this, "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Login Button Click
        Button loginBtn = findViewById(R.id.LoginBtnStudent);
        loginBtn.setOnClickListener(v -> {
            // Navigate to StudentLogin
            Intent intent = new Intent(Student_without_login.this, StudentLogin.class);
            startActivity(intent);
            finish();
        });

    }
}
