package com.example.tutorfinderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.tutorfinderapp.databinding.StudentSignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StudentSignup extends AppCompatActivity {

    private StudentSignupBinding binding;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Binding
        binding = DataBindingUtil.setContentView(this, R.layout.student_signup);
        binding.setLifecycleOwner(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Students");

        // Standard dropdown
        AutoCompleteTextView standardDropdown = binding.standardDropdown;
        List<String> standards = Arrays.asList("10th", "11th (CS)", "12th (CS)", "Btech CSE");
        ArrayAdapter<String> stdAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, standards);
        standardDropdown.setAdapter(stdAdapter);

        // Back press behavior
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(StudentSignup.this, MainActivity.class));
                finish();
            }
        });

        // Signup button
        binding.signupBtn.setOnClickListener(v -> {
            String username = binding.username.getText().toString().trim();
            String pin = binding.pin.getText().toString().trim();
            String standard = binding.standardDropdown.getText().toString().trim();

            if (username.isEmpty() || pin.isEmpty() || standard.isEmpty()) {
                Toast.makeText(StudentSignup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Check if username exists
                databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(StudentSignup.this, "Username already taken!", Toast.LENGTH_SHORT).show();
                        } else {
                            HashMap<String, Object> studentData = new HashMap<>();
                            studentData.put("username", username);
                            studentData.put("pin", pin);
                            studentData.put("standard", standard);


                            databaseReference.child(username).setValue(studentData)
                                    .addOnSuccessListener(unused -> Toast.makeText(StudentSignup.this, "Signup Successful!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(StudentSignup.this, "Signup Failed!", Toast.LENGTH_SHORT).show());

                            //Shared Preferences
                            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("isStudentLoggedIn", true);
                            editor.putString("username", username);
                            editor.putString("standard", standard);
                            editor.apply();

                            Intent intent = new Intent(StudentSignup.this, StudentActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("standard", standard);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StudentSignup.this, "Database error!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
