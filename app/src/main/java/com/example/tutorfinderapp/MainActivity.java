package com.example.tutorfinderapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.tutorfinderapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Binding the layout
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setTutor(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // External article links
        CardView cardView4 = findViewById(R.id.cardView4);
        CardView cardView5 = findViewById(R.id.cardView5);
        CardView cardView6 = findViewById(R.id.cardView6);

        cardView4.setOnClickListener(v -> {
            Intent study_habits = new Intent(Intent.ACTION_VIEW);
            study_habits.setData(Uri.parse("https://medium.com/@khanmahad974/10-study-habits-for-academic-excellence-8f0be2c54e87"));
            startActivity(study_habits);
        });

        cardView5.setOnClickListener(v -> {
            Intent entrance_exams = new Intent(Intent.ACTION_VIEW);
            entrance_exams.setData(Uri.parse("https://medium.com/@jaskirat2212/how-to-prepare-for-entrance-exams-86f12e4b3c7d"));
            startActivity(entrance_exams);
        });

        cardView6.setOnClickListener(v -> {
            Intent online_learning = new Intent(Intent.ACTION_VIEW);
            online_learning.setData(Uri.parse("https://medium.com/@shaista24pak/the-future-of-online-learning-77dbebcca2d7"));
            startActivity(online_learning);
        });


    }

    public void onTutorClick(View view) {
        SharedPreferences prefs = getSharedPreferences("TutorPref", MODE_PRIVATE);
        boolean isTutorLoggedIn = prefs.getBoolean("isTutorLoggedIn", false);

        if (isTutorLoggedIn) {
            //Getting Information Straight from the FireBase
            String username = prefs.getString("username", null);
            String experience = prefs.getString("experience", null);
            String rate = prefs.getString("rate", null);
            String standard = prefs.getString("standard", null);
            String subject = prefs.getString("subject", null);
            String email = prefs.getString("email", null);
            String phoneNumber = prefs.getString("phoneNumber", null);

            //Passing the Information to the TutorActivity
            Intent intent = new Intent(this, TutorActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("experience", experience);
            intent.putExtra("rate", rate);
            intent.putExtra("standard", standard);
            intent.putExtra("subject", subject);
            intent.putExtra("email", email);
            intent.putExtra("phoneNumber", phoneNumber);
            startActivity(intent);
        } else {
            startActivity(new Intent(this, TutorLogin.class));
        }
    }

    public void onStudClick(View view) {
        // Check login status
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isStudentLoggedIn = sharedPreferences.getBoolean("isStudentLoggedIn", false);

        if (isStudentLoggedIn) {
            // Go to StudentLogin directly
            startActivity(new Intent(MainActivity.this, StudentActivity.class));
        } else {
            // Go to Student_without_login
            startActivity(new Intent(MainActivity.this, Student_without_login.class));
        }
    }


    public void quote_of_the_day(View view) {
        Intent quoteIntent = new Intent(Intent.ACTION_VIEW);
        quoteIntent.setData(Uri.parse("https://www.inspiringquotes.com/"));
        startActivity(quoteIntent);
    }

    public void inspirationalQuotes_seeMore(View view) {
        Intent inspirationalQuotes = new Intent(Intent.ACTION_VIEW);
        inspirationalQuotes.setData(Uri.parse("https://www.forbes.com/sites/kevinkruse/2013/05/28/inspirational-quotes/"));
        startActivity(inspirationalQuotes);
    }

    //going to settings
    public void settingsClicked(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    //When Going Back
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit TutorNest?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finishAffinity();
                })
                .setNegativeButton("No", null)
                .show();
    }

}
