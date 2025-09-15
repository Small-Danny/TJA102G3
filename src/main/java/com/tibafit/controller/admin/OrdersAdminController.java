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

@RestController
@RequestMapping("/api/admin/orders")
public class OrdersAdminController {
  private final OrdersService ordersService;
  private final OrderItemService orderItemService;
  
  @Autowired
  public OrdersAdminController(OrdersService ordersService, OrderItemService orderItemService) {
      this.ordersService = ordersService;
      this.orderItemService = orderItemService;
  }

  @GetMapping("/{id}") public OrdersDTO get(@PathVariable Integer id){ return OrdersDTO.from(ordersService.get(id)); }

  @GetMapping
  public Page<OrdersDTO> list(@RequestParam(required=false) Integer userId,
                              @RequestParam(defaultValue="0") int page,
                              @RequestParam(defaultValue="10") int size){
    return ordersService.list(userId, page, size).map(OrdersDTO::from);
  }

  @PutMapping("/{id}/recipient")
  public OrdersDTO updateRecipient(@PathVariable Integer id, @RequestBody @Valid UpdateRecipientDTO req){
    OrdersVO o = ordersService.updateRecipient(id, req.getRecipientName(), req.getRecipientPhone(), req.getRecipientAddress());
    return OrdersDTO.from(o);
  }

  @PutMapping("/{id}/status")
  public OrdersDTO updateStatus(@PathVariable Integer id, @RequestBody @Valid UpdateStatusDTO req){
    OrdersVO o = ordersService.updateStatus(id, req.getOrderStatus(), req.getPaymentStatus());
    return OrdersDTO.from(o);
  }

  @DeleteMapping("/{id}") public void delete(@PathVariable Integer id){ ordersService.delete(id); }

  // Items
  @PostMapping("/{orderId}/items")
  public OrdersDTO addItem(@PathVariable Integer orderId, @RequestBody @Valid AddOrderItemDTO req){
    OrdersVO o = orderItemService.addItem(orderId, req.getProductId(), req.getQuantity(), req.getBuyPrice());
    return OrdersDTO.from(o);
  }

  @GetMapping("/{orderId}/items")
  public java.util.List<OrderItemDTO> listItems(@PathVariable Integer orderId){
    return orderItemService.listByOrder(orderId).stream().map(OrderItemDTO::from).collect(Collectors.toList());
  }

  @PutMapping("/items/{itemId}")
  public OrdersDTO updateItemQty(@PathVariable Integer itemId, @RequestBody @Valid UpdateOrderItemQtyDTO req){
    OrdersVO o = orderItemService.updateQuantity(itemId, req.getQuantity());
    return OrdersDTO.from(o);
  }

  @DeleteMapping("/items/{itemId}")
  public OrdersDTO removeItem(@PathVariable Integer itemId){
    OrdersVO o = orderItemService.removeItem(itemId);
    return OrdersDTO.from(o);
  }

}
