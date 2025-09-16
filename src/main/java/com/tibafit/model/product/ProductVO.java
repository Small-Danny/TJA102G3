package com.tibafit.model.product;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class ProductVO implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Integer product_id;
	
	@Column(name = "product_type", nullable = false)
	private Integer product_type;
	
	@Column(name = "product_name", nullable = false, length = 255)
	private String product_name;
	
	@Column(name = "product_description", nullable = false, length = 255)
	private String product_description;
	 
	@Column(name = "product_price", nullable = false)
	private Integer product_price;
	
	@Column(name = "stock_quantity", nullable = false)
	private Integer stock_quantity;
	
	@Column(name = "product_picture", length = 255)
	private String product_picture;
	
	@Column(name = "product_status", nullable = false)
	private Integer product_status;
	
	@Column(name = "product_code", length = 64, unique=true)
	private String product_code;

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}

	public Integer getProduct_type() {
		return product_type;
	}

	public void setProduct_type(Integer product_type) {
		this.product_type = product_type;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getProduct_description() {
		return product_description;
	}

	public void setProduct_description(String product_description) {
		this.product_description = product_description;
	}

	public Integer getProduct_price() {
		return product_price;
	}

	public void setProduct_price(Integer product_price) {
		this.product_price = product_price;
	}

	public Integer getStock_quantity() {
		return stock_quantity;
	}

	public void setStock_quantity(Integer stock_quantity) {
		this.stock_quantity = stock_quantity;
	}

	public String getProduct_picture() {
		return product_picture;
	}

	public void setProduct_picture(String product_picture) {
		this.product_picture = product_picture;
	}

	public Integer getProduct_status() {
		return product_status;
	}

	public void setProduct_status(Integer product_status) {
		this.product_status = product_status;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

}