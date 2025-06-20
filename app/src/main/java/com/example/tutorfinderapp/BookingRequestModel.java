package com.example.tutorfinderapp;

public class BookingRequestModel {
    private String studentUsername;
    private String studentStandard;
    private String tutorUsername;
    private String message;
    private String requestId;
    private String status;


    // Empty Constructor is Required for Firebase
    public BookingRequestModel() {}

    public BookingRequestModel(String studentUsername, String studentStandard, String tutorUsername, String message, String requestId, String status) {
        this.studentUsername = studentUsername;
        this.studentStandard = studentStandard;
        this.tutorUsername = tutorUsername;
        this.message = message;
        this.requestId = requestId;
        this.status = status;
    }

    // Getters
    public String getStudentUsername() { return studentUsername; }
    public String getStudentStandard() { return studentStandard; }
    public String getTutorUsername() { return tutorUsername; }
    public String getMessage() { return message; }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


}
