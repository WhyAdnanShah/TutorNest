package com.example.tutorfinderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.tutorfinderapp.databinding.TutorLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TutorLogin extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private TutorLoginBinding tutorLoginBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Bind layout
        tutorLoginBinding = DataBindingUtil.setContentView(this, R.layout.tutor_login);
        tutorLoginBinding.setTutorLogin(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_tutor_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Firebase setup
        databaseReference = FirebaseDatabase.getInstance().getReference("Tutors");

        // Login Button
        tutorLoginBinding.loginBtnTutor.setOnClickListener(v -> {
            String username = tutorLoginBinding.username.getText().toString().trim();
            String pin = tutorLoginBinding.pin.getText().toString().trim();

            if (username.isEmpty() || pin.isEmpty()) {
                Toast.makeText(TutorLogin.this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && pin.equals(snapshot.child("pin").getValue(String.class))) {
                        String experience = snapshot.child("experience").getValue(String.class);
                        String rate = snapshot.child("rate").getValue(String.class);
                        String standard = snapshot.child("standard").getValue(String.class);
                        String subject = snapshot.child("subject").getValue(String.class);
                        String email  = snapshot.child("email").getValue(String.class);

                        // Save login session
                        SharedPreferences prefs = getSharedPreferences("TutorPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isTutorLoggedIn", true);
                        editor.putString("username", username);
                        editor.putString("experience", experience);
                        editor.putString("rate", rate);
                        editor.putString("standard", standard);
                        editor.putString("subject", subject);
                        editor.putString("email", email);
                        editor.apply();

                        // Go to tutor Activity
                        Intent intent = new Intent(TutorLogin.this, TutorActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("experience", experience);
                        intent.putExtra("rate", rate);
                        intent.putExtra("standard", standard);
                        intent.putExtra("subject", subject);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(TutorLogin.this, "Invalid username or PIN!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(TutorLogin.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Sign up text click
    public void sign_up_text(View view) {
        startActivity(new Intent(this, TutorSignup.class));
        finish();
    }
}
