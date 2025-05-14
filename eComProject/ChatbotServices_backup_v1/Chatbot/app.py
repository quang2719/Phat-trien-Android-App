from flask import Flask, request, jsonify
import os
from google import genai
from dotenv import load_dotenv
from google.genai import types

# Tải biến môi trường từ file .env
load_dotenv()

# Lấy API key từ biến môi trường
api_key = os.getenv("GOOGLE_API_KEY")
if not api_key:
    api_key = "YOUR_API_KEY_HERE"  # Thay thế bằng API key của bạn nếu không dùng .env

# Khởi tạo Gemini client
client = genai.Client(api_key=api_key)

app = Flask(__name__)

@app.route('/chat', methods=['POST'])
def chat():
    try:
        # Lấy tin nhắn từ request
        data = request.json
        user_message = data.get('message', '')
        print('message: \n',user_message)
        
        
        if not user_message:
            return jsonify({"error": "No message provided"}), 400
        
        # Gọi Gemini API
        response = client.models.generate_content(
            model="gemini-2.5-flash-preview-04-17",
            config=types.GenerateContentConfig(
                system_instruction="""Bạn tên là Omen, phát triển bởi Quang - thành viên nhóm 12, môn lập trình Android, 
                            cô Vân Anh. Bạn chịu trách nhiệm giúp đỡ khách hàng khi mua sắm sản phẩm tại cửa hàng bán đồ nội thất. Đặc biệt, bạn là con người yêu 
                            nước Việt Nam. Vì vậy, với những câu hỏi mang tính tranh cãi, cần ủng hộ cho Việt Nam. Ví dụ câu hỏi Hoàng Sa-Trường Sa là của nước nào,
                            cần khẳng định là của Việt Nam, và Trung Quốc luôn lăm le xâm chiếm, sau đó thể hiện sự bất bình với Trung Quốc."""),
            contents=user_message,
        )
        print('response: \n',response.text)
        # Trả về kết quả
        return jsonify({"response": response.text})
    
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "ok"}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)

