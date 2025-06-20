package com.example.tutorfinderapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.example.tutorfinderapp.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        //binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setSettings(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Shared Preferences for Dark Mode / Light Mode
        SharedPreferences prefs = getSharedPreferences("DarkModePref", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("isDarkMode", true);

        binding.darkModeSwitch.setChecked(isDarkMode);

        //Switch for Dark Mode / Light Mode
        binding.darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);;
                Toast.makeText(this, "Light Mode", Toast.LENGTH_SHORT).show();
            }

            // Save preference
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isDarkMode", isChecked);
            editor.apply();
        });


        binding.contactMe.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mohdadnan23742@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding TutorNest App");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello Adnan,\n\n");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send email using..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show();
            }
        });


        binding.reportProblem.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mohdadnan23742@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Crash Report for TutorNest App");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello Adnan,\n\n");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send email using..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.GithubPage.setOnClickListener(v -> {
            Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/WhyAdnanShah/TutorNest"));
            startActivity(githubIntent);
        });

        binding.LinkedInPage.setOnClickListener(v -> {
            Intent linkedinIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/whyadnan/"));
            startActivity(linkedinIntent);
        });


    }

    public void homeClicked(View view ){
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit TutorNest?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finishAffinity(); // closes all activities
                })
                .setNegativeButton("No", null)
                .show();
    }

}
