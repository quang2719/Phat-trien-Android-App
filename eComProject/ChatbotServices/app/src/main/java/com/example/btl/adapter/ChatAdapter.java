package com.example.btl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btl.R;
import com.example.btl.chatbot.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    
    private List<ChatMessage> chatMessages;
    
    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }
    
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        // Ẩn tất cả các view trước
        holder.userMessageCard.setVisibility(View.GONE);
        holder.botMessageCard.setVisibility(View.GONE);
        holder.botAvatar.setVisibility(View.GONE);

        if ("user".equals(message.getSender())) {
            // Hiển thị tin nhắn người dùng
            holder.userMessageCard.setVisibility(View.VISIBLE);
            holder.userMessageText.setText(message.getMessage());
        } else {
            // Hiển thị tin nhắn bot
            holder.botMessageCard.setVisibility(View.VISIBLE);
            holder.botAvatar.setVisibility(View.VISIBLE);
            holder.botMessageText.setText(message.getMessage());
        }
    }
    
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        CardView userMessageCard;
        TextView userMessageText;
        CardView botMessageCard;
        TextView botMessageText;
        ImageView botAvatar;
        
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageCard = itemView.findViewById(R.id.userMessageCard);
            userMessageText = itemView.findViewById(R.id.userMessageText);
            botMessageCard = itemView.findViewById(R.id.botMessageCard);
            botMessageText = itemView.findViewById(R.id.botMessageText);
            botAvatar = itemView.findViewById(R.id.botAvatar);
        }
    }
}


