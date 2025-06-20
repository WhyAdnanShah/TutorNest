package com.example.tutorfinderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.tutorfinderapp.databinding.TutorProfileDetailBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TutorProfileDetail extends AppCompatActivity {
    TutorProfileDetailBinding binding;

    TextView tutor_username_detail, tutor_subject_detail, tutor_experience_detail, tutor_rate_detail;
    ImageView tutor_profile_image_detail;
    Button send_booking, cancel_booking;
    CardView card_send_request, btn_send_request;
    TextInputLayout booking_msg_layout;

    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Binding
        binding = DataBindingUtil.setContentView(this, R.layout.tutor_profile_detail);
        binding.setTutorProfileDetail(this);
        Log.d("Binding", "BINDING");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Finding Ids
        tutor_username_detail = findViewById(R.id.tutor_username_detail);
        tutor_subject_detail = findViewById(R.id.tutor_subject_detail);
        tutor_experience_detail = findViewById(R.id.tutor_experience_detail);
        tutor_profile_image_detail = findViewById(R.id.tutor_profile_image_detail);
        tutor_rate_detail = findViewById(R.id.tutor_rate_detail);

        // Getting all intent information
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String subject = intent.getStringExtra("subject");
        String gender = intent.getStringExtra("gender");
        String experience = intent.getStringExtra("experience");
        String rate = intent.getStringExtra("rate");
        String email = intent.getStringExtra("email");
        String phoneNumber = intent.getStringExtra("phoneNumber");
        Log.d("Phone Number", "PHONENUMBER");

        // Setting textView
        tutor_username_detail.setText(username);
        tutor_subject_detail.setText(subject);
        tutor_experience_detail.setText(experience);
        tutor_rate_detail.setText(rate);

        // Setting profileImage of the Tutor based on gender
        if ("Male".equals(gender)) {
            tutor_profile_image_detail.setImageResource(R.drawable.tutor_male_100);
        } else if ("Female".equals(gender)) {
            tutor_profile_image_detail.setImageResource(R.drawable.tutor_female_100);
        } else {
            tutor_profile_image_detail.setImageResource(R.drawable.tutor_male_female);
        }

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String studentUsername = prefs.getString("username", "unknown");
        String studentStandard = prefs.getString("standard", "unknown");

        // Sending Request
        btn_send_request = findViewById(R.id.btn_send_request);
        card_send_request = findViewById(R.id.card_send_request);
        send_booking = findViewById(R.id.btn_send_booking);
        cancel_booking = findViewById(R.id.btn_cancel_booking);
        // Message
        booking_msg_layout = findViewById(R.id.booking_msg);
        EditText booking_msg = booking_msg_layout.getEditText();

        // Show card
        btn_send_request.setOnClickListener(v -> {
            binding.blurOverlay.setAlpha(0f);
            binding.blurOverlay.setVisibility(View.VISIBLE);
            binding.blurOverlay.animate().alpha(1f).setDuration(300).start();

            card_send_request.setAlpha(0f);
            card_send_request.setVisibility(View.VISIBLE);
            card_send_request.animate().alpha(1f).setDuration(300).start();
        });

        // Cancel card
        cancel_booking.setOnClickListener(v -> {
            binding.blurOverlay.setVisibility(View.GONE);
            card_send_request.setVisibility(CardView.GONE);
            booking_msg.setText("");
        });

        // Send booking
        send_booking.setOnClickListener(v -> {
            // Check if message is empty
            if (booking_msg.getText().toString().trim().isEmpty()) {
                booking_msg.setError("Message can't be empty");
                return;
            }
            String message = booking_msg.getText().toString().trim();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BookingRequests");
            String key = ref.push().getKey();

            HashMap<String, Object> request = new HashMap<>();
            request.put("studentUsername", studentUsername);
            request.put("studentStandard", studentStandard);
            request.put("tutorUsername", tutor_username_detail.getText().toString().trim());
            request.put("message", message);

            ref.child(key).setValue(request)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Request sent!", Toast.LENGTH_SHORT).show();
                        card_send_request.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    });

            binding.blurOverlay.setVisibility(View.GONE);
            card_send_request.setVisibility(CardView.GONE);
            booking_msg.setText("");
        });


        // Clicking on the ContactBtn
        binding.contactBtn.setOnClickListener(v -> {
            binding.blurOverlay.setAlpha(0f);
            binding.blurOverlay.setVisibility(View.VISIBLE);
            binding.blurOverlay.animate().alpha(1f).setDuration(300).start();

            binding.contactCard.setAlpha(0f);
            binding.contactCard.setVisibility(View.VISIBLE);
            binding.contactCard.animate().alpha(1f).setDuration(300).start();

            binding.tutorEmailDetail.setText("Email: " + email);
            binding.tutorPhoneNumberDetail.setText("Phone Number: " + phoneNumber);
        });

        binding.CardcloseBtn.setOnClickListener(v -> {
            binding.blurOverlay.setVisibility(View.GONE);
            binding.contactCard.setVisibility(View.GONE);
        });
    }
}
