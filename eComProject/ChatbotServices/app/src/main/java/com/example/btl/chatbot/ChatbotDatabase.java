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
    private static final String COLUMN_SESSION_ID = "session_id";
    
    // Thêm các hằng số cho bảng chat_sessions
    private static final String TABLE_SESSIONS = "chat_sessions";
    private static final String COLUMN_SESSION_ID = "session_id";
    private static final String COLUMN_SESSION_TITLE = "title";
    private static final String COLUMN_SESSION_DATE = "date";
    
    public ChatbotDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMessagesTable = "CREATE TABLE " + TABLE_MESSAGES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MESSAGE + " TEXT, "
                + COLUMN_SENDER + " TEXT, "
                + COLUMN_TIMESTAMP + " TEXT, "
                + COLUMN_SESSION_ID + " INTEGER)";
        
        String createSessionsTable = "CREATE TABLE " + TABLE_SESSIONS + " ("
                + COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SESSION_TITLE + " TEXT, "
                + COLUMN_SESSION_DATE + " TEXT)";
        
        db.execSQL(createMessagesTable);
        db.execSQL(createSessionsTable);
        
        // Tạo session mặc định
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION_TITLE, "Chat mới");
        values.put(COLUMN_SESSION_DATE, System.currentTimeMillis());
        db.insert(TABLE_SESSIONS, null, values);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        onCreate(db);
    }
    
    public void addMessage(String message, String sender) {
        addMessage(message, sender, getCurrentSessionId());
    }

    public void addMessage(String message, String sender, int sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_SENDER, sender);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_SESSION_ID, sessionId);
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    public List<ChatMessage> getAllMessages() {
        return getAllMessages(getCurrentSessionId());
    }

    public List<ChatMessage> getAllMessages(int sessionId) {
        List<ChatMessage> messageList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES
                + " WHERE " + COLUMN_SESSION_ID + " = " + sessionId
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
    
    // Thêm phương thức để lấy session hiện tại
    public int getCurrentSessionId() {
        SQLiteDatabase db = this.getReadableDatabase();
        int sessionId = 1; // Mặc định là 1
        
        Cursor cursor = db.rawQuery("SELECT MAX(" + COLUMN_SESSION_ID + ") FROM " + TABLE_SESSIONS, null);
        if (cursor.moveToFirst()) {
            sessionId = cursor.getInt(0);
        }
        cursor.close();
        
        return sessionId;
    }

    // Thêm phương thức để tạo session mới
    public int createNewSession(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION_TITLE, title);
        values.put(COLUMN_SESSION_DATE, System.currentTimeMillis());
        
        long id = db.insert(TABLE_SESSIONS, null, values);
        db.close();
        
        return (int) id;
    }

    // Thêm phương thức để lấy danh sách các session
    public List<ChatSession> getAllSessions() {
        List<ChatSession> sessionList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SESSIONS
                + " ORDER BY " + COLUMN_SESSION_DATE + " DESC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_SESSION_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_SESSION_TITLE);
                int dateIndex = cursor.getColumnIndex(COLUMN_SESSION_DATE);
                
                ChatSession session = new ChatSession();
                session.setId(cursor.getInt(idIndex));
                session.setTitle(cursor.getString(titleIndex));
                session.setDate(cursor.getLong(dateIndex));
                
                sessionList.add(session);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return sessionList;
    }

    // Thêm phương thức để cập nhật tiêu đề session
    public void updateSessionTitle(int sessionId, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SESSION_TITLE, title);
        
        db.update(TABLE_SESSIONS, values, COLUMN_SESSION_ID + " = ?", 
                  new String[]{String.valueOf(sessionId)});
        db.close();
    }

    // Thêm phương thức để xóa tất cả tin nhắn trong một session
    public void clearSessionMessages(int sessionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, COLUMN_SESSION_ID + " = ?", 
                  new String[]{String.valueOf(sessionId)});
        db.close();
    }
}





