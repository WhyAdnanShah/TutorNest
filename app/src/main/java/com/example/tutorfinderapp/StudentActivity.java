package com.example.tutorfinderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorfinderapp.databinding.StudentActivityBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends AppCompatActivity {

    private StudentActivityBinding binding;
    private RecyclerView recyclerView;
    private TutorAdapter tutorAdapter;
    private List<Tutor> tutorList;
    private List<Tutor> filteredTutorList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Binding
        binding = DataBindingUtil.setContentView(this, R.layout.student_activity);
        binding.setStudentLoggedIn(this);

        //getting username and standard of the student
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "Unknown");
        String standard = prefs.getString("standard", "Unknown");

        TextView name_of_student = findViewById(R.id.name_of_student);
        name_of_student.setText(username);
        TextView standard_of_student = findViewById(R.id.standard_of_student);
        standard_of_student.setText(standard);


        // Initializing the RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tutorList = new ArrayList<>();
        filteredTutorList = new ArrayList<>();
        tutorAdapter = new TutorAdapter(this, filteredTutorList);
        recyclerView.setAdapter(tutorAdapter);

        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Tutors");

        // Fetch all tutors from Firebase initially
        fetchAllTutors();

        // Add TextChangeListener to EditText to listen for user input
        binding.username.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String input = charSequence.toString().trim();
                if (!input.isEmpty()) {
                    filterTutors(input);
                } else {
                    // Show all if empty
                    filteredTutorList.clear();
                    filteredTutorList.addAll(tutorList);
                    tutorAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });
    }

    // Fetch all tutor details from Firebase
    private void fetchAllTutors() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tutorList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tutor tutor = dataSnapshot.getValue(Tutor.class);
                    tutorList.add(tutor);
                }
                // Initially show the full list
                filteredTutorList.clear();
                filteredTutorList.addAll(tutorList);
                tutorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Database error: " + error.getMessage());
                Toast.makeText(StudentActivity.this, "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Filter the tutor list based on the entered name and Subject
    private void filterTutors(String query) {
        filteredTutorList.clear();
        for (Tutor tutor : tutorList) {
            if (tutor.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                    tutor.getSubject().toLowerCase().contains(query.toLowerCase())) {
                filteredTutorList.add(tutor);
            }
        }
        tutorAdapter.notifyDataSetChanged();
    }


    public void onProfileClick(View view) {
        CardView profileCard = findViewById(R.id.profile_card);
        View blurOverlay = findViewById(R.id.blur_overlay);
        Button closeBtn = findViewById(R.id.close_profile);
        Button logoutBtn = findViewById(R.id.logout_button);

        // Show the overlay and profile card
        profileCard.setAlpha(0f);
        profileCard.setVisibility(View.VISIBLE);
        profileCard.animate().alpha(1f).setDuration(300).start();

        blurOverlay.setAlpha(0f);
        blurOverlay.setVisibility(View.VISIBLE);
        blurOverlay.animate().alpha(1f).setDuration(300).start();

        //close profileCard
        closeBtn.setOnClickListener(v -> {
            profileCard.setVisibility(View.GONE);
            blurOverlay.setVisibility(View.GONE);
        });

        logoutBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(StudentActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    public void clearText(View view) {
        binding.username.setText("");
    }

    //getter and setter for username and standard of the student... this is being done to pass the username and standard to the TutorProfileDetail activity.
    public String username;
    public String standard;

    public void setUsername(String username) {
        this.username = username;
    }
    public void setStandard(String standard) {
        this.standard = standard;
    }
}