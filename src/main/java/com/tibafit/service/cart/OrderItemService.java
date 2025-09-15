package com.tibafit.service.cart;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.model.cart.OrderItemVO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.model.cart.ProductVO;
import com.tibafit.repository.cart.OrderItemDAO;
import com.tibafit.repository.cart.OrdersDAO;
import com.tibafit.repository.cart.ProductDAO;

@Service
public class OrderItemService {
	
	  private final OrdersDAO ordersDAO;
	  private final ProductDAO productDAO;
	  private final OrderItemDAO orderItemDAO;
	  
	  @Autowired
		public OrderItemService(OrdersDAO ordersDAO, ProductDAO productDAO, OrderItemDAO orderItemDAO) {
		    this.ordersDAO = ordersDAO;
		    this.productDAO = productDAO;
		    this.orderItemDAO = orderItemDAO;
		  }
	  
	  
	  public OrderItemVO get(Integer id){
	    return orderItemDAO.findById(id).orElseThrow(() -> new IllegalStateException("找不到訂單明細 ID " + id));
	  }
	  
	  public List<OrderItemVO> listByOrder(Integer orderId){ return orderItemDAO.findByOrder_OrderId(orderId); }

	  @Transactional
	  public OrdersVO addItem(Integer orderId, Integer productId, Integer quantity, Integer buyPrice){
	    OrdersVO order = ordersDAO.findById(orderId).orElseThrow(() -> new IllegalStateException("找不到訂單 ID " + orderId));
	    Integer price = (buyPrice!=null)? buyPrice : productDAO.findOnShelfPrice(productId);
	    if (price==null) throw new IllegalStateException("商品 "+productId+" 不存在或未上架");
	    if (quantity==null || quantity<=0) throw new IllegalStateException("數量須大於 0");

	    OrderItemVO it = new OrderItemVO();
	    it.setOrder(order);
	    ProductVO ref = new ProductVO(); ref.setProductId(productId); it.setProduct(ref);
	    it.setOrderItemQuantity(quantity);
	    it.setBuyPrice(price);
	    it.setItemTotalPrice(price * quantity);
	    it.setOrderItemCode("ITM"+productId+"_"+(new Random().nextInt(900000)+100000));

	    order.getOrderItems().add(it);
	    order.setTotalPrice((order.getTotalPrice()==null?0:order.getTotalPrice()) + it.getItemTotalPrice());
	    return order;
	  }

	  @Transactional
	  public OrdersVO updateQuantity(Integer itemId, Integer newQty){
	    if (newQty==null || newQty<=0) throw new IllegalStateException("數量須大於 0");
	    OrderItemVO it = get(itemId);
	    OrdersVO order = it.getOrder();
	    int old = it.getItemTotalPrice();
	    it.setOrderItemQuantity(newQty);
	    it.setItemTotalPrice(it.getBuyPrice() * newQty);
	    order.setTotalPrice(order.getTotalPrice() - old + it.getItemTotalPrice());
	    return order;
	  }

	  @Transactional
	  public OrdersVO removeItem(Integer itemId){
	    OrderItemVO it = get(itemId);
	    OrdersVO order = it.getOrder();
	    order.setTotalPrice(order.getTotalPrice() - it.getItemTotalPrice());
	    order.getOrderItems().remove(it); // orphanRemoval=true
	    return order;
	  }
	}
