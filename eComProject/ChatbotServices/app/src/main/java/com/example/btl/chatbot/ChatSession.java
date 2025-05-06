package com.example.btl.chatbot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatSession {
    private int id;
    private String title;
    private long date;

    public ChatSession() {
    }

    public ChatSession(int id, String title, long date) {
        this.id = id;
        this.title = title;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(date));
    }
}