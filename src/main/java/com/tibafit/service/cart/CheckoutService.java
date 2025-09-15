package com.tibafit.service.cart;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.model.cart.OrderItemVO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.model.cart.ProductVO;
import com.tibafit.repository.cart.OrdersDAO;
import com.tibafit.repository.cart.ProductDAO;

@Service
public class CheckoutService {
	private final CartService cartservice;
	private final OrdersDAO ordersDAO;
	private final ProductDAO productDAO;

	@Autowired
	public CheckoutService(CartService cartservice, OrdersDAO ordersDAO, ProductDAO productDAO) {
		this.cartservice = cartservice;
		this.ordersDAO = ordersDAO;
		this.productDAO = productDAO;
	}

	@Transactional
	public OrdersVO createOrderFromCart(Integer userId, String rn, String rp, String ra, Integer usedPts) {
	    var cart = cartservice.getCart(userId);
	    if (cart == null || cart.isEmpty()) throw new IllegalStateException("購物車是空的");

	    OrdersVO vo = new OrdersVO();
	    vo.setUserId(userId);
	    vo.setOrderStatus(0);          // 未出貨
	    vo.setPaymentStatus(0);        // 未付款
	    vo.setPaymentTime(null);       // 建議未付款先為 null
	    vo.setOrderCode("ORD" + System.currentTimeMillis() + (new Random().nextInt(9000) + 1000));

	    if (rn != null) vo.setRecipientName(rn);
	    if (rp != null) vo.setRecipientPhone(rp);
	    if (ra != null) vo.setRecipientAddress(ra);

	    int total = 0;
	    for (var e : cart.entrySet()) {
	        Integer pid = Integer.valueOf(e.getKey().toString());
	        Integer qty = (Integer) e.getValue();

	        Integer price = productDAO.findOnShelfPrice(pid);
	        if (price == null) throw new IllegalStateException("商品 " + pid + " 不存在或未上架");

	        OrderItemVO oit = new OrderItemVO();
	        oit.setOrder(vo);
	        // ⬇⬇ 用 reference 掛外鍵（不會誤插商品）
	        oit.setProduct(productDAO.getReferenceById(pid));
	        oit.setOrderItemQuantity(qty);
	        oit.setBuyPrice(price);
	        oit.setItemTotalPrice(price * qty);
	        oit.setOrderItemCode("ITM" + pid + "_" + (new Random().nextInt(900000) + 100000));

	        vo.getOrderItems().add(oit);
	        total += oit.getItemTotalPrice();
	    }

	    // ⬇⬇ 扣點安全（不可超扣、不可負數）
	    int safeUsed = Math.max(0, Math.min(usedPts == null ? 0 : usedPts, total));
	    vo.setUsedPointsAmount(safeUsed);

	    vo.setTotalPrice(total);
	    OrdersVO saved = ordersDAO.save(vo);

	    // ⬇⬇ 建單完成清購物車（Redis）
	    cartservice.clear(userId);

	    return saved;
	}


	@Transactional
	public OrdersVO markPaid(Integer id) {
		var o = ordersDAO.findById(id).orElseThrow();
		o.setPaymentStatus(1);
		o.setPaymentTime(LocalDateTime.now());
		return o;
	}

	@Transactional
	public OrdersVO markFailed(Integer id) {
		var o = ordersDAO.findById(id).orElseThrow();
		o.setPaymentStatus(2);
		return o;
	}
}
