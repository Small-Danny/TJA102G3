package com.tibafit.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.cart.CartSetQuantityDTO;
import com.tibafit.model.cart.CartItemVO;
import com.tibafit.repository.cart.CartItemDAO;
import com.tibafit.service.cart.CartService;

import jakarta.validation.Valid;

//  CartAdminController 提供後台/測試用途的購物車管理 API。它一方面透過 CartService 操作 Redis 中的購物車（新增/置數量/移除/清空）
//  ，另一方面可選擇用 CartItemDAO 查詢 MySQL 的購物車快照（如果你有把購物車另存 DB 以便稽核或除錯）。
//  整體用於管理者或開發期間快速檢視與調整特定使用者的購物車內容


@RestController // 宣告這是一個 REST 控制器，方法回傳值直接序列化成 JSON
@RequestMapping("/api/admin/carts") // 這支控制器底下所有 API 的共同路徑前綴
public class CartAdminController {
	private final CartService cartService; // 操作「Redis 購物車」的服務（增/改/刪/清空）
	private final CartItemDAO cartItemDAO; // （可選）操作 DB 快照的 DAO：查詢某用戶購物車快照用

	@Autowired // 建構子注入：Spring 會自動把對應的 Bean 傳進來
	public CartAdminController(CartService cartService, CartItemDAO cartItemDAO) {
		this.cartService = cartService;   // 指向購物車服務（Redis）
		this.cartItemDAO = cartItemDAO;   // 指向購物車快照 DAO（MySQL，有使用才會生效）
	}

	@GetMapping("/{userId}") // GET /api/admin/carts/{userId}
	public List<CartItemVO> list(@PathVariable Integer userId) {
		// 從 DB 讀出某用戶的購物車「快照」清單
		// 回傳的是 VO（JPA Entity 對應的資料型別）
		return cartItemDAO.findByUserId(userId);
	}

	@PostMapping("/{userId}/items") // POST /api/admin/carts/{userId}/items
	public void add(@PathVariable Integer userId, @RequestBody @Valid CartSetQuantityDTO req) {
		// 新增或置換某商品數量（偏「新增」若已存在則變成更新）
		// 期待 req 內有 productId 與 qty（qty > 0），@Valid 會先做欄位驗證
		cartService.setQuantity(userId, req.getProductId(), req.getQty());
	}

	@PutMapping("/{userId}/items") // PUT /api/admin/carts/{userId}/items
	public void setQty(@PathVariable Integer userId, @RequestBody @Valid CartSetQuantityDTO req) {
		// 明確「設定數量」的版本（語意上是 idempotent 置數量）
		// 與 POST 的差異在 REST 語意；實作上同樣呼叫 setQuantity
		cartService.setQuantity(userId, req.getProductId(), req.getQty());
	}

	@DeleteMapping("/{userId}/items/{productId}") // DELETE /api/admin/carts/{userId}/items/{productId}
	public void remove(@PathVariable Integer userId, @PathVariable Integer productId) {
		// 從 Redis 購物車刪除指定商品（Hash delete）
		// 若項目不存在，通常為 no-op（不拋錯）
		cartService.removeItem(userId, productId);
	}

	@DeleteMapping("/{userId}") // DELETE /api/admin/carts/{userId}
	public void clear(@PathVariable Integer userId) {
		// 清空該用戶的整車（刪除 key：cart:{userId}）
		cartService.clear(userId);
	}
};