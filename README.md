# 【2.0】TibaFit - 會員與認證系統模組 (已結案)

### 專案管理與文件快速入口
- **🗂️ 專案進度全紀錄 (Notion)**: [點此查看](https://www.notion.so/b175114be7584853bab36f91fe63dd19?v=2597f1b7104c80deab35000cde82f28c)
- **🖥️ TJA102G3 最終簡報**: [點此查看](https://docs.google.com/presentation/d/19NFrOkiwjrEjuGoqULAUm6hftoAE2plSIn8Ri3zTfKM/edit?usp=sharing)
- **🗄️ SQL 資料庫結構 (0930 最新)**: [點此查看](https://docs.google.com/document/d/1Mv8Y0tZvodUAg5iY2C7T5kaWCZFwTcNsEEVekQTxDF0/edit?tab=t.0)

---

## 1. 專案總覽 (Project Overview)

本文件是 TibaFit 會員與認證系統模組 v2.0 的官方技術指南。此版本已全面升級，採用了現代化的混合渲染架構，並基於 Spring Security 6 框架，為整個應用程式提供了一個強大、安全且前後台分離的認證與授權解決方案。

#### 1.1 核心架構：CSR + SSR 混合模式

- **前台 (CSR - Client-Side Rendering)**:
  - **技術棧**: 純 HTML + JavaScript (fetch API)。
  - **安全模型**: API 驅動。前端頁面公開，動態數據透過呼叫受保護的 `/api/**` 接口取得。
- **後台 (SSR - Server-Side Rendering)**:
  - **技術棧**: Thymeleaf。
  - **安全模型**: 重定向驅動。未登入的管理員訪問 `/admin/**` 會被 Spring Security 直接重定向到登入頁。

## 2. 主要功能列表 (Features)

<details>
<summary>點此展開詳細功能列表</summary>

#### 2.1 前台會員功能 (Frontend User Features)
- **使用者註冊**: Email 唯一性與驗證碼驗證 (Redis, 5分鐘有效)、整合 Google reCAPTCHA v2。
- **使用者登入**: Session-based 認證、Google reCAPTCHA v2、支援「記住我」(14天)。
- **忘記密碼**: 透過 Email 寄送安全的 UUID Token 重設連結 (15分鐘有效)。
- **會員資料管理**: 修改個人資料、大頭照上傳預覽、修改密碼。

#### 2.2 後台管理功能 (Admin Features)
- **管理員登入**: 獨立的 `/admin/login` 路徑與認證流程。
- **會員列表與查詢**: 顯示所有會員、支援關鍵字模糊查詢。
- **會員狀態管理**: 可對使用者帳號進行「停權」與「復權」。
- **管理員個人設定**: 修改自己的登入密碼。

</details>

## 3. 技術棧 (Technology Stack)

- **後端 (Backend)**: Java 17, Spring Boot 3, Spring Security 6, Spring Data JPA (Hibernate), Thymeleaf, MySQL, Redis, Maven
- **前端 (Frontend)**: HTML5, CSS3, JavaScript (ES6), AdminLTE, SweetAlert2, Google reCAPTCHA v2
- **開發工具**: IntelliJ IDEA, VS Code, DBeaver, SourceTree

## 4. 核心安全架構 (SecurityConfig.java)

`SecurityConfig.java` 是整個應用程式的安全中樞。最終版本採用了以下核心設計，以應對複雜的前後台混合場景：

- **雙安全過濾鏈 (Dual SecurityFilterChain)**:
  - **後台鏈 (`@Order(1)`)**: 優先處理所有 `/admin/**` 及相關 API 路徑的請求。它擁有獨立的登入頁、驗證邏輯 (`adminAuthenticationProvider`) 與安全上下文 (`adminSecurityContextRepository`)。
  - **前台鏈 (`@Order(2)`)**: 處理所有非後台的請求。它使用另一套獨立的驗證邏輯 (`customUserAuthenticationProvider`) 與安全配置。
  - 這種完全隔離的架構，確保了前後台權限的清晰分離，杜絕了互相干擾的可能。

- **智慧型認證入口 (`DelegatingAuthenticationEntryPoint`)**:
  - 當未登入用戶訪問時，此組件會智慧判斷請求來源：
    - 若為 API 請求 (`/api/**`)，則回傳 `401 Unauthorized` JSON 錯誤，供前端 JS 處理。
    - 若為頁面請求 (`/admin/**` 或 `/frontend-template/**`)，則將用戶重定向到對應的登入頁面。

- **獨立的 Session 與 Remember-Me**:
  - 為了讓管理員和普通用戶能**同時登入**而不衝突，我們為兩者分別配置了獨立的 `SecurityContextRepository` 和 `RememberMeServices`，將他們的登入狀態儲存在不同的 Session 屬性中。

想了解完整的授權規則與細節實作，請直接參考專案中的 `com.tibafit.config.SecurityConfig.java` 原始碼。

## 5. 環境設定與啟動 (Setup & Run)

<details>
<summary>點此展開環境設定與啟動步驟</summary>

- **環境需求**:
  - Java 17+
  - Maven 3.8+
  - MySQL & Redis
- **設定步驟**:
  1. Clone 專案到本地。
  2. 在 `src/main/resources/application.properties` 中配置你的數據庫、Redis、Remember-Me Key 與 Google reCAPTCHA Keys。
- **執行**:
  - 在專案根目錄執行 `mvn spring-boot:run`。
  - 前台訪問: `http://localhost:8080/`
  - 後台訪問: `http://localhost:8080/admin/login`
</details>

---

## 📚 完整技術文件庫
<details>
<summary>點此展開所有相關文件連結</summary>

### 置頂常用
- 📘 【3.0】TibaFit 專案：共用 Header & Footer 導入指南｜[Google 文件](https://docs.google.com/document/d/1d6IbYluVN3PTOd46kBJGrdmg39DfGHIatgf_UoGVAtM/edit?usp=sharing)
- 🗄️ SQL 資料庫（0930 最新）｜[Google 文件](https://docs.google.com/document/d/1Mv8Y0tZvodUAg5iY2C7T5kaWCZFwTcNsEEVekQTxDF0/edit?tab=t.0)
- 🚨 緊急 SOP：從 Git 歷史強制刪除敏感檔案｜[Google 文件](https://docs.google.com/document/d/1GUcw4dmD11z4d1WKMY6nYQU4wZXTrE0omPbqiteggck/edit?usp=sharing)

### 開發環境與工具
- 🧭 SourceTree 下載連結（共筆）｜[HackMD](https://hackmd.io/@necdisd/SJSctAhYh#SourceTree-%E4%B8%8B%E8%BC%89%E9%80%A3%E7%B5%90)
- 🌐 ngrok 實戰教學：本地分享｜[Google 文件](https://docs.google.com/document/d/1ij8hVUt7Wyuwdh9iRwqP6kht6MwfEGYQ0BQnS7R8_Xs/edit?tab=t.0)

### 後端設定與安全
- 🔐 【3.0】會員登入狀態－前後端協助指南｜[Google 文件](https://docs.google.com/document/d/1LrK1geNfW2fFWOg4sKWoBoVfYAGz53Awuz3NsK9hS0c/edit?usp=sharing)
- 🛡️ CSRF 的前後端設定指南｜[Google 文件](https://docs.google.com/document/d/1BJdCUlOcYGETgbquIHhI6ysM31t95DtR-YPNcLaqoBA/edit?usp=sharing)
- 🛠️ 在 Eclipse 中重建 application.properties｜[Google 文件](https://docs.google.com/document/d/1QeRWaGLOa7dhy4vsWHABHA9kteVzavqqcJRSCvcN4GE/edit?tab=t.0)

### 前端與 UI
- 🎨 SweetAlert2 美化 alert 與 confirm｜[Google 文件](https://docs.google.com/document/d/1UkdmK22UXqmIRFvXmVwoG7-0MqKLKZZRm32FSo-2hJ0/edit?usp=sharing)

### 版本控制與流程
- 🌿 Git 流程｜[Google 文件](https://docs.google.com/document/d/1MlGfCZ0r3eZtFbpEWnP_-mT12XituiPHqKjzZJFpQHA/edit?usp=sharing)
- 🧰 Git 指令大全｜[Google 文件](https://docs.google.com/document/d/13xamak6s3GO2fPaQSx2t4f7TL2i4i1S_xq8lxLaSVoc/edit?usp=sharing)
- 🧠 IntelliJ IDEA Git 大師級講義｜[Google 文件](https://docs.google.com/document/d/1OKDJ13qlRrybboEtWzb28mlBfgd1qb4WMQ40eIlv86M/edit?usp=sharing)
- 🧹 將檔案從 Git 移除的一勞永逸解法｜[Google 文件](https://docs.google.com/document/d/1MpOu2w_o0Y3L1ZsBXjZgQCL1R8fooZRBxTT0vaXf3FM/edit?usp=sharing)

### 專案模組與文件
- 📘 規範文檔｜[Google 文件](https://docs.google.com/document/d/1yWMLyZnHneiVIY0PNK857lqcUnP4e_bbDdLTOG7RN1g/edit?tab=t.0)
- 📄 【2.0】TibaFit - 會員與認證系統模組 README｜[Google 文件](https://docs.google.com/document/d/1mG8NWSN3xs50Z9qYxrSL3eJfwwnUOAuwxGMPW8newHM/edit?usp=sharing)
- 🐳 Docker 崩潰安裝｜[Google 文件](連結網址)
- ☁️ GCP 環境建置與部署教學｜[Google 文件](連結網址)
- 💳 LINE Pay 模擬串接（前後端整合）｜[Google 文件](https://docs.google.com/document/d/1lLFje8nG917H-4V4YfoFPe8ETYlOgaBVNnXZcbbVtU8/edit?usp=sharing)
- 💳 TibaFit 金流串接整合｜[Google 文件](https://docs.google.com/document/d/1Z6ixvdG3221meCfPTjRUS8yRG9Jh8H51ndjUjY1PpcU/edit?tab=t.0)
- 🖥️ TJA102G3 簡報｜[Google 文件](https://docs.google.com/presentation/d/19NFrOkiwjrEjuGoqULAUm6hftoAE2plSIn8Ri3zTfKM/edit?usp=sharing)

</details>