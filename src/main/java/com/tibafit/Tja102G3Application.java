package com.tibafit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
public class Tja102G3Application implements CommandLineRunner{

	   @Autowired
	    private PasswordEncoder passwordEncoder;

	    public static void main(String[] args) {
	        SpringApplication.run(Tja102G3Application.class, args);
	    }

	    @Override
	    public void run(String... args) throws Exception {
	        // 生成所有團隊需要的加密密碼
	        String encryptedPass1 = passwordEncoder.encode("password123");
	        String encryptedPass2 = passwordEncoder.encode("devuser456");

	        System.out.println("--- 團隊測試密碼 ---");
	        System.out.println("password123 的加密結果: " + encryptedPass1);
	        System.out.println("devuser456 的加密結果: " + encryptedPass2);
	        System.out.println("--------------------");
	    }
}
