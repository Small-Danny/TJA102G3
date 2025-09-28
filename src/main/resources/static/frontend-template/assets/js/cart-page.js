// assets/js/cart-page.js
$(function() {
    const $list = $('#cartList');
    const $sumSubtotal = $('#sum-subtotal');
    const $sumOrder = $('#sum-order');
    const $sumGrand = $('#sum-grand');
    const $usePoints = $('#usePoints');
    const $btnNext = $('#btnNext');

    const money = n => `NT$ ${Number(n || 0).toLocaleString('zh-TW')}`;

    // 核心函式：從 API 重新取得購物車資料並渲染整個頁面
    async function refreshCartPage() {
        try {
            const response = await apiFetch('/api/cart/summary');
            const data = await response.json();

            $list.empty();
            if (!data.items || data.items.length === 0) {
                $list.html('<li><div class="c-c"><div class="c-data"><h2>您的購物車是空的</h2></div></div></li>');
                $btnNext.addClass('disabled').attr('href', '#');
            } else {
                $btnNext.removeClass('disabled').attr('href', 'cart_order.html');
                data.items.forEach(item => {
                    const imgSrc = item.productPicture ? `/frontend-template/assets/img/${item.productPicture}` : 'https://via.placeholder.com/80x80';
                    const li = `
                      <li data-product-id="${item.productId}">
                        <div class="c-c">
                          <div class="c-data">
                            <a class="cr-svg d-flex-all btn-remove" href="javascript:void(0)"><img src="/frontend-template/assets/images/cross.svg" alt="remove"></a>
                            <img src="${imgSrc}" alt="${item.productName}" onerror="this.src='https://via.placeholder.com/80x80'">
                            <h2><a href="/shop/product/${item.productId}">${item.productName}</a></h2>
                          </div>
                          <div class="c-price"><span class="orgnl">${money(item.unitPrice)}</span></div>
                          <div class="c-quality"><input class="qty" type="number" min="1" step="1" value="${item.quantity}"></div>
                          <div class="c-total"><span class="line-total">${money(item.subtotal)}</span></div>
                        </div>
                      </li>`;
                    $list.append(li);
                });
            }

            // 更新總計
            const subtotal = data.totalAmount || 0;
            const used = Math.max(0, parseInt($usePoints.val() || '0', 10));
            const order = Math.max(0, subtotal - used);
            $sumSubtotal.text(money(subtotal));
            $sumOrder.text(money(order));
            $sumGrand.text(money(order));

            // 把最新的摘要存到 sessionStorage 供下一頁使用
            sessionStorage.setItem('cartSummary', JSON.stringify({
                items: data.items,
                subtotal: subtotal,
                usedPoints: used
            }));

        } catch (error) {
            console.error("載入購物車失敗:", error);
            $list.html('<li><div class="c-c"><div class="c-data"><h2>載入購物車失敗，請稍後再試</h2></div></div></li>');
        }
    }

    // --- 事件綁定 ---
    const debounce = (func, delay) => {
        let timeout;
        return function(...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), delay);
        };
    };

    $list.on('input', '.qty', debounce(async function() {
        const productId = $(this).closest('li').data('product-id');
        const quantity = Math.max(1, parseInt($(this).val() || '1', 10));
        await apiFetch('/api/cart/items', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json', [window.TibaFit.csrf.headerName]: window.TibaFit.csrf.token },
            body: JSON.stringify({ productId, qty: quantity })
        });
        await refreshCartPage(); // 重新整理整個頁面
        window.TibaFit.updateCartInfo(); // 同步更新 header
    }, 500));

    $list.on('click', '.btn-remove', async function() {
        const productId = $(this).closest('li').data('product-id');
        if (!confirm(`確定要從購物車中移除此商品嗎？`)) return;
        await apiFetch(`/api/cart/items?productId=${productId}`, { 
            method: 'DELETE',
            headers: { [window.TibaFit.csrf.headerName]: window.TibaFit.csrf.token }
        });
        await refreshCartPage();
        window.TibaFit.updateCartInfo();
    });

    $usePoints.on('input', refreshCartPage);
    $btnNext.on('click', function(e) {
        if ($(this).hasClass('disabled')) {
            e.preventDefault();
            alert('您的購物車是空的，無法進行下一步！');
        }
        // sessionStorage 的儲存已在 refreshCartPage 中完成
    });

    // 頁面初次載入
    refreshCartPage();
});