// src/main/java/com/tibafit/service/cart/OrdersService.java
package com.tibafit.service.cart;

import com.tibafit.dto.cart.OrdersDTO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.repository.cart.OrdersDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersService {

	private final OrdersDAO ordersDAO;

	@Autowired
	public OrdersService(OrdersDAO ordersDAO) {
		this.ordersDAO = ordersDAO;
	}

	/**
	 * 【修正後的查詢方法】 根據使用者 ID 查詢其所有訂單，並轉換為 DTO 列表。
	 * 
	 * @param userId 使用者 ID
	 * @return 該使用者的訂單 DTO 列表
	 */
	@Transactional(readOnly = true)
	public List<OrdersDTO> findOrdersByUserId(Integer userId) {
		// 1. The DAO now directly returns a List<OrdersVO>, so no Page object is
		// involved.
		List<OrdersVO> userOrders = ordersDAO.findByUserIdOrderByOrderDateAsc(userId);

		// 2. The rest of the logic remains the same.
		return userOrders.stream().map(OrdersDTO::from).collect(Collectors.toList());
	}

	// =================================================================
	// 以下保留您原本 OrdersService 中的後台管理功能
	// =================================================================

	/** 以 ID 讀取單筆訂單；不存在則丟出業務例外 */
	@Transactional(readOnly = true)
	public OrdersVO get(Integer id) {
		return ordersDAO.findById(id).orElseThrow(() -> new IllegalStateException("找不到訂單 ID " + id));
	}

	/**
	 * 分頁查詢訂單 (後台用) - 若帶 userId：回傳該使用者的訂單（時間新→舊） - 否則：回全體訂單（同樣新→舊）
	 */
	public Page<OrdersVO> list(Integer userId, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1),
				Sort.by(Sort.Direction.ASC, "orderId"));

		if (userId != null) {
			return ordersDAO.findByUserIdOrderByOrderDateAsc(userId, pageable);
		}
		return ordersDAO.findAll(pageable);
	}

	/**
	 * 局部更新收件資訊（非 null 才覆寫）
	 */
	@Transactional
	public OrdersVO updateRecipient(Integer id, String name, String phone, String address) {
		OrdersVO o = get(id);
		if (name != null)
			o.setRecipientName(name);
		if (phone != null)
			o.setRecipientPhone(phone);
		if (address != null)
			o.setRecipientAddress(address);
		return o;
	}

	/**
	 * 更新訂單狀態 / 付款狀態
	 */
	@Transactional
	public OrdersDTO updateOrderStatus(Integer id, Integer orderStatus) {
		OrdersVO o = get(id); // 還在同一個 Hibernate Session
		if (orderStatus != null)
			o.setOrderStatus(orderStatus);
		return OrdersDTO.from(o); // 這裡轉 DTO，不會 Lazy 初始化失敗
	}

	@Transactional
	public OrdersDTO updateStatus(Integer id, Integer orderStatus, Integer paymentStatus) {
		OrdersVO o = get(id);
		if (orderStatus != null)
			o.setOrderStatus(orderStatus);
		if (paymentStatus != null) {
			o.setPaymentStatus(paymentStatus);
			if (paymentStatus == 1) {
				o.setPaymentTime(LocalDateTime.now());
			}
		}
		return OrdersDTO.from(o); // 同理，這裡就完成轉 DTO
	}

	/** 刪除訂單 */
	@Transactional
	public void delete(Integer id) {
		if (!ordersDAO.existsById(id)) {
			throw new IllegalStateException("訂單不存在");
		}
		ordersDAO.deleteById(id);
	}

}