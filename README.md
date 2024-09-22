1. Clone the repository:
   ```bash
   git clone https://github.com/thaiDev1997/distributed-transaction.git
   ```
2. Change directory:
   ```bash
   cd distributed-transaction
   ```
3. Run application by Docker Compose:
   ```bash
   docker-compose -f infras-compose.yml -f docker-compose.yml up --build
   ```
4. Place order
    ```bash
    POST http://localhost:8081/api/orders
    Content-Type: application/json
    {
        "userId": 1,
        "productId": 1,
        "quantity": 1,
        "paymentMode": "CREDIT_CARD"
    }
   ```
5. Delivery simulation
   </br>5.1) Start delivering
      ```bash
      PUT http://localhost:8084/api/orders/1/delivery-status
      Content-Type: application/json
      {
          "status": "DELIVERING",
          "timestamp": "2024-09-21T14:30:00Z"
      }
      ```
   </br>5.2) Successfully delivered
      ```bash
      PUT http://localhost:8084/api/orders/1/delivery-status
      Content-Type: application/json
      {
          "status": "DELIVERED",
          "timestamp": "2024-09-21T14:30:00Z"
      }
      ```
   </br>5.2) or delivery cancelled
      ```bash
      PUT http://localhost:8084/api/orders/1/delivery-status
      Content-Type: application/json
      {
          "status": "CANCELLED",
          "timestamp": "2024-09-21T14:30:00Z"
      }
      ```