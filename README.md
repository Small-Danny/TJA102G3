【2.0】TibaFit - 會員與認證系統模組 README.md


1. 專案總覽 (Project Overview)

本文件是 TibaFit 會員與認證系統模組 v2.0 的官方技術指南。此版本已全面升級，採用了現代化的混合渲染架構，並基於 Spring Security 6 框架，為整個應用程式提供了一個強大、安全且前後台分離的認證與授權解決方案。

1.1 核心架構：CSR + SSR 混合模式

我們的應用程式採用了前後端分離 (CSR) + 伺服器端渲染 (SSR) 的混合架構，以應對不同的業務場景：
前台 (CSR - Client-Side Rendering)：
技術棧：純 HTML + JavaScript (fetch API)。
安全模型：API 驅動。前端頁面本身是公開的，但頁面上的動態數據是透過呼叫受保護的 /api/** 接口來取得。後端透過回傳 HTTP 狀態碼 200 OK (成功) 或 401 Unauthorized (失敗) 來告知前端是否已登入。
後台 (SSR - Server-Side Rendering)：
技術棧：Thymeleaf。
安全模型：重定向驅動。後台頁面本身就是受保護的。如果未登入的管理員嘗試訪問 /admin/**，Spring Security 會在伺服器端直接將其重定向到 /admin/login 頁面。

2. 主要功能列表 (Features)


2.1 前台會員功能 (Frontend User Features)

使用者註冊：
Email 唯一性驗證。
透過 Redis 實現的非同步 Email 驗證碼寄送與驗證 (5 分鐘有效)。
整合 Google reCAPTCHA v2，防止機器人註冊。
使用者登入：
使用 Spring Security 進行 Session-based 認證。
整合 Google reCAPTCHA v2，提升登入安全性。
支援「記住我」功能，cookie 有效期為 14 天。
已停權帳號無法登入。
忘記密碼：
使用者可透過 Email 申請重設密碼。
產生安全的 UUID Token，並透過 Email 寄送重設連結 (15 分鐘有效)。
已停權帳號無法使用此功能。
會員資料管理 (Profile)：
使用者登入後可查看並修改個人資料（姓名、暱稱、性別、身高等）。
支援大頭照圖片上傳、預覽與儲存。
提供密碼修改功能，包含舊密碼驗證與新密碼強度提示。

2.2 後台管理功能 (Admin Features)

管理員登入：
使用獨立的 /admin/login 路徑及頁面。
認證流程與前台會員完全分離，帳號儲存於 admins 資料表。
整合 Google reCAPTCHA v2 保護。
會員列表與查詢：
顯示所有會員列表。
支援關鍵字模糊查詢（依 ID, 姓名, Email）。
會員狀態管理：
管理員可對使用者帳號進行「停權」與「復權」操作。
操作時使用 SweetAlert2 提供體驗更佳的確認對話框。
管理員個人設定：
管理員可修改自己的登入密碼。

3. 技術棧 (Technology Stack)

後端 (Backend): Java 17, Spring Boot 3, Spring Security 6, Spring Data JPA (Hibernate), Thymeleaf, MySQL, Redis, Maven
前端 (Frontend): HTML5, CSS3, JavaScript (ES6), AdminLTE, SweetAlert2, Google reCAPTCHA v2
開發工具: (請填寫您團隊使用的工具，例如：Eclipse, IntelliJ IDEA, VS Code, DBeaver)

4. 專案結構概覽 (Project Structure)


4.1 Java 原始碼結構 (src/main/java)

com.tibafit.config: 存放 Spring 框架的核心設定，如 SecurityConfig, WebConfig。
com.tibafit.controller.user: 存放所有的 Controller，負責接收前端請求並回傳回應。
com.tibafit.dto.user: 存放資料傳輸物件 (DTO)，用於在不同層之間安全地傳遞資料。
com.tibafit.exception: 存放自定義的例外類別，如 ValidationException。
com.tibafit.model.user: 存放 JPA 實體 (Entity)，對應資料庫表格。
com.tibafit.repository.user: 存放 Spring Data JPA 的 Repository 介面，負責資料庫操作。
com.tibafit.service.user: 存放商業邏輯的 Service 層。

4.2 資源目錄結構 (src/main/resources)

static/: 存放靜態資源，瀏覽器可以直接存取。
frontend-template/: 存放所有前台會員系統的 HTML, CSS, JavaScript 檔案。
adminlte/: 存放後台管理介面 (AdminLTE) 的樣式和腳本。
templates/: 存放動態樣板，會由後端 Thymeleaf 引擎渲染後再回傳給瀏覽器。
admin/: 存放所有後台管理系統的 HTML 樣板檔案。

5. 安全配置核心 (SecurityConfig.java)

SecurityConfig.java 是整個後端權限系統的單一真相來源，所有關於「誰可以訪問什麼資源」的規則都在這裡集中管理。

5.1 核心 Bean 解析

delegatingAuthenticationEntryPoint()：智慧型入口分發器。當未登入用戶訪問時，它會根據 URL 決定是回傳 401 JSON 錯誤（API 請求），還是重定向到登入頁（頁面請求）。
前後台分離的 Provider 和 Service：我們為前台 (user) 和後台 (admin) 分別建立了獨立的 AuthenticationProvider 和 RememberMeServices，以實現數據和邏輯的完全隔離。
delegatingRememberMeServices()：「記住我」分發器。根據請求是來自前台還是後台，動態選擇對應的 RememberMeServices 進行處理。

5.2 授權規則 (authorizeHttpRequests)

Spring Security 的規則是有順序性的，將最精確的規則放在前面是關鍵的最佳實踐。

Java


// SecurityConfig.java
.authorizeHttpRequests(authorize -> authorize
    // 規則 1: 優先放行後台登入頁，防止重定向循環
    .requestMatchers("/admin/login").permitAll()
    
    // 規則 2: 放行所有公開資源、靜態文件和無需登入的 API
    .requestMatchers("/", "/login.html", "/css/**", "/api/users/login", ...).permitAll()
    
    // 規則 3: 所有 /admin/ 路徑下的請求，都必須擁有 "ADMIN" 角色
    .requestMatchers("/admin/**").hasRole("ADMIN")
    
    // 規則 4: 所有 /api/ 路徑下的請求，都必須經過認證 (登入)
    .requestMatchers("/api/**").authenticated()
    
    // 規則 5: 其他所有未匹配到的請求，都必須經過認證 (預設拒絕)
    .anyRequest().authenticated()
)



6. 前後端協作指南 (Collaboration Guide)


6.1 前端夥伴 (CSR - JavaScript)

核心原則：忘掉 sessionStorage，信任 API 回應。
前端不應儲存任何登入狀態。判斷使用者是否登入的唯一標準，就是去呼叫一個受保護的後端 API (例如 /api/users/profile)，然後檢查其回應。
實作範例 (profile.html)：
頁面載入時，立即 fetch('/api/users/profile')。
如果 response.ok (HTTP 200)，則使用回傳的 JSON 數據渲染頁面。
如果 response.status === 401，則代表未登入，立刻用 window.location.href 將使用者踢回登入頁。

6.2 後端夥伴 (SSR - Thymeleaf)

核心原則：利用 thymeleaf-extras-springsecurity6 在伺服器端完成權限控制。
後台頁面的權限邏輯完全由後端處理，前端無需編寫相關 JavaScript。
實作範例 (layout.html 或其他後台頁面)：
在 pom.xml 中加入 thymeleaf-extras-springsecurity6 依賴。
在 HTML 模板中使用 sec: 屬性來控制內容顯示。
判斷是否登入：<div sec:authorize="isAuthenticated()">...</div>
根據角色顯示：<a th:href="..." sec:authorize="hasRole('ADMIN')">會員管理</a>
顯示用戶名：<span sec:authentication="name"></span>
登出功能：必須使用 POST 表單，並包含 CSRF Token。

7. 環境設定與啟動 (Setup & Run)

環境需求：
Java 17+
Maven 3.8+
對應的數據庫 (例如 MySQL, PostgreSQL)
Redis
設定步驟：
Clone 專案到本地。
在 src/main/resources/application.properties 中配置你的數據庫與 Redis 連線資訊。
配置 app.security.remember-me-key，這是一個用於加密 "記住我" Cookie 的私鑰，請確保其複雜度和唯一性。
配置 Google reCAPTCHA 的 site-key 和 secret-key。
執行：
在專案根目錄執行 mvn spring-boot:run。
前台訪問：http://localhost:8080/
後台訪問：http://localhost:8080/admin/login
