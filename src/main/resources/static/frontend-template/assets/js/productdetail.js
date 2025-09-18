(function () {
  const stockHint = document.getElementById('stockHint');
  const hidPid    = document.getElementById('variantProductId');
  const hidSize   = document.getElementById('selectedSize');
  const btnCart   = document.getElementById('btnAddToCart');

  
  function setHint(text, isError) {
    if (!stockHint) return;
    stockHint.textContent = text;
    stockHint.classList.toggle('text-danger', !!isError);
    stockHint.classList.toggle('text-secondary', !isError);
  }

  async function loadStock(pid) {
    if (!stockHint) return;
    const api = stockHint.getAttribute('data-stock-url') || '/shop/api/stock';
    if (!pid) { setHint('請選擇尺寸以查看庫存', false); return; }

    setHint('載入庫存中…', false);
    try {
      const r = await fetch(`${api}?productId=${encodeURIComponent(pid)}`);
      const data = await r.json();
      if (r.ok && data && data.ok) {
        setHint(`庫存：${data.stock} 件`, false);
      } else {
        throw new Error();
      }
    } catch {
      setHint('無法取得庫存，請稍後再試', true);
    }
  }

  // 文件層級代理：不怕外層阻擋或 chip 是 span/div/button
  document.addEventListener('click', (e) => {
    const chip = e.target.closest('.size-chip');
    if (!chip) return;

    // 避免被覆蓋層攔截的情況（若有）
    e.preventDefault();

    // 切換 active（只在同一個 size-group 內）
    const group = chip.closest('#sizeGroup');
    if (group) {
      group.querySelectorAll('.size-chip.active').forEach(el => el.classList.remove('active'));
      chip.classList.add('active');
    }

    const size = chip.getAttribute('data-size') || '';
    const pid  = chip.getAttribute('data-pid')  || '';

    if (hidSize) hidSize.value = size;
    if (hidPid)  hidPid.value  = pid;
    if (btnCart && pid) btnCart.setAttribute('data-id', pid);

    loadStock(pid);
	console.log('clicked size:', size, 'pid:', pid);
  });

  // 首次載入如果有預設 active 尺寸就查庫存
  const active = document.querySelector('#sizeGroup .size-chip.active');
  if (active) {
    loadStock(active.getAttribute('data-pid'));
  } else {
    setHint('請選擇尺寸以查看庫存', false);
  }
})();
