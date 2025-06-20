package com.example.tutorfinderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

import com.example.tutorfinderapp.databinding.TutorActivityBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TutorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingRequestAdapter adapter;
    private List<BookingRequestModel> requestList;
    protected DatabaseReference databaseReference;
    private SharedPreferences prefs;
    TutorActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = DataBindingUtil.setContentView(this, R.layout.tutor_activity);
        binding.setTutorActivity(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.request_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestList = new ArrayList<>();
        adapter = new BookingRequestAdapter(TutorActivity.this, requestList);
        recyclerView.setAdapter(adapter);


        // Get logged-in tutor's username
        prefs = getSharedPreferences("TutorPref", MODE_PRIVATE);
        String tutorUsername = prefs.getString("username", "Unknown");
        String tutorSubject = prefs.getString("subject", "Unknown");
        String email = prefs.getString("email","Unknown");


        // Profile Button to open profileCard
        binding.tutorProfileBtn.setOnClickListener(v -> {
            binding.profileCard.setAlpha(0f);
            binding.profileCard.setVisibility(View.VISIBLE);
            binding.profileCard.animate().alpha(1f).setDuration(300).start();

            //Details in profileCard
            binding.nameOfTutor.setText("Name: "+ tutorUsername);
            binding.subjectOfTutor.setText("Subject: " + tutorSubject);
            binding.emailOfTutor.setText("Email: " + email);

            binding.blurOverlay.setAlpha(0f);
            binding.blurOverlay.setVisibility(View.VISIBLE);
            binding.blurOverlay.animate().alpha(1f).setDuration(300).start();
        });

        //Logout Button
        binding.TutorlogoutBtn.setOnClickListener(v -> {
            prefs = getSharedPreferences("TutorPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(TutorActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        //close profileCard
        binding.closeProfile.setOnClickListener(v -> {
            binding.profileCard.setVisibility(View.GONE);
            binding.blurOverlay.setVisibility(View.GONE);
        });


        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("BookingRequests");

        // Fetch data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookingRequestModel request = dataSnapshot.getValue(BookingRequestModel.class);

                    // Check and set the Firebase key (requestId)
                    if (request != null && tutorUsername.equals(request.getTutorUsername())) {
                        request.setRequestId(dataSnapshot.getKey());  // Set requestId here
                        requestList.add(request);
                    }
                }
                adapter.notifyDataSetChanged();  // Refresh UI
            }

            private boolean tutorUsernameIsValid(String tutorField, String loggedInTutor) {
                return tutorField != null && tutorField.equals(loggedInTutor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TutorActivity.this, "Failed to fetch requests", Toast.LENGTH_SHORT).show();
            }
        });



        //On Clicking a particular Student, the details would come up
        adapter.setOnItemClickListener(request -> {
            // Fill data
            binding.bookingStudentName.setText("Student: " + request.getStudentUsername());
            binding.bookingMessage.setText("Message: " + request.getMessage());


            // Show card
            binding.blurOverlay.setAlpha(0f);
            binding.blurOverlay.setVisibility(View.VISIBLE);
            binding.blurOverlay.animate().alpha(1f).setDuration(300).start();

            binding.requestDetailCard.setAlpha(0f);
            binding.requestDetailCard.setVisibility(View.VISIBLE);
            binding.requestDetailCard.animate().alpha(1f).setDuration(300).start();



            // Accept
            binding.btnAccept.setOnClickListener(v -> {
                binding.blurOverlay.setVisibility(View.GONE);
                binding.requestDetailCard.setVisibility(View.GONE);

                databaseReference = FirebaseDatabase.getInstance()
                        .getReference("BookingRequests")
                        .child(request.getRequestId());

                databaseReference.child("status").setValue("accepted").addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Request Accepted", Toast.LENGTH_SHORT).show();
                        adapter.getItemId(R.id.status);
                        adapter.notifyDataSetChanged();
                    }
                });
            });

            //Deny
            binding.btnDeny.setOnClickListener(v -> {
                binding.blurOverlay.setVisibility(View.GONE);
                binding.requestDetailCard.setVisibility(View.GONE);

                DatabaseReference ref = FirebaseDatabase.getInstance()
                        .getReference("BookingRequests")
                        .child(request.getRequestId());

                ref.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Request Denied", Toast.LENGTH_SHORT).show();
                        requestList.remove(request);
                        adapter.notifyDataSetChanged();
                    }
                });
            });

            //Close
            binding.closeDetailCard.setOnClickListener(v -> {
                binding.blurOverlay.setVisibility(View.GONE);
                binding.requestDetailCard.setVisibility(View.GONE);
            });

        });




    }
}