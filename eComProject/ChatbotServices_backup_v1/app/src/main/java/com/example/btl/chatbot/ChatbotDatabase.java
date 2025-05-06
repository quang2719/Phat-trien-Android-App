package com.example.btl.chatbot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ChatbotDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chatbot.db";
    private static final int DATABASE_VERSION = 1;
    
    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_SENDER = "sender";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    
    public ChatbotDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_MESSAGES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MESSAGE + " TEXT, "
                + COLUMN_SENDER + " TEXT, "
                + COLUMN_TIMESTAMP + " TEXT)";
        db.execSQL(createTableQuery);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }
    
    public void addMessage(String message, String sender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_SENDER, sender);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    public List<ChatMessage> getAllMessages() {
        List<ChatMessage> messageList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES
                + " ORDER BY " + COLUMN_TIMESTAMP + " ASC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int messageIndex = cursor.getColumnIndex(COLUMN_MESSAGE);
                int senderIndex = cursor.getColumnIndex(COLUMN_SENDER);
                int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);
                
                ChatMessage message = new ChatMessage();
                message.setId(cursor.getInt(idIndex));
                message.setMessage(cursor.getString(messageIndex));
                message.setSender(cursor.getString(senderIndex));
                message.setTimestamp(String.valueOf(cursor.getLong(timestampIndex)));
                
                messageList.add(message);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return messageList;
    }
    
    public void clearAllMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, null, null);
        db.close();
    }
}



