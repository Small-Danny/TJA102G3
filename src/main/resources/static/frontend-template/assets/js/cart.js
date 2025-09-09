$(function () {

  // ---------- 事件綁定 ----------
  // 開啟燈箱
  $(document).on('click', '#open-cart', function (e) {
    e.preventDefault();
    $('#cart-lightbox').stop(true, true).fadeIn(150).css('display', 'block');
    syncCartUI();
  });

  // 關閉（右上叉叉）
  $(document).on('click', '#cart-lightbox .lightbox-close, #cart-lightbox .cart-close', function (e) {
    e.preventDefault();
    $('#cart-lightbox').stop(true, true).fadeOut(150);
  });

  // 點背景關閉
  $(document).on('click', '#cart-lightbox', function (e) {
    if (e.target === this) $('#cart-lightbox').stop(true, true).fadeOut(150);
  });

  // 移除商品
  $(document).on('click', '#cart-lightbox .remove-item', function (e) {
    e.preventDefault();
    $(this).closest('li').fadeOut(200, function () {
      $(this).remove();
      $(document).trigger('cart:changed');
    });
  });

  // 其他地方動到購物車時可觸發這個事件
  $(document).on('cart:changed', function () {
    syncCartUI();
  });


  // ---------- UI 同步 ----------
  function syncCartUI() {
    updateCartCount();
    updateCartTotal();
  }

  // 右上紅點數量
  function updateCartCount() {
    const count = $('#cart-lightbox .cart-popup ul li').length;
    const $badge = $('#open-cart .cart-count');
    if (!$badge.length) return;
    if (count > 0) {
      $badge.text(count).show();
    } else {
      $badge.empty().hide();
    }
  }

  // 總計
  function updateCartTotal() {
    let total = 0;
    let useNTD = false;

    $('#cart-lightbox .cart-popup ul li').each(function () {
      // 1) 先看資料屬性（若你有加 data-qty / data-price 會最準）
      let qty = parseInt($(this).attr('data-qty'), 10);
      let price = parseFloat($(this).attr('data-price'));

      if (isNaN(qty) || isNaN(price)) {
        // 2) 從文字解析：例如「1 x NT$ 700」或「1 x $25.00」
        const text = ($(this).find('.p-data p').text() || '').trim();
        if (text.includes('NT$')) useNTD = true;

        // 把所有數字抓出來（處理逗號與小數）
        const nums = (text.replace(/,/g, '').match(/(\d+(?:\.\d+)?)/g) || []).map(parseFloat);

        // 規則：第一個是數量、最後一個是單價
        if (isNaN(qty) && nums.length >= 1) qty = parseInt(nums[0], 10);
        if (isNaN(price) && nums.length >= 1) price = parseFloat(nums[nums.length - 1]);
      }

      qty = isNaN(qty) ? 0 : qty;
      price = isNaN(price) ? 0 : price;
      total += qty * price;
    });

    const currency = useNTD ? 'NT$' : '$';
    const formatted = useNTD ? formatMoney(total, 0) : formatMoney(total, 2);
    $('#cart-lightbox .cart-total span:last-child').text(currency + ' ' + formatted);
  }

  // 金額格式化（帶千分位）
  function formatMoney(value, decimals) {
    const n = isFinite(value) ? value : 0;
    const d = isNaN(decimals) ? 2 : Math.abs(decimals);
    const parts = n.toFixed(d).split('.');
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    return d ? parts.join('.') : parts[0];
  }

  // header 是用 fetch 插入的；等它載完再做第一次同步
  (function waitHeaderThenInit() {
    let tries = 0;
    const t = setInterval(() => {
      tries++;
      if ($('#open-cart .cart-count').length || tries > 50) {
        syncCartUI();
        clearInterval(t);
      }
    }, 100);
  })();

});

