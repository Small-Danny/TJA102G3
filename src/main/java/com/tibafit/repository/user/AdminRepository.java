package com.tibafit.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tibafit.model.user.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

// 只要方法名稱叫做 findBy<欄位名稱>，Spring就會自動幫我們實作查詢

	Optional<Admin> findByAccount(String account);
}