document.addEventListener('DOMContentLoaded', () => {
  const PAGE_SIZE = 9;

  const grid   = document.getElementById('product-grid');
  const pager  = document.getElementById('pagination');
  const emptyTip = document.getElementById('empty-tip');

  if (!grid) {
    console.error('找不到商品列表容器 #product-grid');
    return;
  }

  // 取得所有商品卡
  const items = Array.from(grid.querySelectorAll('.product-item'));

  // 無商品：顯示提示、隱藏分頁
  if (items.length === 0) {
    if (pager) pager.style.display = 'none';
    if (emptyTip) emptyTip.style.display = '';
    return;
  }
  if (emptyTip) emptyTip.style.display = 'none';

  // ===== 分頁邏輯 =====
  const totalPages = Math.max(1, Math.ceil(items.length / PAGE_SIZE));
  let current = 1;

  function buildPagination() {
    if (!pager) return;
    pager.innerHTML = '';
    if (totalPages <= 1) return;

    const addLi = (label, page, disabled = false, active = false) => {
      const li = document.createElement('li');
      li.className = 'page-item' + (disabled ? ' disabled' : '') + (active ? ' active' : '');
      const a = document.createElement('a');
      a.className = 'page-link';
      a.href = 'javascript:void(0)';
      a.textContent = label;
      if (!disabled && !active) a.addEventListener('click', () => renderPage(page));
      li.appendChild(a);
      pager.appendChild(li);
    };

    // 上一頁
    addLi('«', current - 1, current === 1);

    // 頁碼窗口（含省略號）
    const windowSize = 3; // 左右各顯示幾頁
    let start = Math.max(1, current - windowSize);
    let end   = Math.min(totalPages, current + windowSize);

    if (start > 1) {
      addLi('1', 1, false, current === 1);
      if (start > 2) addLi('…', current, true, false);
    }

    for (let p = start; p <= end; p++) addLi(String(p), p, false, p === current);

    if (end < totalPages) {
      if (end < totalPages - 1) addLi('…', current, true, false);
      addLi(String(totalPages), totalPages, false, current === totalPages);
    }

    // 下一頁
    addLi('»', current + 1, current === totalPages);
  }

  function renderPage(page) {
    current = Math.min(Math.max(1, page), totalPages);

    // 全部先隱藏
    items.forEach(el => { el.style.display = 'none'; });

    const start = (current - 1) * PAGE_SIZE;
    const end   = Math.min(start + PAGE_SIZE, items.length);
    for (let i = start; i < end; i++) items[i].style.display = '';

    buildPagination();

    // 捲回商品區（可改 smooth）
    grid.scrollIntoView({ behavior: 'instant', block: 'start' });
  }

  // 初始化第一頁
  renderPage(1);

  // ===== 統一的「加入購物車」處理（事件代理在 grid 上）=====
  grid.addEventListener('click', async (event) => {
    const btn = event.target.closest('.add-to-cart');
    if (!btn) return;

    event.preventDefault();

    const productId = btn.dataset.id || btn.getAttribute('data-id');
    if (!productId) {
      console.error('找不到 Product ID！');
      alert('無法加入購物車，缺少商品資訊。');
      return;
    }

    // 先用 cart.js 的 addItemToCart（若已載入），否則退回 fetch API
    const useCartJs = (typeof window.addItemToCart === 'function');

    try {
      if (useCartJs) {
        await new Promise((resolve, reject) => {
          window.addItemToCart(productId, 1, {
            onSuccess: resolve,
            onError: reject
          });
        });
      } else {
        const res = await fetch('/api/cart/items', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ productId: Number(productId), qty: 1 })
        });
        if (!res.ok) throw new Error('加入購物車失敗');
      }

      // 成功後事件與提示（讓頁首購物車徽章能聽這個事件去更新）
      document.dispatchEvent(new Event('cart:changed'));
      alert('商品已成功加入購物車！');

      // 如果你有函式可直接更新徽章，也可在這裡呼叫：
      // if (typeof updateCartIconCount === 'function') updateCartIconCount();

    } catch (err) {
      console.error(err);
      alert(useCartJs
        ? '加入購物車失敗，請稍後再試。（cart.js onError）'
        : '加入購物車失敗，請稍後再試。');
    }
  });
});