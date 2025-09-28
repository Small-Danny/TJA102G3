// loader.js (指南最終版)
$(function () {
  window.TibaFit = window.TibaFit || {};
  const headerPlaceholder = $('#header-placeholder');
  const footerPlaceholder = $('#footer-placeholder');
  const basePath = '/frontend-template';
  let currentUser = null;
  // 載入共用組件
  async function loadComponent(url, placeholder) {
    try {
      const html = await $.get(`${url}?v=${Date.now()}`);
      if (placeholder.length) placeholder.html(html);
    } catch (error) { console.error(`載入組件失敗: ${url}`, error); }
  }

  function initializeHeaderScripts() {
    console.log("Header 已載入，開始直接綁帶事件...");
    if ($('#mobile-nav').children().length === 0) {
      try {
        const menuClone = $('.desktop-nav .nav-bar > ul').clone();
        $('#mobile-nav').append(menuClone);
        console.log("手機版選單已成功從桌面版覆製！");
      } catch (e) { console.error("覆製手機版選單失敗:", e); }
    }

    // 使用事件代理來綁帶動態載入的元素
    $(document).on('click', '#mobile-menu', function (e) { e.preventDefault(); $('#mobile-nav').toggleClass('open'); });
    $(document).on('click', '#res-cross', function (e) { e.preventDefault(); $('#mobile-nav').removeClass('open'); });
    $(document).on('click', '#mobile-nav .menu-item-has-children > a', function (e) {
      e.preventDefault();
      $(this).parent().toggleClass('active').siblings().removeClass('active');
    });
    $(document).on('click', '#open-cart', function (e) { e.preventDefault(); $('#cart-lightbox').addClass('is-visible'); });
    $(document).on('click', '.lightbox-close, #cart-lightbox', function (e) {
      if (e.target === this) { $('#cart-lightbox').removeClass('is-visible'); }
    });

    // 【核心】監聽來自其他 JS (如 productlist.js) 的購物車變更事件
    $(document).on('cart:changed', function () {
      console.log('收到 cart:changed 事件，正在更新購物車資訊...');
      updateCartInfo();
    });
  }
  function updateNewsletterBox() {
    const newsletterBox = $('#newsletter-box');
    if (!newsletterBox.length) return;

    if (currentUser) {
        // --- 使用者已登入 ---
        if (currentUser.subscribed) {
            // 如果已經訂閱了
            newsletterBox.html('<p class="text-success">您已訂閱我們的電子報！</p>');
        } else {
            // 如果還沒訂閱，顯示訂閱按鈕
            newsletterBox.html('<button id="subscribe-btn" class="btn btn-primary">點我訂閱電子報</button>');
        }
    } else {
        // --- 使用者未登入 ---
        // 顯示引導登入的訊息和按鈕
        newsletterBox.html(`
            <p>登入會員即可一鍵訂閱！</p>
            <a href="${basePath}/login.html" class="btn btn-secondary">前往登入</a>
        `);
    }
}
  // 在 loader.js 中
  // 【核心】狀態查詢函式 (Single Source of Truth)
 async function checkLoginStatus() {
    const authLinksContainer = $('#auth-links');
    if (!authLinksContainer.length) return;
    try {
        const profileResponse = await fetch('/api/users/profile', { credentials: 'include', cache: 'no-store' });
        if (profileResponse.ok) {
            const user = await profileResponse.json();
            currentUser = user; // <-- 【新增】登入成功時，把使用者資料存起來
            authLinksContainer.html(`<a href="${basePath}/profile.html" class="login">會員中心 (${user.nickName || user.name})</a> / <a href="#" class="logout-link login">登出</a>`);
        } else {
            currentUser = null; // <-- 【新增】未登入或 session 過期時，清空使用者資料
            authLinksContainer.html(`<a href="${basePath}/login.html" class="login">登入 / 註冊</a>`);
        }
    } catch (error) {
        console.error('檢查登入狀態時發生錯誤:', error);
        currentUser = null; // <-- 【新增】發生錯誤時，也清空
        authLinksContainer.html(`<a href="${basePath}/login.html" class="login">登入 / 註冊</a>`);
    }
}

  // 在 loader.js 中

  // 【核心】登出邏輯 (最終修正版)
  $(document).on('click', '.logout-link', function (e) {
    e.preventDefault();

    // 增加一個檢查，確保 Swal 函式庫已載入
    if (typeof Swal === 'undefined') {
      console.error('SweetAlert2 (Swal) is not loaded!');
      // 如果 Swal 不存在，可以給一個簡單的瀏覽器原生提示
      if (confirm('您確定要登出嗎？')) {
        // 執行不依賴 Swal 的登出邏輯
        performLogout();
      }
    } else {
      // 如果 Swal 存在，使用漂亮的彈窗
      Swal.fire({
        title: '您確定要登出嗎？',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: '確定登出',
        cancelButtonText: '取消'
      }).then((result) => {
        if (result.isConfirmed) {
          performLogout();
        }
      });
    }
  });

  // 將實際的登出 fetch 邏輯抽出來，方便共用
  async function performLogout() {
    try {
      const csrfResponse = await fetch('/api/csrf-token');
      const csrfData = await csrfResponse.json();

      const response = await fetch('/api/users/logout', {
        method: 'POST',
        headers: {
          [csrfData.headerName]: csrfData.token
        }
      });

      if (response.ok) {
        // 登出成功後，清除 sessionStorage 並跳轉
        sessionStorage.removeItem('loggedInUser');
        window.location.href = response.url; // 由後端決定跳轉到哪
      } else {
        throw new Error('登出失敗');
      }
    } catch (error) {
      console.error('登出時發生錯誤:', error);
      // 如果 Swal 存在，可以用它來顯示錯誤
      if (typeof Swal !== 'undefined') {
        Swal.fire('錯誤', '登出時發生問題，請稍後再試', 'error');
      } else {
        alert('登出時發生問題，請稍後再試');
      }
    }
  }
  // 【新增】訂閱按鈕的點擊事件處理
  $(document).on('click', '#subscribe-btn', async function () {
    if (!currentUser) return; // 再次確認是登入狀態

    // 顯示處理中，避免重複點擊
    $(this).text('處理中...').prop('disabled', true);

    try {
      // 取得 CSRF token
      const csrfResponse = await fetch('/api/csrf-token');
      const csrfData = await csrfResponse.json();

      // 發送訂閱請求
      const subscribeResponse = await fetch('/api/users/subscribe', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          [csrfData.headerName]: csrfData.token
        },
        body: JSON.stringify({ email: currentUser.email })
      });

      const result = await subscribeResponse.json();

      if (subscribeResponse.ok) {
        Swal.fire('訂閱成功！', result.message, 'success');
        // 更新使用者狀態並重新整理訂閱區塊的畫面
        currentUser.subscribed = true;
        updateNewsletterBox();
      } else {
        throw new Error(result.message || '訂閱失敗');
      }
    } catch (error) {
      console.error('訂閱失敗:', error);
      Swal.fire('發生錯誤', error.message, 'error');
      // 恢復按鈕狀態
      updateNewsletterBox();
    }
  });
  async function updateCartInfo() {
    const cartCountEl = $('#cart-count');
    const cartItemsContainer = $('#cart-items-container');
    const cartTotalEl = $('#cart-lightbox .cart-total span:last-child');
    try {
      const response = await fetch('/api/cart/summary', { credentials: 'include', cache: 'no-store' });
      if (!response.ok) throw new Error('購物車為空或請求失敗');
      const cartData = await response.json();
      cartCountEl.text(cartData.totalQuantity > 0 ? cartData.totalQuantity : '');
      cartItemsContainer.empty();
      if (cartData.items && cartData.items.length > 0) {
        cartData.items.forEach(item => {
          const imgSrc = item.productPicture ? `/frontend-template/assets/img/${item.productPicture}` : 'https://via.placeholder.com/60';
          const itemHtml = `
            <div class="cart-item d-flex align-items-center" style="margin-bottom: 10px; padding-bottom: 10px; border-bottom: 1px solid #eee;">
              <img src="${imgSrc}" alt="${item.productName}" style="width:60px; height:60px; object-fit:cover; margin-right:15px; border-radius: 4px;">
              <div class="item-details" style="flex-grow:1;">
                <div style="font-weight: 500;">${item.productName}</div>
                <small>${item.quantity} x NT$ ${item.unitPrice.toLocaleString()}</small>
              </div>
              <div class="item-price" style="font-weight: 500;">NT$ ${item.subtotal.toLocaleString()}</div>
            </div>`;
          cartItemsContainer.append(itemHtml);
        });
      } else {
        cartItemsContainer.html('<div class="text-center p-3">您的購物車是空的</div>');
      }
      cartTotalEl.text(`NT$ ${cartData.totalAmount ? cartData.totalAmount.toLocaleString() : 0}`);
    } catch (error) {
      console.warn(error.message);
      cartCountEl.text('');
      cartItemsContainer.html('<div class="text-center p-3">您的購物車是空的</div>');
      cartTotalEl.text('NT$ 0');
    }
  }

  window.TibaFit.updateCartInfo = updateCartInfo;
  
  // 主執行流程
  async function main() {
    await Promise.all([
      loadComponent(`${basePath}/assets/layout/header.inc`, headerPlaceholder),
      loadComponent(`${basePath}/assets/layout/footer.inc`, footerPlaceholder)
    ]);
    if (headerPlaceholder.length) {
      initializeHeaderScripts();
      await checkLoginStatus(); // 等待登入狀態確認完畢
      updateCartInfo();     // 再更新購物車
      updateNewsletterBox();
    }
  }

  main();
});