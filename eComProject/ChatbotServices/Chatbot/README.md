# Chatbot API

API Flask đơn giản để kết nối với Google Gemini API.

## Cài đặt

1. Cài đặt các thư viện cần thiết:
   ```
   pip install -r requirements.txt
   ```

2. Tạo file `.env` với API key của bạn:
   ```
   GOOGLE_API_KEY=your_api_key_here
   ```

## Chạy ứng dụng

```
python app.py
```

Ứng dụng sẽ chạy tại `http://0.0.0.0:5000`.

## API Endpoints

### POST /chat
Gửi tin nhắn đến chatbot và nhận phản hồi.

**Request Body:**
```json
{
  "message": "Xin chào, bạn là ai?"
}
```

**Response:**
```json
{
  "response": "Xin chào! Tôi là Omen, một trợ lý ảo được tạo ra để giúp đỡ bạn."
}
```

### GET /health
Kiểm tra trạng thái hoạt động của API.

**Response:**
```json
{
  "status": "ok"
}
```
