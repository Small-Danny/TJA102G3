package com.tibafit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 專案的主程式入口。 - @SpringBootApplication 會啟用自動組態與元件掃描 - 類別放在 com.tibafit 套件下，可讓
 * Spring 從此處向下掃描所有子套件
 */
@SpringBootApplication
@ComponentScan("com.tibafit") // 指定從 com.tibafit 開始掃描 Bean（主類已在該套件下時其實可省略，但保留不會出錯）
public class Tja102G3Application {

	/**
	 * Java 應用程式的進入點。 執行後會： 1) 建立 Spring 應用程式上下文（IoC/DI 容器） 2)
	 * 套用自動組態（依照你的依賴：Web、Data JPA、Redis…） 3) 啟動內嵌 Tomcat 並監聽 server.port（預設 8080，或
	 * application.yml 的設定） 4)
	 * 進行元件掃描，註冊 @Component/@Service/@Repository/@RestController 等 Bean
	 */
	public static void main(String[] args) {
		SpringApplication.run(Tja102G3Application.class, args); // 啟動整個 Spring Boot 應用
	}

}

	/* 
	   這是整個 Spring Boot 專案的啟動入口。執行 main 後，Spring 會建立 IoC 容器、套用自動組態、啟動內嵌 Tomcat，
   	   並從 com.tibafit 開始掃描與載入你寫的所有元件（Controller/Service/Repository 等），讓 API 可以對外提供服務。
    */