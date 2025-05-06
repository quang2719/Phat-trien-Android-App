package com.example.btl.chatbot;

import android.content.Context;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.btl.utils.NetworkUtils;

public class ChatbotEngine {
    private Map<String, String[]> responses;
    private Random random;
    private ChatbotApiClient apiClient;
    private boolean useLocalResponses = false; // Set to false để sử dụng API
    private Context context;

    public ChatbotEngine(Context context) {
        this.context = context;
        random = new Random();
        initializeResponses();
        apiClient = new ChatbotApiClient();
    }

    private void initializeResponses() {
        responses = new HashMap<>();
        
        // Các câu chào
        responses.put("hello", new String[]{
            "Xin chào! Tôi là Omen, tôi có thể giúp gì cho bạn?",
            "Chào bạn! Rất vui được gặp bạn.",
            "Xin chào, tôi có thể giúp gì cho bạn hôm nay?"
        });
    }

    public interface ResponseCallback {
        void onResponseGenerated(String response);
    }

    public void generateResponse(String userMessage, final ResponseCallback callback) {
        generateResponse(userMessage, null, callback);
    }

    public void generateResponse(String userMessage, List<ChatMessage> chatHistory, final ResponseCallback callback) {
        if (useLocalResponses || !NetworkUtils.isNetworkAvailable(context)) {
            // Sử dụng phản hồi cục bộ nếu không có kết nối internet
            String response = generateLocalResponse(userMessage);
            if (!useLocalResponses && !NetworkUtils.isNetworkAvailable(context)) {
                response = "Không có kết nối internet. " + response;
            }
            callback.onResponseGenerated(response);
        } else {
            // Sử dụng API bên ngoài
            if (chatHistory != null && !chatHistory.isEmpty()) {
                // Gửi lịch sử chat nếu có
                apiClient.sendMessageWithHistory(chatHistory, new ChatbotApiClient.ChatbotApiCallback() {
                    @Override
                    public void onResponse(String response) {
                        callback.onResponseGenerated(response);
                    }

                    @Override
                    public void onError(String error) {
                        // Fallback to local response if API fails
                        String response = generateLocalResponse(userMessage);
                        callback.onResponseGenerated("API Error: " + error + ". Fallback: " + response);
                    }
                });
            } else {
                // Gửi tin nhắn đơn nếu không có lịch sử
                apiClient.sendMessage(userMessage, new ChatbotApiClient.ChatbotApiCallback() {
                    @Override
                    public void onResponse(String response) {
                        callback.onResponseGenerated(response);
                    }

                    @Override
                    public void onError(String error) {
                        // Fallback to local response if API fails
                        String response = generateLocalResponse(userMessage);
                        callback.onResponseGenerated("API Error: " + error + ". Fallback: " + response);
                    }
                });
            }
        }
    }

    private String generateLocalResponse(String userMessage) {
        userMessage = userMessage.toLowerCase().trim();
        // Kiểm tra từ khóa trong tin nhắn của người dùng
        return getRandomResponse("hello");
    }

    private String getRandomResponse(String key) {
        String[] possibleResponses = responses.get(key);
        int index = random.nextInt(possibleResponses.length);
        return possibleResponses[index];
    }
}







