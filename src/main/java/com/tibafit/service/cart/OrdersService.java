package com.tibafit.service.cart;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.model.cart.OrdersVO;
import com.tibafit.repository.cart.OrdersDAO;

//	讀單（get）、分頁列表（可選 userId 過濾，時間新→舊）。
//	部分更新收件資訊（只覆寫非 null 欄位）。
//	更新訂單狀態與付款狀態；當設為已付款時自動寫入 paymentTime。
//	刪除訂單（含存在性檢查）。

@Service // 訂單主檔服務：查單、分頁列表、更新收件資訊/狀態、刪除
public class OrdersService {

	private final OrdersDAO ordersDAO; // Spring Data JPA Repository

	@Autowired
	public OrdersService(OrdersDAO ordersDAO) {
		this.ordersDAO = ordersDAO;
	}

	/** 以 ID 讀取單筆訂單；不存在則丟出業務例外（由全域例外處理轉 400） */
	public OrdersVO get(Integer id) {
		return ordersDAO.findById(id).orElseThrow(() -> new IllegalStateException("找不到訂單 ID " + id));
	}

	/**
	 * 分頁查詢訂單 - 若帶 userId：回傳該使用者的訂單（時間新→舊） - 否則：回全體訂單（同樣新→舊）
	 */
	public Page<OrdersVO> list(Integer userId, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), // 頁碼下限保護
				Math.max(size, 1), // 每頁筆數下限保護
				Sort.by(Sort.Direction.DESC, "orderDate")); // 依下單時間倒序

		if (userId != null) {
			return ordersDAO.findByUserIdOrderByOrderDateDesc(userId, pageable);
		}
		return ordersDAO.findAll(pageable);
	}

	/**
	 * 局部更新收件資訊（非 null 才覆寫） Transactional：進入持久化內容後，於交易結束時由 JPA flush
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
	 * 更新訂單狀態 / 付款狀態 - orderStatus / paymentStatus 任一為 null 表示不變更 - 當 paymentStatus
	 * 設為 1（已付款）時，自動補寫付款時間 now()
	 */
	@Transactional
	public OrdersVO updateStatus(Integer id, Integer orderStatus, Integer paymentStatus) {
		OrdersVO o = get(id);
		if (orderStatus != null)
			o.setOrderStatus(orderStatus);
		if (paymentStatus != null) {
			o.setPaymentStatus(paymentStatus);
			if (paymentStatus == 1) {
				o.setPaymentTime(LocalDateTime.now()); // 已付款 → 補付款時間
			}
			// 若業務需要：paymentStatus == 0（未付）時可考慮將 paymentTime 置為 null
		}
		return o;
	}

	/** 刪除訂單（不存在則丟出業務例外） */
	@Transactional
	public void delete(Integer id) {
		if (!ordersDAO.existsById(id)) {
			throw new IllegalStateException("訂單不存在");
		}
		ordersDAO.deleteById(id);
	}
}
