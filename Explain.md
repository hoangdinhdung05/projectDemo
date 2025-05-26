//Luồng Cart
Frontend (User) 
    ⇩
[POST /api/carts] — Tạo giỏ hàng
    ⇩
[POST /api/cart-details] — Thêm sản phẩm
    ⇩
[GET /api/carts/user/{id}] — Lấy giỏ hàng
    ⇩
[GET /api/cart-details/cart/{id}] — Xem sản phẩm trong giỏ
    ⇩
[DELETE /api/cart-details/{id}] — Xóa sản phẩm khỏi giỏ
    ⇩
[DELETE /api/carts/{id}] — Xóa giỏ hàng

//Phát triển các tính năng sau: 


//Luồng Order

[User chọn sản phẩm] 
      ↓
[Thêm vào Cart (cart-details)]
      ↓
[POST /orders - gửi OrderRequest]
      ↓
[OrderService xử lý]
      ↓
→ Tạo Order
→ Tạo OrderDetails
→ Tính tổng tiền
→ Xóa CartDetail
      ↓
[Trả OrderResponse cho frontend]
