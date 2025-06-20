package com.example.tutorfinderapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingRequestAdapter extends RecyclerView.Adapter<BookingRequestAdapter.ViewHolder> {

    Context context;
    private List<BookingRequestModel> requests;
    private OnItemClickListener listener;

    public BookingRequestAdapter(Context context, List<BookingRequestModel> requests) {
        this.context = context;
        this.requests = requests;
    }

    public BookingRequestAdapter(List<BookingRequestModel> requests) {
    }

    public interface OnItemClickListener {
        void onItemClick(BookingRequestModel request);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingRequestModel request = requests.get(position);
        holder.username.setText("Student: " + request.getStudentUsername());
        holder.standard.setText("Standard: " + request.getStudentStandard());
        holder.message.setText("Message: " + request.getMessage());
        String status = request.getStatus();
        Log.d("BookingRequestAdapter", "Status: " + status);


        if (status != null) {
            holder.status.setText(status.substring(0, 1).toUpperCase() + status.substring(1));

            if ("accepted".equalsIgnoreCase(status)) {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.medium_sea_green));
            } else if ("pending".equalsIgnoreCase(status)) {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.orange));
            } else if ("denied".equalsIgnoreCase(status)) {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.indian_red));
            }

        } else {
            Log.e("BookingRequestAdapter", "Status is null for request: " + request.getRequestId());
            holder.status.setText("Pending"); // fallback
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.orange));
        }


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(requests.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, standard, message, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.student_username);
            standard = itemView.findViewById(R.id.student_standard);
            message = itemView.findViewById(R.id.booking_message);
            status = itemView.findViewById(R.id.status);
        }
    }
}


