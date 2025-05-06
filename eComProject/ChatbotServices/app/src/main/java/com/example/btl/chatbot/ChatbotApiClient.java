package com.example.btl.chatbot;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.json.JSONObject;

public class ChatbotApiClient {
    // Thay đổi URL này thành địa chỉ IP của máy tính chạy Flask
    // Ví dụ: "http://192.168.1.100:5000/chat"
    private static final String API_URL = "http://10.0.2.2:5000/chat"; // 10.0.2.2 là địa chỉ localhost của máy host từ Android Emulator
    
    public interface ChatbotApiCallback {
        void onResponse(String response);
        void onError(String error);
    }
    
    public void sendMessage(String message, ChatbotApiCallback callback) {
        new ChatbotApiTask(callback).execute(message);
    }
    
    public void sendMessageWithHistory(List<ChatMessage> chatHistory, ChatbotApiCallback callback) {
        new ChatbotApiTaskWithHistory(callback).execute(chatHistory);
    }
    
    private static class ChatbotApiTask extends AsyncTask<String, Void, String> {
        private ChatbotApiCallback callback;
        private String errorMessage;
        
        public ChatbotApiTask(ChatbotApiCallback callback) {
            this.callback = callback;
        }
        
        @Override
        protected String doInBackground(String... params) {
            String message = params[0];
            String response = null;
            
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(15000); // 15 giây timeout
                connection.setReadTimeout(15000);
                connection.setDoOutput(true);
                
                // Tạo JSON request
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("message", message);
                
                // Gửi request
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                
                // Đọc response
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder responseBuilder = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            responseBuilder.append(responseLine.trim());
                        }
                        response = responseBuilder.toString();
                        
                        // Parse JSON response
                        JSONObject jsonResponse = new JSONObject(response);
                        return jsonResponse.getString("response");
                    }
                } else {
                    errorMessage = "HTTP Error: " + responseCode;
                }
                
                connection.disconnect();
                
            } catch (Exception e) {
                errorMessage = "Error: " + e.getMessage();
                e.printStackTrace();
            }
            
            return response;
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                callback.onResponse(result);
            } else {
                callback.onError(errorMessage);
            }
        }
    }
    
    private static class ChatbotApiTaskWithHistory extends AsyncTask<List<ChatMessage>, Void, String> {
        private ChatbotApiCallback callback;
        private String errorMessage;
        
        public ChatbotApiTaskWithHistory(ChatbotApiCallback callback) {
            this.callback = callback;
        }
        
        @Override
        protected String doInBackground(List<ChatMessage>... params) {
            List<ChatMessage> chatHistory = params[0];
            String response = null;
            
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(15000); // 15 giây timeout
                connection.setReadTimeout(15000);
                connection.setDoOutput(true);
                
                // Tạo chuỗi lịch sử chat theo định dạng yêu cầu
                StringBuilder formattedHistory = new StringBuilder();
                for (ChatMessage message : chatHistory) {
                    String sender = "user".equals(message.getSender()) ? "User" : "Chatbot";
                    formattedHistory.append(sender).append(": ").append(message.getMessage()).append("\n");
                }
                
                // Tạo JSON request
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("message", formattedHistory.toString());
                
                // Gửi request
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonRequest.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                
                // Đọc response
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder responseBuilder = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            responseBuilder.append(responseLine.trim());
                        }
                        response = responseBuilder.toString();
                        
                        // Parse JSON response
                        JSONObject jsonResponse = new JSONObject(response);
                        return jsonResponse.getString("response");
                    }
                } else {
                    errorMessage = "HTTP Error: " + responseCode;
                }
                
                connection.disconnect();
                
            } catch (Exception e) {
                errorMessage = "Error: " + e.getMessage();
                e.printStackTrace();
            }
            
            return response;
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                callback.onResponse(result);
            } else {
                callback.onError(errorMessage);
            }
        }
    }
}



