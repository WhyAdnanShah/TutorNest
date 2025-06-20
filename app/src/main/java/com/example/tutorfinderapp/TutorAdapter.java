package com.example.tutorfinderapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.TutorViewHolder> {
    private List<Tutor> tutorList;
    Context context;

    public TutorAdapter(Context context, List<Tutor> tutorList) {
        this.tutorList = tutorList;
        this.context  = context;
    }

    @NonNull
    @Override
    public TutorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutor_item, parent, false);
        return new TutorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorViewHolder holder, int position) {
        Tutor tutor = tutorList.get(position);
        holder.name.setText(tutor.getUsername());
        holder.subject.setText(tutor.getSubject());
        holder.rate.setText(tutor.getRate());
        if (tutor.getGender() != null) {
            switch (tutor.getGender().toLowerCase()) {
                case "male":
                    holder.profileImage.setImageResource(R.drawable.tutor_male_64);
                    break;
                case "female":
                    holder.profileImage.setImageResource(R.drawable.tutor_female_64);
                    break;
                default:
                    holder.profileImage.setImageResource(R.drawable.tutor_male_female_small); // fallback
            }
        } else {
            holder.profileImage.setImageResource(R.drawable.tutor_male_female_small); // fallback
        }

        //click listener for the item in recycler view
        holder.itemView.setOnClickListener(v -> {
            SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isStudentLoggedIn", false);

            if (isLoggedIn) {
                Intent intent = new Intent(context, TutorProfileDetail.class);
                intent.putExtra("username", tutor.getUsername());
                intent.putExtra("subject", tutor.getSubject());
                intent.putExtra("gender", tutor.getGender());
                intent.putExtra("experience", tutor.getExperience());
                intent.putExtra("rate", tutor.getRate());
                intent.putExtra("email", tutor.getEmail());
                Log.d("EMAIL", "Email detected");
                intent.putExtra("phoneNumber", tutor.getPhoneNumber());
                Log.d("phone number", "Phone number detected" + tutor.getPhoneNumber());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Please Login to view Tutor details", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return tutorList.size();
    }

    public static class TutorViewHolder extends RecyclerView.ViewHolder {
        TextView name, subject, rate;
        ImageView profileImage;

        public TutorViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.username_text);
            subject = itemView.findViewById(R.id.subject_text);
            rate = itemView.findViewById(R.id.rate_text);
            profileImage = itemView.findViewById(R.id.imageView4);

        }
    }
}
