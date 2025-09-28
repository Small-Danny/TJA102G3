// loader.js (指南最終版)
$(function () {
    window.TibaFit = window.TibaFit || {};
    const headerPlaceholder = $('#header-placeholder');
    const footerPlaceholder = $('#footer-placeholder');
    const basePath = '/frontend-template';

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
    $(document).on('click', '#mobile-menu', function(e) { e.preventDefault(); $('#mobile-nav').toggleClass('open'); });
    $(document).on('click', '#res-cross', function(e) { e.preventDefault(); $('#mobile-nav').removeClass('open'); });
    $(document).on('click', '#mobile-nav .menu-item-has-children > a', function(e) {
        e.preventDefault();
        $(this).parent().toggleClass('active').siblings().removeClass('active');
    });
     $(document).on('click', '#open-cart', function (e) { e.preventDefault(); $('#cart-lightbox').addClass('is-visible'); });
        $(document).on('click', '.lightbox-close, #cart-lightbox', function (e) {
            if (e.target === this) { $('#cart-lightbox').removeClass('is-visible'); }
        });

        // 【核心】監聽來自其他 JS (如 productlist.js) 的購物車變更事件
        $(document).on('cart:changed', function() {
            console.log('收到 cart:changed 事件，正在更新購物車資訊...');
            updateCartInfo();
        });
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
                authLinksContainer.html(`<a href="${basePath}/profile.html" class="login">會員中心 (${user.nickName || user.name})</a> / <a href="#" class="logout-link login">登出</a>`);
            } else {
                authLinksContainer.html(`<a href="${basePath}/login.html" class="login">登入 / 註冊</a>`);
            }
        } catch (error) {
            console.error('檢查登入狀態時發生錯誤:', error);
            authLinksContainer.html(`<a href="${basePath}/login.html" class="login">登入 / 註冊</a>`);
        }
    }
    
 // 在 loader.js 中

// 【核心】登出邏輯 (最終修正版)
$(document).on('click', '.logout-link', async function(e) {
    e.preventDefault(); 
    
    if (!confirm('您確定要登出嗎？')) {
        return;
    }

    try {
        // 步驟 1: 在執行登出前，先主動獲取一個最新的 CSRF Token
        const csrfResponse = await fetch('/api/csrf-token', { 
            method: 'GET',
            credentials: 'include' 
        });

        if (!csrfResponse.ok) {
            throw new Error('無法獲取登出驗證資訊，請稍後再試。');
        }
        const csrfData = await csrfResponse.json();

        // 步驟 2: 使用剛剛獲取到的新 Token 來發送登出請求
        const logoutResponse = await fetch('/api/users/logout', { 
            method: 'POST', 
            credentials: 'include',
            headers: {
                // 將 Token 放在正確的 header 中
                [csrfData.headerName]: csrfData.token
            }
        });

        if (logoutResponse.ok) {
            alert('您已成功登出！');
            // 登出成功後，重新導向到首頁
            window.location.href = '/frontend-template/index.html';
        } else {
            const errorData = await logoutResponse.json();
            throw new Error(errorData.message || '登出時發生錯誤。');
        }
    } catch (error) {
        console.error('登出請求失敗:', error);
        alert(`登出失敗：${error.message}`);
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
        }
    }

    main();
});