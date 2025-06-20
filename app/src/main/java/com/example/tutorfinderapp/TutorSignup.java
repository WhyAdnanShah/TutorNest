package com.example.tutorfinderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.tutorfinderapp.databinding.TutorSignupBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class TutorSignup extends AppCompatActivity {
    private TutorSignupBinding tutorSignupBinding;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding
        tutorSignupBinding = DataBindingUtil.setContentView(this, R.layout.tutor_signup);
        tutorSignupBinding.setTutorSignup(this);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Necessary Ids
        RadioGroup genderRadioGroup = findViewById(R.id.gender_radio_group);
        ImageView imageView = findViewById(R.id.imageView3);



        //firebase ref
        databaseReference = FirebaseDatabase.getInstance().getReference("Tutors");


        //Standard Dropdown
        AutoCompleteTextView standard_dropdown = findViewById(R.id.standard_dropdown);
        //Creating a list of Satndards
        List<String> standards = Arrays.asList("10th", "11th (CS)", "12th (CS)", "Btech CSE");
        //Adapter for Standardsss
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, standards);
        standard_dropdown.setAdapter(adapter);

        //Subjecctts Dropdownn
        AutoCompleteTextView subjects_dropdown = findViewById(R.id.subjects_dropdown);
        //Creating a list of Subjects
        standard_dropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStandard = standards.get(position);
            List<String> subjects;

            if (selectedStandard.equals("10th")) {
                subjects = Arrays.asList("Maths", "Biology", "Chemistry", "Physics", "Computer Science", "History", "Geography", "Economics", "Pol. Science");
            } else if (selectedStandard.equals("11th (CS)")) {
                subjects = Arrays.asList("Physics", "Chemistry", "Mathematics", "Computer Science");
            } else if (selectedStandard.equals("12th (CS)")) {
                subjects = Arrays.asList("Physics", "Chemistry", "Mathematics", "Computer Science", "Biology");
            } else { // BTech CSE
                subjects = Arrays.asList("DBMS", "OS", "DSA", "AI", "Machine Learning", "Networking");
            }

            // Update subject dropdown
            ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
            subjects_dropdown.setAdapter(subjectAdapter);
            subjects_dropdown.setText("", false);

        });


        //SignUp btn Clicked like a chad
        tutorSignupBinding.signupBtn.setOnClickListener(v -> {
            String username = tutorSignupBinding.username.getText().toString();
            int selectedId = genderRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = findViewById(selectedId);
            String gender = selectedRadioButton.getText().toString().trim();
            String pin = tutorSignupBinding.pin.getText().toString();
            String rate = tutorSignupBinding.rate.getText().toString();
            String experience = tutorSignupBinding.exp.getText().toString();
            String selectedStandard = standard_dropdown.getText().toString();
            String selectedSubject = subjects_dropdown.getText().toString();
            String email = tutorSignupBinding.Email.getText().toString();
            String phoneNumber = tutorSignupBinding.phoneNumber.getText().toString();

            if (username.isEmpty() || gender.isEmpty() ||pin.isEmpty() || rate.isEmpty() || experience.isEmpty() || selectedStandard.isEmpty() || selectedSubject.isEmpty() || email.isEmpty()){
                Toast.makeText(TutorSignup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();

            } else {
                // Check if username already exists
                databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(TutorSignup.this, "Username already taken!", Toast.LENGTH_SHORT).show();
                        }else {
                            //Store data to firebase
                            HashMap<String, Object> tutorData = new HashMap<>();
                            tutorData.put("username", username);
                            tutorData.put("gender", gender);
                            tutorData.put("pin", pin);
                            tutorData.put("experience", experience);
                            tutorData.put("rate", rate);
                            tutorData.put("standard", selectedStandard);
                            tutorData.put("subject", selectedSubject);
                            tutorData.put("email", email);
                            tutorData.put("phoneNumber", phoneNumber);

                            databaseReference.child(username).setValue(tutorData)
                                    .addOnSuccessListener(unused -> Toast.makeText(TutorSignup.this, "Signup Successful!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(TutorSignup.this, "Signup Failed!", Toast.LENGTH_SHORT).show());


                            // ✅ Save login session
                            SharedPreferences prefs = getSharedPreferences("TutorPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("isTutorLoggedIn", true);
                            editor.putString("username", username);
                            editor.putString("gender", gender);
                            editor.putString("experience", experience);
                            editor.putString("rate", rate);
                            editor.putString("standard", selectedStandard);
                            editor.putString("subject", selectedSubject);
                            editor.putString("email", email);
                            editor.putString("phoneNumber", phoneNumber);
                            editor.apply();

                            //open TutorActivity.class
                            Intent intent = new Intent(TutorSignup.this, TutorActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("gender", gender);
                            intent.putExtra("experience", experience);
                            intent.putExtra("rate", rate);
                            intent.putExtra("standard", selectedStandard);
                            intent.putExtra("subject", selectedSubject);
                            intent.putExtra("email", email);
                            intent.putExtra("phoneNumber", phoneNumber);
                            startActivity(intent);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TutorSignup.this, "Database Error!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        //Change image based on gender on top of the screen
        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int imageResId;

            if (checkedId == R.id.radio_male) {
                imageResId = R.drawable.tutor_male_100;
            } else if (checkedId == R.id.radio_female) {
                imageResId = R.drawable.tutor_female_100;
            } else {
                imageResId = R.drawable.tutor_male_female;
            }

            // Fade out current image
            imageView.animate()
                    .translationX(-200f)
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        imageView.setImageResource(imageResId);
                        imageView.setTranslationX(100f);

                        imageView.animate()
                                .translationX(0f)
                                .alpha(1f)
                                .setDuration(300)
                                .start();
                    })
                    .start();

        });

    }
}

