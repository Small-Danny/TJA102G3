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

@Service
public class OrdersService {
	
	private final OrdersDAO ordersDAO;
	
	@Autowired
	public OrdersService(OrdersDAO ordersDAO) {
	    this.ordersDAO = ordersDAO;
	  }
	
	public OrdersVO get(Integer id){
	    return ordersDAO.findById(id).orElseThrow(() -> new IllegalStateException("找不到訂單 ID " + id));
	  }

	  public Page<OrdersVO> list(Integer userId, int page, int size){
	    Pageable pageable = PageRequest.of(Math.max(page,0), Math.max(size,1), Sort.by(Sort.Direction.DESC, "orderDate"));
	    if (userId!=null) return ordersDAO.findByUserIdOrderByOrderDateDesc(userId, pageable);
	    return ordersDAO.findAll(pageable);
	  }

	  @Transactional
	  public OrdersVO updateRecipient(Integer id, String name, String phone, String address){
	    OrdersVO o = get(id);
	    if (name!=null) o.setRecipientName(name);
	    if (phone!=null) o.setRecipientPhone(phone);
	    if (address!=null) o.setRecipientAddress(address);
	    return o;
	  }

	  @Transactional
	  public OrdersVO updateStatus(Integer id, Integer orderStatus, Integer paymentStatus){
	    OrdersVO o = get(id);
	    if (orderStatus!=null) o.setOrderStatus(orderStatus);
	    if (paymentStatus!=null) { o.setPaymentStatus(paymentStatus); if (paymentStatus==1) o.setPaymentTime(LocalDateTime.now()); }
	    return o;
	  }

	  @Transactional
	  public void delete(Integer id){
	    if (!ordersDAO.existsById(id)) throw new IllegalStateException("訂單不存在");
	    ordersDAO.deleteById(id);
	  }
	}
