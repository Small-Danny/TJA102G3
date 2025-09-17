$(function () {

  // 先移除舊的（避免重複載入造成重複綁定）
  $(document)
    .off('click.cart')
    .off('cart:changed.cart');

  // 開啟
  $(document).on('click.cart', '#open-cart', function (e) {
    e.preventDefault();
    const $lightbox = $('#cart-lightbox');
    const $panel = $lightbox.find('.white_content');

    $lightbox.addClass('is-open');
    // 清舊的 inline（保險，避免 right/width/opacity 被殘留）
    $panel.css({ right: '', width: '', opacity: '' }).addClass('is-open');

    syncCartUI();
  });

  // 關閉（右上叉叉 / 關閉鈕）
  $(document).on('click.cart',
    '#cart-lightbox .lightbox-close, #cart-lightbox .cart-close',
    function (e) {
      e.preventDefault();
      closeCart();
    });

  // 點背景關閉（點到 overlay 本體才關）
  $(document).on('click.cart', '#cart-lightbox', function (e) {
    if (e.target === this) closeCart();
  });

  function closeCart() {
    const $lightbox = $('#cart-lightbox');
    const $panel = $lightbox.find('.white_content');

    $panel.removeClass('is-open');
    // 等過渡結束再隱藏外層，避免閃爍
    setTimeout(() => { $lightbox.removeClass('is-open'); }, 200);
  }

  // 移除商品
  $(document).on('click.cart', '#cart-lightbox .remove-item', function (e) {
    e.preventDefault();
    $(this).closest('li').fadeOut(200, function () {
      $(this).remove();
      $(document).trigger('cart:changed.cart');
    });
  });

  // ---- UI 同步 ----
  $(document).on('cart:changed.cart', function () {
    syncCartUI();
  });

  function syncCartUI() {
    updateCartCount();
    updateCartTotal();
  }

  function updateCartCount() {
    const count = $('#cart-lightbox .cart-popup ul li').length;
    const $badge = $('#open-cart .cart-count');
    if (!$badge.length) return;
    if (count > 0) $badge.text(count).show();
    else $badge.empty().hide();
  }

  function updateCartTotal() {
    let total = 0, useNTD = false;
    $('#cart-lightbox .cart-popup ul li').each(function () {
      let qty = parseInt($(this).attr('data-qty'), 10);
      let price = parseFloat($(this).attr('data-price'));
      if (isNaN(qty) || isNaN(price)) {
        const text = ($(this).find('.p-data p').text() || '').trim();
        if (text.includes('NT$')) useNTD = true;
        const nums = (text.replace(/,/g, '').match(/(\d+(?:\.\d+)?)/g) || []).map(parseFloat);
        if (isNaN(qty) && nums.length >= 1) qty = parseInt(nums[0], 10);
        if (isNaN(price) && nums.length >= 1) price = parseFloat(nums[nums.length - 1]);
      }
      total += (isNaN(qty) ? 0 : qty) * (isNaN(price) ? 0 : price);
    });
    const currency = useNTD ? 'NT$' : '$';
    const formatted = formatMoney(total, useNTD ? 0 : 2);
    $('#cart-lightbox .cart-total span:last-child').text(currency + ' ' + formatted);
  }

  function formatMoney(value, decimals) {
    const n = isFinite(value) ? value : 0;
    const d = isNaN(decimals) ? 2 : Math.abs(decimals);
    const parts = n.toFixed(d).split('.');
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    return d ? parts.join('.') : parts[0];
  }

  // header 用 fetch 載入 → 等有 badge 再做第一次同步
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
