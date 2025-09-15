// 功能：
// 1) 從後端 GET /api/cart/{userId}/summary 取得購物車摘要
// 2) 渲染右側「購物明細」清單與黑色總計框
// 3) 讀 sessionStorage.usedPoints 扣點顯示（不超扣）
// 4) 若購物車為空：停用結帳按鈕並擋提交

(function () {
  'use strict';

  // ====== 可調整區 ======
  const API_BASE = '/api/cart'; // 你的 CartController 前綴（單數）
  const PLACEHOLDER_IMG = '/assets/images/placeholder.png';
  const DEFAULT_USER_ID = 101;  // 沒登入時的預設 userId（依專案調整）
  // =====================

  // 取得 userId 與使用點數（從上一頁 cart.js 存的）
  const uid = Number(localStorage.getItem('uid') || DEFAULT_USER_ID);
  const usedPoints = Number(sessionStorage.getItem('usedPoints') || '0');

  // DOM 節點
  const $form  = document.getElementById('checkout-form');
  const $items = document.getElementById('order-items');
  const $total = document.getElementById('total-amount');
  const $used  = document.getElementById('used-points');
  const $order = document.getElementById('order-amount');
  const $grand = document.getElementById('grand-total');
  const $checkoutBtns = document.querySelectorAll('.checkout-btn');

  // 格式化貨幣
  const currency = (n) => new Intl.NumberFormat('zh-TW', { style: 'currency', currency: 'TWD' }).format(n);

  // 安全字串
  function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, m => ({
      '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'
    }[m]));
  }

  // 載入摘要
  async function loadSummary() {
    const res = await fetch(`${API_BASE}/${uid}/summary`, { cache: 'no-store' });
    if (!res.ok) throw new Error('載入購物車摘要失敗');
    const data = await res.json(); // { items:[...], totalQuantity, totalAmount }
    renderItems(data.items || []);
    renderTotals(data.totalAmount || 0, usedPoints);
  }

  // 渲染商品清單
  function renderItems(items) {
    if (!items.length) {
      $items.innerHTML = `<p class="text-muted">購物車是空的</p>`;
      setCheckoutDisabled(true);
      return;
    }
    setCheckoutDisabled(false);

    $items.innerHTML = items.map(it => `
      <div class="order-item">
        <img src="${it.imageUrl || PLACEHOLDER_IMG}" alt="">
        <div style="flex:1;">
          <div class="name">${escapeHtml(it.productName)}</div>
          <div>單價：${currency(it.unitPrice)}　×　${it.quantity}</div>
        </div>
        <div class="price">${currency(it.subtotal)}</div>
      </div>
    `).join('');
  }

  // 渲染總計
  function renderTotals(totalAmount, usedPts) {
    const safeUsed = Math.max(0, Math.min(usedPts, totalAmount)); // 不可超扣
    const orderAmount = totalAmount;           // 未扣點的訂單金額
    const grand = totalAmount - safeUsed;      // 扣點後合計

    $total.textContent = currency(totalAmount);
    $used.textContent  = `-${currency(safeUsed)}`;
    $order.textContent = currency(orderAmount);
    $grand.textContent = currency(grand);
  }

  // 切換結帳可用狀態
  function setCheckoutDisabled(disabled) {
    $checkoutBtns.forEach(b => b.disabled = !!disabled);
  }

  // 擋掉「空購物車」與「欄位未過驗證」的提交
  if ($form) {
    $form.addEventListener('submit', function (e) {
      // 空購物車就擋
      if ([...$checkoutBtns].some(b => b.disabled)) {
        e.preventDefault();
        alert('購物車為空，無法結帳');
        return;
      }

      // 清除先前錯誤狀態
      ['recipient_name','recipient_phone','recipient_address'].forEach(n => {
        const el = $form[n];
        if (el) el.classList.remove('is-invalid');
      });

      // 交給 HTML5 驗證規則（required / pattern / minlength...）
      if (!$form.checkValidity()) {
        e.preventDefault();

        // 手動標上 is-invalid 並捲到第一個錯誤欄位
        const fields = ['recipient_name','recipient_phone','recipient_address'];
        let firstBad = null;
        fields.forEach(n => {
          const el = $form[n];
          if (el && !el.checkValidity()) {
            el.classList.add('is-invalid');
            if (!firstBad) firstBad = el;
          }
        });
        if (firstBad) firstBad.scrollIntoView({behavior:'smooth', block:'center'});
        return;
      }

      // ✅ 通過驗證：之後要接後端 checkout API 就在這裡接手 fetch POST
      // e.preventDefault();
      // fetch('/api/orders/checkout', { method:'POST', ... });
    });
  }

  // 使用者輸入時即時清除錯誤樣式
  ['recipient_name','recipient_phone','recipient_address'].forEach(n => {
    const el = $form ? $form[n] : null;
    if (el) el.addEventListener('input', () => el.classList.remove('is-invalid'));
  });


  // 進入頁面就載入
  loadSummary().catch(err => {
    console.error(err);
    $items.innerHTML = `<p class="text-danger">載入失敗，請稍後再試</p>`;
    setCheckoutDisabled(true);
  });
})();
