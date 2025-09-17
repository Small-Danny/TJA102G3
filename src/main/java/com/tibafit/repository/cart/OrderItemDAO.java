package com.tibafit.repository.cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tibafit.model.cart.OrderItemVO;

//	OrderItemDAO 是 order_item 表的 Spring Data JPA 存取介面。除了繼承 JpaRepository 的通用 CRUD，還
//	提供 findByOrder_OrderId(orderId)，能用訂單主檔的 ID直接查出該訂單的所有明細，非常適合在後台或服
//	務層載入訂單時一併取回明細列表。

public interface OrderItemDAO extends JpaRepository<OrderItemVO, Integer> {

	// 依「訂單主鍵」查詢該訂單的所有明細
	// 語法說明：findByOrder_OrderId
	// - order 對應 OrderItemVO 裡的屬性名（ManyToOne OrdersVO）
	// - orderId 對應 OrdersVO 裡的主鍵欄位名
	List<OrderItemVO> findByOrder_OrderId(Integer orderId);
}
