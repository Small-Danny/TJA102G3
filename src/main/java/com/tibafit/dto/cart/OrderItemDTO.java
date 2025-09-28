package com.tibafit.dto.cart;

import com.tibafit.model.cart.OrderItemVO;

//	OrderItemDTO 是訂單明細的回傳模型。透過 from(OrderItemVO) 把資料庫的 OrderItemVO 轉成前端需要的
//	欄位（productId、quantity、buyPrice、itemTotalPrice、orderItemCode）。通常搭配 OrdersDTO 一
//	起回傳整張訂單的明細列表；若擔心 oiv.getProduct() 為 null，可在 from(...) 內加空值防護以避免
//	NPE。

public class OrderItemDTO {
	private Integer productId; // 商品 ID（由關聯的 Product 取出）
	private String productName;
	private Integer quantity; // 數量
	private Integer buyPrice; // 成交單價（下單當下的快照）
	private Integer itemTotalPrice; // 小計（通常 = buyPrice * quantity）
	private String orderItemCode; // 訂單明細代碼（若有規則碼或流水）

	// ===== Getter / Setter（JavaBean 命名，便於 Spring/Jackson 綁定）=====
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(Integer buyPrice) {
		this.buyPrice = buyPrice;
	}

	public Integer getItemTotalPrice() {
		return itemTotalPrice;
	}

	public void setItemTotalPrice(Integer itemTotalPrice) {
		this.itemTotalPrice = itemTotalPrice;
	}

	public String getOrderItemCode() {
		return orderItemCode;
	}

	public void setOrderItemCode(String orderItemCode) {
		this.orderItemCode = orderItemCode;
	}

	/**
	 * 將 JPA Entity（OrderItemVO）轉成前端友善的 DTO。 - 這裡透過 oiv.getProduct().getProductId()
	 * 取得商品 ID（假設 VO 與 Product 有 ManyToOne 關聯） - 其餘欄位直接取 VO 中的快照欄位（數量、單價、小計、明細代碼） -
	 * 若擔心 product 為 null（例如延遲載入/資料異常），可加上空值防護以避免 NPE
	 */
	public static OrderItemDTO from(OrderItemVO oiv) {
		var oid = new OrderItemDTO();

		// ⚠ 若 oiv.getProduct() 可能為 null，建議改寫為：
		// Integer pid = (oiv.getProduct() != null) ? oiv.getProduct().getProductId() :
		// null;
		// oid.setProductId(pid);
		if (oiv.getProduct() != null) {
			oid.setProductId(oiv.getProduct().getProductId());
			// 假設您的 ProductVO 中有名為 getProductName() 的方法
			oid.setProductName(oiv.getProduct().getProductName());
		} else {
			// 如果關聯的商品因故不存在，提供一個預設值
			oid.setProductId(null);
			oid.setProductName("商品資料不存在");
		}

		oid.setQuantity(oiv.getOrderItemQuantity()); // 訂單明細數量
		oid.setBuyPrice(oiv.getBuyPrice()); // 成交單價（快照）
		oid.setItemTotalPrice(oiv.getItemTotalPrice()); // 小計（若未存快照，可於此處以 buyPrice*quantity 計算）
		oid.setOrderItemCode(oiv.getOrderItemCode()); // 明細代碼（若有）
		return oid;
	}
}
