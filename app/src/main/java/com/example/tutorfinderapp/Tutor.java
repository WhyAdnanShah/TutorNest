package com.example.tutorfinderapp;

public class Tutor {
    public String username, gender,pin, rate, experience, standard, subject, email, phoneNumber;

    // Default constructor required for Firebase - check Ai for more info later
    public Tutor() {
    }

    public Tutor(String username, String gender, String pin, String rate, String experience, String standard, String subject, String email, String phoneNumber) {
        this.username = username;
        this.pin = pin;
        this.gender = gender;
        this.rate = rate;
        this.experience = experience;
        this.standard = standard;
        this.subject = subject;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }
    public String getGender() {
        return gender;
    }

    public String getRate() {
        return rate;
    }

    public String getExperience() {
        return experience;
    }

    public String getStandard() {
        return standard;
    }

    public String getSubject() {
        return subject;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
}
