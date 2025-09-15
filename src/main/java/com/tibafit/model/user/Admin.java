package com.tibafit.model.user;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "admins")
public class Admin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "admin_id")
	private Integer adminId;
	
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "account", nullable = false, unique = true)
	private String account;

	@Column(name = "password", nullable = false)
	private String password;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "admins_created_time", nullable = false, updatable = false)
	private Date createTime;

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login_at")
	private Date lastLoginAt;

}