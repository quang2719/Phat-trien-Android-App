package com.example.btl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.chatbot.ChatSession;

import java.util.List;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder> {

    private List<ChatSession> sessions;
    private OnSessionClickListener listener;

    public interface OnSessionClickListener {
        void onSessionClick(ChatSession session);
        void onDeleteClick(ChatSession session, int position);
    }

    public ChatHistoryAdapter(List<ChatSession> sessions, OnSessionClickListener listener) {
        this.sessions = sessions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatSession session = sessions.get(position);
        holder.titleTextView.setText(session.getTitle());
        holder.dateTextView.setText(session.getFormattedDate());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSessionClick(session);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(session, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }
    
    public void removeItem(int position) {
        sessions.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.sessionTitle);
            dateTextView = itemView.findViewById(R.id.sessionDate);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
