// assets/js/api.js

// 這個函式會先去要 CSRF token，然後再幫你發送真正的 fetch 請求
async function apiFetch(url, options = {}) {
    try {
        // 1. 獲取 CSRF Token
        const csrfResponse = await fetch('/api/csrf-token');
        if (!csrfResponse.ok) {
            throw new Error('無法獲取 CSRF 驗證資訊，請重新整理頁面再試');
        }
        const csrfData = await csrfResponse.json();
        const csrfToken = csrfData.token;
        const csrfHeaderName = csrfData.headerName;

        // 2. 準備請求的 headers
        // 如果 options 裡面本來沒有 headers，就給一個空物件
        if (!options.headers) {
            options.headers = {};
        }

        // 3. 把 CSRF Token 加到 headers 裡面
        options.headers[csrfHeaderName] = csrfToken;
        
        // 4. 設定 credentials: 'include'，確保 session cookie (JSESSIONID) 會被一起送出
        options.credentials = 'include';

        // 5. 執行真正的 fetch 並回傳結果
        return fetch(url, options);

    } catch (error) {
        console.error('API 請求前置作業失敗:', error);
        // 直接把錯誤丟出去，讓呼叫它的地方可以接住並處理
        throw error;
    }
}