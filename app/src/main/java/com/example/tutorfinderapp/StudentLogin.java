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

import com.example.tutorfinderapp.databinding.StudentLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentLogin extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StudentLoginBinding studentLoginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Bind layout
        studentLoginBinding = DataBindingUtil.setContentView(this, R.layout.student_login);
        studentLoginBinding.setStudentLoginData(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.student_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Students");

        // Login Button Click
        studentLoginBinding.loginBtnStudent.setOnClickListener(v -> {
            String username = studentLoginBinding.username.getText().toString().trim();
            String pin = studentLoginBinding.pin.getText().toString().trim();

            if (username.isEmpty() || pin.isEmpty()) {
                Toast.makeText(StudentLogin.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Auth check
            databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && pin.equals(snapshot.child("pin").getValue(String.class))) {
                        String standard = snapshot.child("standard").getValue(String.class);

                        // Save login session
                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isStudentLoggedIn", true);
                        editor.putString("username", username);
                        editor.putString("standard", standard);
                        editor.apply();

                        // Move to student dashboard
                        Intent intent = new Intent(StudentLogin.this, StudentActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("standard", standard);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(StudentLogin.this, "Invalid username or PIN!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(StudentLogin.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Click on "Sign up" text
    public void signUpToStud(View view) {
        startActivity(new Intent(this, StudentSignup.class));
        finish();
    }
}
