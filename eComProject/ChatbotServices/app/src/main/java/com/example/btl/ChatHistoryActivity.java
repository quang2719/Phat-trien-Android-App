package com.example.btl;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    private ChatHistoryAdapter adapter;

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
            
            adapter = new ChatHistoryAdapter(sessions, this);
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
    public void onDeleteClick(ChatSession session, int position) {
        new AlertDialog.Builder(this)
            .setTitle("Xóa đoạn chat")
            .setMessage("Bạn có chắc chắn muốn xóa đoạn chat này?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                // Xóa session và tất cả tin nhắn liên quan
                chatbotDatabase.deleteSession(session.getId());
                
                // Cập nhật UI
                adapter.removeItem(position);
                
                // Kiểm tra nếu không còn session nào
                if (adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    historyRecyclerView.setVisibility(View.GONE);
                }
                
                Toast.makeText(this, "Đã xóa đoạn chat", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Hủy", null)
            .show();
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



