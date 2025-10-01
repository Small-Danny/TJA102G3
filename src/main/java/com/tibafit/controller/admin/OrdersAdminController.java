package com.tibafit.controller.admin;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.admin.AddOrderItemDTO;
import com.tibafit.dto.admin.UpdateOrderItemQtyDTO;
import com.tibafit.dto.admin.UpdateRecipientDTO;
import com.tibafit.dto.admin.UpdateStatusDTO;
import com.tibafit.dto.cart.OrderItemDTO;
import com.tibafit.dto.cart.OrdersDTO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.service.cart.OrderItemService;
import com.tibafit.service.cart.OrdersService;

import jakarta.validation.Valid;

//	查詢單筆訂單、分頁查詢訂單（可依 userId 過濾）。
//	更新收件資訊、更新訂單/付款狀態、刪除整張訂單。
//	維護訂單明細：新增、列出、修改數量、刪除。
//	這組 API 給管理者或後台系統（如 AdminLTE）使用

@RestController // 宣告為 REST 控制器：方法回傳值會自動序列化成 JSON
@RequestMapping("/api/admin/orders") // 這個控制器所有 API 的共同前綴
public class OrdersAdminController {

	private final OrdersService ordersService; // 訂單主檔相關服務（查、改收件人、改狀態、刪單等）
	private final OrderItemService orderItemService; // 訂單明細相關服務（增/修/刪明細）

	@Autowired // 建構子注入：Spring 會把對應的 Service Bean 傳入
	public OrdersAdminController(OrdersService ordersService, OrderItemService orderItemService) {
		this.ordersService = ordersService;
		this.orderItemService = orderItemService;
	}

	/** 取得單筆訂單（含明細），供後台檢視 */
	@GetMapping("/{id}")
	public OrdersDTO get(@PathVariable Integer id) {
		// service 回傳 OrdersVO（Entity），轉成乾淨的 OrdersDTO 給前端
		return OrdersDTO.from(ordersService.get(id));
	}

	/**
	 * 訂單列表（可選 userId 過濾 + 分頁） 例：GET /api/admin/orders?userId=101&page=0&size=10
	 */
	@GetMapping
	public Page<OrdersDTO> list(@RequestParam(required = false) Integer userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		// 服務層回傳 Page<OrdersVO>，用 map 轉成 Page<OrdersDTO>
		return ordersService.list(userId, page, size).map(OrdersDTO::from);
	}

	/** 更新收件資訊（姓名 / 電話 / 地址） */
	@PutMapping("/{id}/recipient")
	public OrdersDTO updateRecipient(@PathVariable Integer id, @RequestBody @Valid UpdateRecipientDTO req) {
		// @Valid 先做欄位驗證（空白、格式等），再呼叫服務層更新
		OrdersVO o = ordersService.updateRecipient(id, req.getRecipientName(), req.getRecipientPhone(),
				req.getRecipientAddress());
		return OrdersDTO.from(o);
	}

	/** 更新狀態（orderStatus / paymentStatus） */
	@PutMapping("/{id}/status")
	public OrdersDTO updateStatus(@PathVariable Integer id, @RequestBody @Valid UpdateStatusDTO req) {
		// 只更新出貨狀態
		if (req.getPaymentStatus() == null) {
			return ordersService.updateOrderStatus(id, req.getOrderStatus());
		}
		// 同時更新付款狀態
		return ordersService.updateStatus(id, req.getOrderStatus(), req.getPaymentStatus());
	}

	/** 刪除整張訂單（注意：通常需受權限/業務規則限制） */
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Integer id) {
		ordersService.delete(id);
	}

	// ===== 訂單明細 Items 區 =====

	/** 在既有訂單中新增一筆明細（指定商品、數量、單價） */
	@PostMapping("/{orderId}/items")
	public OrdersDTO addItem(@PathVariable Integer orderId, @RequestBody @Valid AddOrderItemDTO req) {
		// 服務層會建立 OrderItem，並回傳更新後的 OrdersVO（通常包含明細）
		OrdersVO o = orderItemService.addItem(orderId, req.getProductId(), req.getQuantity(), req.getBuyPrice());
		return OrdersDTO.from(o);
	}

	/** 列出某訂單的所有明細（後台檢視用） */
	@GetMapping("/{orderId}/items")
	public java.util.List<OrderItemDTO> listItems(@PathVariable Integer orderId) {
		// 把明細的 Entity 轉成乾淨的 DTO，避免直接曝露內部模型
		return orderItemService.listByOrder(orderId).stream().map(OrderItemDTO::from).collect(Collectors.toList());
	}

	/** 修改某一筆明細的數量（依 itemId 指定） */
	@PutMapping("/items/{itemId}")
	public OrdersDTO updateItemQty(@PathVariable Integer itemId, @RequestBody @Valid UpdateOrderItemQtyDTO req) {
		// 服務層負責檢查數量邏輯與更新，回傳整張訂單（方便前端刷新畫面）
		OrdersVO o = orderItemService.updateQuantity(itemId, req.getQuantity());
		return OrdersDTO.from(o);
	}

	/** 刪除某一筆明細（依 itemId 指定） */
	@DeleteMapping("/items/{itemId}")
	public OrdersDTO removeItem(@PathVariable Integer itemId) {
		// 刪除明細後，回傳最新的訂單資料（一般含剩餘明細與總價等）
		OrdersVO o = orderItemService.removeItem(itemId);
		return OrdersDTO.from(o);
	}

}