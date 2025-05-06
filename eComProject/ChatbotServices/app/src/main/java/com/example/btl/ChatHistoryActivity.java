package com.example.btl;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.adapter.ChatHistoryAdapter;
import com.example.btl.chatbot.ChatSession;
import com.example.btl.chatbot.ChatbotDatabase;

import java.util.List;

public class ChatHistoryActivity extends AppCompatActivity implements ChatHistoryAdapter.OnSessionClickListener {

    private RecyclerView historyRecyclerView;
    private TextView emptyView;
    private ChatbotDatabase chatbotDatabase;
    private List<ChatSession> sessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Khởi tạo views
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        emptyView = findViewById(R.id.emptyView);

        // Khởi tạo database
        chatbotDatabase = new ChatbotDatabase(this);

        // Lấy danh sách session
        loadSessions();
    }

    private void loadSessions() {
        sessions = chatbotDatabase.getAllSessions();
        
        if (sessions.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            historyRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            historyRecyclerView.setVisibility(View.VISIBLE);
            
            ChatHistoryAdapter adapter = new ChatHistoryAdapter(sessions, this);
            historyRecyclerView.setAdapter(adapter);
            historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    public void onSessionClick(ChatSession session) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("session_id", session.getId());
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatbotDatabase != null) {
            chatbotDatabase.close();
        }
    }
}