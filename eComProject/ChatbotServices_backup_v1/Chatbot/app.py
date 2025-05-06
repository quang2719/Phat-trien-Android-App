from flask import Flask, request, jsonify
import os
from google import generativeai as genai
from dotenv import load_dotenv

# Tải biến môi trường từ file .env
load_dotenv()

# Lấy API key từ biến môi trường
api_key = os.getenv("GOOGLE_API_KEY")
if not api_key:
    api_key = "YOUR_API_KEY_HERE"  # Thay thế bằng API key của bạn nếu không dùng .env

# Khởi tạo Gemini client
genai.configure(api_key=api_key)

app = Flask(__name__)

@app.route('/chat', methods=['POST'])
def chat():
    try:
        # Lấy tin nhắn từ request
        data = request.json
        user_message = data.get('message', '')
        
        if not user_message:
            return jsonify({"error": "No message provided"}), 400
        
        # Gọi Gemini API
        model = genai.GenerativeModel("gemini-2.5-flash-preview-04-17")
        chat = model.start_chat(history=[])
        response = chat.send_message(f"Trả lời bằng tiếng Việt, ngắn gọn: {user_message}")
        
        # Trả về kết quả
        return jsonify({"response": response.text})
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "ok"}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)

