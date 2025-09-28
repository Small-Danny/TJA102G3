(function () {
  const stockHint = document.getElementById('stockHint');
  const hidPid    = document.getElementById('variantProductId');
  const hidSize   = document.getElementById('selectedSize');
  const btnCart   = document.getElementById('btnAddToCart');
  const skuText   = document.getElementById('skuText');
  const priceText = document.getElementById('priceText');

  // 將 API 先取出一次
  const STOCK_API = stockHint ? (stockHint.dataset.stockUrl || '/shop/api/stock') : '/shop/api/stock';

  // 初始按鈕狀態：沒有 pid 就先停用
  if (btnCart && (!btnCart.dataset.id || btnCart.dataset.id.trim() === '')) {
    btnCart.setAttribute('aria-disabled', 'true');
    btnCart.classList.add('disabled');
  }

  function setHint(text, isError) {
    if (!stockHint) return;
    stockHint.textContent = text;
    stockHint.classList.toggle('text-danger', !!isError);
    stockHint.classList.toggle('text-secondary', !isError);
  }
  
  // 價格格式化
  function setPrice(val) {
    if (!priceText) return;
    if (val == null || val === '') return;
    const n = Number(val);
    priceText.textContent = Number.isFinite(n) ? n.toLocaleString('zh-TW') : String(val);
  }

  // 控制請求並避免亂序
  let inflightCtrl = null;

  async function loadStock(pid) {
    if (!stockHint) return;

    // 無 pid：代表此尺寸未上架或未選
    if (!pid) {
      setHint('此尺寸未上架或尚未選擇尺寸', true);
      if (btnCart) {
        btnCart.setAttribute('aria-disabled', 'true');
        btnCart.classList.add('disabled');
      }
      return;
    }

    // 取消上一個請求
    if (inflightCtrl) inflightCtrl.abort();
    inflightCtrl = new AbortController();

    setHint('載入庫存中…', false);

    try {
      const r = await fetch(`${STOCK_API}?productId=${encodeURIComponent(pid)}`, {
        credentials: 'same-origin',
        signal: inflightCtrl.signal
      });

      // content-type 檢查以避免非 JSON 回傳
      const ctype = r.headers.get('content-type') || '';
      const isJson = ctype.toLowerCase().includes('application/json');

      if (!isJson) {
        // 非 JSON 直接當作錯誤處理
        throw new Error('Non-JSON response');
      }

      const data = await r.json();

      if (!r.ok) throw new Error('HTTP ' + r.status);

      // 預期結構 { ok: true, stock: number }
      if (data && data.ok === true && typeof data.stock === 'number') {
        if (data.stock > 0) {
          setHint(`庫存：${data.stock} 件`, false);
          if (btnCart) {
            btnCart.removeAttribute('aria-disabled');
            btnCart.classList.remove('disabled');
          }
        } else {
          setHint('目前缺貨', true);
          if (btnCart) {
            btnCart.setAttribute('aria-disabled', 'true');
            btnCart.classList.add('disabled');
          }
        }
      } else {
        // 後端欄位名稱不一致時的降級處理
        const stock = Number(data?.stock ?? data?.quantity ?? NaN);
        if (!Number.isNaN(stock)) {
          if (stock > 0) {
            setHint(`庫存：${stock} 件`, false);
            if (btnCart) {
              btnCart.removeAttribute('aria-disabled');
              btnCart.classList.remove('disabled');
            }
          } else {
            setHint('目前缺貨', true);
            if (btnCart) {
              btnCart.setAttribute('aria-disabled', 'true');
              btnCart.classList.add('disabled');
            }
          }
        } else {
          throw new Error('Invalid payload');
        }
      }
    } catch (err) {
      if (err.name === 'AbortError') return; // 使用者又選了其他尺寸
      setHint('無法取得庫存，請稍後再試', true);
      if (btnCart) {
        btnCart.setAttribute('aria-disabled', 'true');
        btnCart.classList.add('disabled');
      }
    }
  }

  // 文件層級代理：支援 button/div/span 都可當 size-chip
  document.addEventListener('click', (e) => {
    const chip = e.target.closest('.size-chip');
    if (!chip) return;

    e.preventDefault();

    // 只在同一個 size-group 內切換
    const group = chip.closest('#sizeGroup');
    if (group) {
      group.querySelectorAll('.size-chip.active').forEach(el => {
        el.classList.remove('active');
        el.setAttribute('aria-selected', 'false');
      });
      chip.classList.add('active');
      chip.setAttribute('aria-selected', 'true');
    }

    const size = chip.dataset.size || '';
    let pid    = chip.dataset.pid || '';
    const sku  = chip.dataset.sku  || '';
    const price = chip.dataset.price || '';

    // 正規化 pid（保留空字串；有值時轉成純數字字串）
    if (pid) {
      const n = Number(pid);
      pid = Number.isFinite(n) ? String(n) : String(pid);
    }

    if (hidSize) hidSize.value = size;
    if (hidPid)  hidPid.value  = pid;

    if (btnCart) {
      if (pid) {
        btnCart.setAttribute('data-id', pid);
      } else {
        btnCart.removeAttribute('data-id');
      }
      if (skuText && sku) skuText.textContent = sku;
    }

    if (skuText && sku) skuText.textContent = sku;
    setPrice(price);                                
    loadStock(pid);
  });

  // 首次載入如果有預設 active 尺寸就查庫存；否則提示
  const active = document.querySelector('#sizeGroup .size-chip.active');
  if (active) {
    const pid = active.dataset.pid || '';
    const sku = active.dataset.sku || '';
    active.setAttribute('aria-selected', 'true');
    loadStock(pid);
  } else {
    setHint('請選擇尺寸以查看庫存', false);
  }
  
  // ==========================================================
  // 「加入購物車」按鈕邏輯（合併重複代碼，保留完整功能）
  // ==========================================================
  if (btnCart) {
    btnCart.addEventListener('click', function(event) {
      event.preventDefault();

      // 檢查按鈕是否處於停用狀態
      if (this.classList.contains('disabled') || this.getAttribute('aria-disabled') === 'true') {
        setHint('請先選擇有庫存的尺寸', true);
        return;
      }

      const productId = this.dataset.id;
      const quantityInput = document.getElementById('qty');
      const quantity = quantityInput ? quantityInput.value : 1;
      
      if (!productId) {
        console.error('找不到商品 Variant ID，無法加入購物車。');
        setHint('無法加入購物車，請重新選擇尺寸', true);
        return;
      }
      
      // 呼叫 cart.js 中的 addItemToCart 函式
      if (typeof addItemToCart === 'function') {
        addItemToCart(productId, quantity, {
          onSuccess: function(data) {
            alert('商品已成功加入購物車！');
            
            // 1. 觸發通用事件（與 productlist.js 保持一致，便於全局監聽）
            document.dispatchEvent(new CustomEvent('cart:changed', { detail: data }));
            
            // 2. 保留原始的 updateCartInfo 調用（兼容既有邏輯）
            if (typeof window.TibaFit?.updateCartInfo === 'function') {
              window.TibaFit.updateCartInfo();
            }
          },
          onError: function(error) {
            alert('加入購物車失敗：' + (error?.message || '請稍後再試'));
          }
        });
      } else {
        alert('發生錯誤：購物車功能未正確載入。');
      }
    });
  }
})();