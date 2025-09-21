(() => {
  const PAGE_SIZE = 9;
  const grid = document.getElementById('product-grid');
  if (!grid) return;

  const items = Array.from(grid.querySelectorAll('.product-item'));
  const emptyTip = document.getElementById('empty-tip');
  const pager = document.getElementById('pagination');

  // 沒商品：顯示提示、隱藏分頁
  if (items.length === 0) {
    if (pager) pager.style.display = 'none';
    if (emptyTip) emptyTip.style.display = '';
    return;
  }
  // 有商品：隱藏「沒有商品」提示
  if (emptyTip) emptyTip.style.display = 'none';

  const totalPages = Math.max(1, Math.ceil(items.length / PAGE_SIZE));
  let current = 1;

  function renderPage(page) {
    current = Math.min(Math.max(1, page), totalPages);

    // 全部先隱藏
    items.forEach(el => { el.style.display = 'none'; });

    const start = (current - 1) * PAGE_SIZE;
    const end = Math.min(start + PAGE_SIZE, items.length);
    for (let i = start; i < end; i++) {
      items[i].style.display = ''; // 顯示
    }

    // 更新分頁 UI
    buildPagination();
    // 捲到商品區頂端（可改成平滑捲動）
    grid.scrollIntoView({ behavior: 'instant', block: 'start' });
  }

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
      if (!disabled && !active) {
        a.addEventListener('click', () => renderPage(page));
      }
      li.appendChild(a);
      pager.appendChild(li);
    };

    // 上一頁
    addLi('«', current - 1, current === 1);

    // 頁碼（帶省略號）
    const windowSize = 3; // 目前頁左右各顯示幾頁
    let start = Math.max(1, current - windowSize);
    let end = Math.min(totalPages, current + windowSize);

    if (start > 1) {
      addLi('1', 1, false, current === 1);
      if (start > 2) addLi('…', current, true, false);
    }

    for (let p = start; p <= end; p++) {
      addLi(String(p), p, false, p === current);
    }

    if (end < totalPages) {
      if (end < totalPages - 1) addLi('…', current, true, false);
      addLi(String(totalPages), totalPages, false, current === totalPages);
    }

    // 下一頁
    addLi('»', current + 1, current === totalPages);
  }

  // 初始化
  renderPage(1);

  // (可選) 監聽加入購物車
  document.addEventListener('click', async (e) => {
    const btn = e.target.closest('.add-to-cart');
    if (!btn) return;
    const id = Number(btn.getAttribute('data-id'));
    try {
      // TODO: 換成你隊友的 Redis API
      const res = await fetch('/api/cart/items', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json'},
        body: JSON.stringify({ productId: id, qty: 1 })
      });
      if (!res.ok) throw new Error('加入購物車失敗');
      document.dispatchEvent(new Event('cart:changed'));
    } catch (err) {
      console.error(err);
      alert('加入購物車失敗，請稍後再試');
    }
  });
})();
