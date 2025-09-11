package com.tibafit.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tibafit.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	// 只要方法名稱叫做 findBy<欄位名稱>，Spring就會自動幫我們實作查詢
	Optional<User> findByEmail(String email);

	Optional<User> findByResetPasswordToken(String resetPasswordToken);
	
	@Query("SELECT u FROM User u WHERE " +
		       "str(u.userId) LIKE %:keyword% OR " + 
		       "u.name LIKE %:keyword% OR " +
		       "u.email LIKE %:keyword%")
		List<User> searchUsers(@Param("keyword") String keyword);
}
