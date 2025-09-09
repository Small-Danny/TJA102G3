package com.tibafit.model.user;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "account_status", nullable = false)
	private Integer accountStatus = 1;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", nullable = false, updatable = false)
	private Date createTime;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_time", nullable = false)
	private Date updateTime;

	@Column(name = "name")
	private String name;

	@Column(name = "nick_name")
	private String nickName;

	@Column(name = "phone")
	private String phone;

	@Column(name = "profile_picture")
	private String profilePicture;

	@Column(name = "gender")
	private Integer gender;

	@Column(name = "height_cm", precision = 5, scale = 2)
	private BigDecimal heightCm;

	@Column(name = "weight_kg", precision = 5, scale = 2)
	private BigDecimal weightKg;

	@Column(name = "bmi", precision = 4, scale = 2)
	private BigDecimal bmi;

	@Column(name = "points_balance", nullable = false)
	private Integer pointsBalance = 0;
	
	@Column(name = "reset_password_token")
	private String resetPasswordToken;

    @Column(name = "token_expiry_date")
    private LocalDateTime tokenExpiryDate;
    //Java 8 之後處理日期和時間的標準 API，它不含時區資訊
}