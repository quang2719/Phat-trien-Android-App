package com.example.btl.chatbot;

import android.content.Context;
import java.util.HashMap;
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
        
        // Các câu hỏi về Omen
        responses.put("omen", new String[]{
            "Tôi là Omen, một nhân vật trong game Valorant.",
            "Omen là một đặc vụ bí ẩn với khả năng dịch chuyển và tạo màn sương.",
            "Tôi có thể dịch chuyển và tạo màn sương để hỗ trợ đồng đội."
        });
        
        // Các câu hỏi về game
        responses.put("game", new String[]{
            "Valorant là một game bắn súng chiến thuật 5v5 của Riot Games.",
            "Trong Valorant, bạn có thể chọn nhiều đặc vụ khác nhau với khả năng độc đáo.",
            "Bạn có thể chơi nhiều chế độ khác nhau như Unrated, Competitive, Spike Rush, và Deathmatch."
        });
        
        // Câu trả lời mặc định
        responses.put("default", new String[]{
            "Tôi không hiểu ý bạn. Bạn có thể nói rõ hơn được không?",
            "Xin lỗi, tôi không hiểu câu hỏi của bạn.",
            "Tôi đang học hỏi thêm. Bạn có thể hỏi điều khác không?"
        });
    }

    public interface ResponseCallback {
        void onResponseGenerated(String response);
    }

    public void generateResponse(String userMessage, final ResponseCallback callback) {
        if (useLocalResponses || !NetworkUtils.isNetworkAvailable(context)) {
            // Sử dụng phản hồi cục bộ nếu không có kết nối internet
            String response = generateLocalResponse(userMessage);
            if (!useLocalResponses && !NetworkUtils.isNetworkAvailable(context)) {
                response = "Không có kết nối internet. " + response;
            }
            callback.onResponseGenerated(response);
        } else {
            // Sử dụng API bên ngoài
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

    private String generateLocalResponse(String userMessage) {
        userMessage = userMessage.toLowerCase().trim();
        
        // Kiểm tra từ khóa trong tin nhắn của người dùng
        if (userMessage.contains("xin chào") || userMessage.contains("chào") || userMessage.contains("hello") || userMessage.contains("hi")) {
            return getRandomResponse("hello");
        } else if (userMessage.contains("omen") || userMessage.contains("bạn là ai")) {
            return getRandomResponse("omen");
        } else if (userMessage.contains("game") || userMessage.contains("valorant") || userMessage.contains("chơi")) {
            return getRandomResponse("game");
        } else {
            return getRandomResponse("default");
        }
    }

    private String getRandomResponse(String key) {
        String[] possibleResponses = responses.get(key);
        int index = random.nextInt(possibleResponses.length);
        return possibleResponses[index];
    }
}






