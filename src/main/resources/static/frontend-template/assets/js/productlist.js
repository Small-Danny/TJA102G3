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
      // 錯誤提示也改用 Swal
      Swal.fire('錯誤', '無法加入購物車，缺少商品資訊。', 'error');
      return;
    }

    try {
      // 呼叫 cart.js 中的核心函式
      await addItemToCart(productId, 1);
      
      // 成功提示改用 Swal
      Swal.fire({
        icon: 'success',
        title: '成功加入購物車！',
        showConfirmButton: false,
        timer: 1500 // 1.5秒後自動關閉
      });

    } catch (err) {
      // 失敗提示也改用 Swal
      Swal.fire({
        icon: 'error',
        title: '加入失敗',
        text: err.message || '請稍後再試'
      });
    }
  });
});