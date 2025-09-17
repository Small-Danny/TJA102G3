package com.tibafit.dto.cart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//	CartDTO 是前端購物車展示用的回傳模型：把 Redis 的 Hash 結構（{ productId: qty }）轉成陣列 items
//	與 totalQuantity，利於頁面渲染與徽章顯示。fromCartMap 內建空車防呆與型別轉換，避免控制器在購物
//	車為空時發生 NPE。

public class CartDTO {
	private List<CartItemDTO> items; // 前端好渲染的明細陣列（每筆含 productId + qty）
	private Integer totalQuantity; // 總數量（徽章/角標常用）

	// 方便一次指定 items 與總數
	public CartDTO(List<CartItemDTO> items, Integer totalQuantity) {
		this.items = items;
		this.totalQuantity = totalQuantity;
	}

	// ===== Getter / Setter（JavaBean 命名，便於 Spring/Jackson 綁定）=====
	public List<CartItemDTO> getItems() {
		return items;
	}

	public void setItems(List<CartItemDTO> items) {
		this.items = items;
	}

	public Integer getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	/**
	 * 將 Redis Hash（Map<Object,Object>：productId -> qty）轉為前端友善的 CartDTO。 - 自動把
	 * null/<=0 的 qty 略過加總（但仍會加入清單，若不想加入可再調整） - 提供空車防呆：cart 為 null 或空時，回傳空清單與 0 總數
	 */
	public static CartDTO fromCartMap(Map<Object, Object> cart) {
		// 空車／拿不到購物車 → 回傳預設空資料，避免 NPE
		if (cart == null || cart.isEmpty()) {
			return new CartDTO(new ArrayList<>(), 0);
		}

		var list = new ArrayList<CartItemDTO>();
		int total = 0;

		for (var e : cart.entrySet()) {
			// RedisTemplate 預設 Object，因此需要轉型與 toString() 處理
			Integer pid = Integer.valueOf(e.getKey().toString());
			Integer qty = (Integer) e.getValue();

			// 組成一筆項目（前端要用的 productId + qty）
			list.add(new CartItemDTO(pid, qty));

			// 累計總數量（null 視為 0）
			total += (qty == null ? 0 : qty);
		}

		return new CartDTO(list, total);
	}
}
