<header class="header-style-one">
  <div class="container">
    <div class="row">
      <div class="desktop-nav" id="stickyHeader">
        <div class="container">
          <div class="row">
            <div class="col-lg-12">
              <div class="d-flex-all justify-content-between">

                <!-- LOGO -->
                <div class="header-logo">
                  <a href="/frontend-template/index.html">
                    <figure><img src="/frontend-template/assets/images/logo-2.svg" alt="TibaFit Logo"></figure>
                  </a>
                </div>

                <!-- 導覽 -->
                <div class="nav-bar">
                  <ul>
                    <li><a href="/frontend-template/plan.html">運動計畫</a></li>
                    <li class="menu-item-has-children"><a href="/analytics">數據圖表</a>
                      <ul class="sub-menu">
                        <li><a href="/analytics">數據圖表</a></li>
                        <li><a href="/frontend-template/task.html">我的任務</a></li>
                      </ul>
                    </li>
                    <li><a href="/frontend-template/forum.html">討論區</a></li>
                    <li><a href="/shop/products">商城</a></li>
                  </ul>
                </div>

                <!-- 右側：登入 / 購物車 / 手機選單 -->
                <div class="extras">
                  <div id="auth-links" style="white-space: nowrap;">
                    <a href="/frontend-template/login.html" class="login">登入 / 註冊</a>
                  </div>

                  <a href="javascript:void(0)" id="open-cart" class="shopping-bag white-icon" aria-label="開啟購物車">
                    <svg viewBox="0 0 512 512" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                      <path d="m452 120h-60.946c-7.945-67.478-65.477-120-135.054-120s-127.109 52.522-135.054 120h-60.946c-11.046 0-20 8.954-20 20v352c0 11.046 8.954 20 20 20h392c11.046 0 20-8.954 20-20v-352c0-11.046-8.954-20-20-20zm-196-80c47.484 0 87.019 34.655 94.659 80h-189.318c7.64-45.345 47.175-80 94.659-80zm176 432h-352v-312h40v60c0 11.046 8.954 20 20 20s20-8.954 20-20v-60h192v60c0 11.046 8.954 20 20 20s20-8.954 20-20v-60h40z"/>
                    </svg>
                    <span class="cart-count" id="cart-count"></span>
                  </a>

                  <a href="javascript:void(0)" id="mobile-menu" class="menu-start">
                    <svg id="ham-menu" viewBox="0 0 100 100" width="26" height="26">
                      <path class="line line1" d="M 20,29 H 80"></path>
                      <path class="line line2" d="M 20,50 H 80"></path>
                      <path class="line line3" d="M 20,71 H 80"></path>
                    </svg>
                  </a>
                </div>

              </div>
            </div>
          </div>
        </div>

        <div class="mobile-nav" id="mobile-nav"></div>
      </div>
    </div>
  </div>
</header>

<!-- Cart Lightbox -->
<div id="cart-lightbox" class="cart-overlay clearfix" aria-hidden="true">
  <div class="white_content" role="dialog" aria-labelledby="cart-title" aria-modal="true">
    <a href="javascript:;" class="lightbox-close cart-close" aria-label="關閉">✕</a>

    <div class="cart-popup">
      <h3 id="cart-title" class="cart-title">購物車</h3>

      <!-- 可滾動的清單區 -->
      <div id="cart-items-container" class="cart-scroll">
        <!-- 由 JS 動態渲染 -->
      </div>

      <!-- 總計與按鈕 -->
      <div class="cart-btn">
        <div class="cart-total d-flex align-items-center justify-content-between">
          <span>總計：</span>
          <span id="cart-total">NT$ 0</span>
        </div>
        <div class="cart-btns d-flex align-items-center justify-content-between mt-3">
          <a class="btn btn-outline-theme" href="/frontend-template/cart.html">查看購物車</a>
          <a class="btn btn-theme" href="/frontend-template/cart_order.html">結帳</a>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- ==== 這段樣式可放全站，也可跟著 header.inc 走 ==== -->
<style>
  /* 讓燈箱有最基本顯示控制（若你已有就可刪） */
  .cart-overlay { display: none; }
  .cart-overlay.open { display: block; }

  .cart-scroll { max-height: 420px; overflow: auto; }

  /* 單筆商品卡 */
  .mini-cart-item {
    position: relative;
    display: grid;
    grid-template-columns: 64px 1fr auto;
    gap: 12px;
    align-items: center;
    padding: 10px 8px;
    border-bottom: 1px solid #eee;
  }
  .mini-cart-thumb img {
    width: 64px; height: 64px; object-fit: cover; border-radius: 6px;
  }
  .mini-cart-price { white-space: nowrap; font-weight: 600; }

  /* 新增「刪除」按鈕 */
  .mini-cart-remove {
    position: absolute;
    top: 6px; right: 6px;
    border: none; background: transparent;
    font-size: 18px; line-height: 1;
    cursor: pointer; opacity: .55;
  }
  .mini-cart-remove:hover { opacity: 1; }
</style>

<script>
(function () {
  const cartOverlay   = document.getElementById('cart-lightbox');
  const cartCloseBtn  = cartOverlay?.querySelector('.cart-close');
  const openCartBtn   = document.getElementById('open-cart');
  const cartListWrap  = document.getElementById('cart-items-container');
  const cartTotalEl   = document.getElementById('cart-total');
  const cartCountEl   = document.getElementById('cart-count');

  /* ========= CSRF 取用（新增 Cookie 備援） ========= */
  let CSRF = { headerName: null, token: null };

  function readCookie(name) {
    return document.cookie
      .split(';')
      .map(s => s.trim())
      .find(s => s.startsWith(name + '='))?.split('=')[1];
  }

  async function ensureCsrf() {
    if (CSRF.token && CSRF.headerName) return CSRF;

    // 1) 先從 Cookie 取（CookieCsrfTokenRepository 預設 XSRF-TOKEN）
    const cookieToken = readCookie('XSRF-TOKEN');
    if (cookieToken) {
      CSRF = { headerName: 'X-XSRF-TOKEN', token: decodeURIComponent(cookieToken) };
      return CSRF;
    }

    // 2) 再嘗試你現有的 API
    try {
      const res = await fetch('/api/csrf-token', { credentials: 'same-origin' });
      if (res.ok) {
        const data = await res.json();
        CSRF = {
          headerName: data.headerName || 'X-CSRF-TOKEN',
          token: data.token || ''
        };
      }
    } catch (_) {}
    return CSRF;
  }
  function withCsrf(headers = {}) {
    if (CSRF.token && CSRF.headerName) headers[CSRF.headerName] = CSRF.token;
    return headers;
  }

  /* ========= 載入購物車摘要 ========= */
  async function loadCart() {
    try {
      const res = await fetch('/api/cart/summary', { credentials: 'same-origin', cache: 'no-store' });
      if (!res.ok) throw 0;
      const data = await res.json();
      renderCart(data || { items: [], totalAmount: 0, totalQuantity: 0 });
    } catch {
      renderCart({ items: [], totalAmount: 0, totalQuantity: 0 });
    }
  }

  /* ========= 繪製 UI ========= */
  function renderCart(model) {
    const items = Array.isArray(model.items) ? model.items : [];
    cartListWrap.innerHTML = items.map(it => {
      const pid   = it.productId ?? it.id;
      const name  = escapeHtml(it.productName ?? it.name ?? '');
      const qty   = Number(it.quantity ?? it.orderItemQuantity ?? 0);
      const price = Number(it.unitPrice ?? it.price ?? 0);
      const sub   = qty * price;

      const picUrl = it.productPicture
        ? `/frontend-template/assets/img/${encodeURIComponent(it.productPicture)}`
        : '/frontend-template/assets/images/line_qrcode_placeholder.svg';

      return `
        <div class="mini-cart-item" data-product-id="${pid}">
          <div class="mini-cart-thumb"><img src="${picUrl}" alt="${name}"></div>
          <div class="mini-cart-info">
            <div class="fw-semibold">${name}</div>
            <div class="text-muted" style="font-size:.9rem">${qty} x NT$ ${fmt(price)}</div>
          </div>
          <div class="mini-cart-price">NT$ ${fmt(sub)}</div>
          <button class="mini-cart-remove" title="移除" aria-label="移除" data-product-id="${pid}">&times;</button>
        </div>
      `;
    }).join('') || `<div class="text-center text-muted py-4">購物車是空的</div>`;

    const total = Number(model.totalAmount ?? 0);
    cartTotalEl.textContent = `NT$ ${fmt(total)}`;

    // 使用後端提供的 totalQuantity 優先，其次自己加總
    const count = Number(model.totalQuantity ?? items.reduce((s, it)=>s + Number(it.quantity||0), 0));
    cartCountEl.textContent = count > 0 ? count : '';
  }

  /* ========= 刪除商品（改成 ?productId=...） ========= */
  async function removeItem(productId) {
    await ensureCsrf();

    const url = `/api/cart/items?productId=${encodeURIComponent(Number(productId))}`;
    const res = await fetch(url, {
      method: 'DELETE',
      headers: withCsrf({ 'Accept': 'application/json' }),
      credentials: 'same-origin'
    });

    if (!(res.ok || res.status === 204)) {
      const msg = await res.text().catch(()=>'');
      throw new Error(`移除失敗：${res.status} ${msg}`);
    }
  }

  /* ========= 事件 ========= */
  openCartBtn?.addEventListener('click', async () => {
    await loadCart();
    openOverlay();
  });

  cartListWrap.addEventListener('click', async (evt) => {
    const btn = evt.target.closest('.mini-cart-remove');
    if (!btn) return;
    const pid = btn.dataset.productId;
    if (!pid) return;

    btn.disabled = true;
    try {
      await removeItem(pid);
      await loadCart();
    } catch (e) {
      alert('移除商品失敗，請稍後再試');
      console.error(e);
    } finally {
      btn.disabled = false;
    }
  });

  cartCloseBtn?.addEventListener('click', closeOverlay);
  cartOverlay?.addEventListener('click', (e) => {
    if (e.target === cartOverlay) closeOverlay();
  });
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') closeOverlay();
  });

  function openOverlay(){
    cartOverlay?.setAttribute('aria-hidden','false');
    cartOverlay?.classList.add('open');
  }
  function closeOverlay(){
    cartOverlay?.setAttribute('aria-hidden','true');
    cartOverlay?.classList.remove('open');
  }

  // 初次同步徽章
  loadCart();

  // 小工具
  function fmt(n){ return (Math.round(Number(n||0)*100)/100).toLocaleString() }
  function escapeHtml(s){return String(s??'').replace(/[&<>"']/g,m=>({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]))}
})();
</script>
