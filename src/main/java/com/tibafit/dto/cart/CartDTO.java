package com.tibafit.dto.cart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartDTO {
	private List<CartItemDTO> items;
	private Integer totalQuantity;
	
	public CartDTO(List<CartItemDTO> items, Integer totalQuantity) {
        this.items = items;
        this.totalQuantity = totalQuantity;
    }
	
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

	public static CartDTO fromCartMap(Map<Object, Object> cart) {
		var list = new ArrayList<CartItemDTO>();
		int total =0;
		for(var e : cart.entrySet()) {
			Integer pid = Integer.valueOf(e.getKey().toString());
			Integer qty = (Integer)e.getValue();
			list.add(new CartItemDTO(pid, qty));
			total += (qty == null ? 0 :qty);
		}
		return new CartDTO(list, total);
	}
}
