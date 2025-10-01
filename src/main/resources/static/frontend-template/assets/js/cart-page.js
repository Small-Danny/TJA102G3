// assets/js/cart-page.js
// TibaFit 前台購物車頁面（摘要、數量更新、刪除、金額重算）

(function($) {
	'use strict';

	// ---------- 後備 apiFetch（若專案已有 apiFetch 會略過這段） ----------
	if (typeof window.apiFetch !== 'function') {
		window.apiFetch = async function(url, options = {}) {
			try {
				const t = await fetch('/api/csrf-token', { credentials: 'include' });
				if (t.ok) {
					const { headerName, token } = await t.json();
					options.headers = Object.assign({}, options.headers, { [headerName]: token });
				}
			} catch (_) { /* 後端若無 csrf endpoint 也沒關係 */ }
			return fetch(url, Object.assign({ credentials: 'include' }, options));
		};
	}

	$(function() {
		// ---------- DOM ----------
		const $list = $('#cartList');
		const $sumSubtotal = $('#sum-subtotal');
		const $sumOrder = $('#sum-order');
		const $sumGrand = $('#sum-grand');
		const $usePoints = $('#usePoints');
		const $btnNext = $('#btnNext');

		// ---------- Helpers ----------
		const money = (n) => `NT$ ${Number(n || 0).toLocaleString('zh-TW')}`;

		const debounce = (fn, delay) => {
			let timer;
			return function(...args) {
				clearTimeout(timer);
				timer = setTimeout(() => fn.apply(this, args), delay);
			};
		};

		function computeAndRenderTotals(data) {
			const subtotal = data.totalAmount || 0;
			const used = Math.max(0, parseInt($usePoints.val() || '0', 10));
			const order = Math.max(0, subtotal - used);

			$sumSubtotal.text(money(subtotal));
			$sumOrder.text(money(order));
			$sumGrand.text(money(order));

			sessionStorage.setItem('cartSummary', JSON.stringify({
				items: data.items || [],
				subtotal,
				usedPoints: used,
			}));
		}

		// ---------- 主函式：拉資料並渲染 ----------
		async function refreshCartPage() {
			try {
				const res = await apiFetch('/api/cart/summary');
				if (!res.ok) throw new Error(`載入購物車失敗（${res.status}）`);
				const data = await res.json();

				$list.empty();

				if (!data.items || data.items.length === 0) {
					$list.html('<li><div class="c-c"><div class="c-data"><h2>您的購物車是空的</h2></div></div></li>');
					$btnNext.addClass('disabled').attr('href', '#');
				} else {
					$btnNext.removeClass('disabled').attr('href', 'cart_order.html');

					data.items.forEach((item) => {
						const imgSrc = item.productPicture
							? `/frontend-template/assets/img/${item.productPicture}`
							: 'https://via.placeholder.com/80x80';

						const li = `
              <li data-product-id="${item.productId}">
                <div class="c-c">
                  <div class="c-data">
                    <a class="cr-svg d-flex-all btn-remove" href="javascript:void(0)">
                      <img src="/frontend-template/assets/images/cross.svg" alt="remove">
                    </a>
                    <img src="${imgSrc}" alt="${item.productName}" onerror="this.src='https://via.placeholder.com/80x80'">
                    <h2><a href="/shop/product/${item.productId}">${item.productName}</a></h2>
                  </div>
                  <div class="c-price"><span class="orgnl">${money(item.unitPrice)}</span></div>
                  <div class="c-quality">
                    <input class="qty" type="number" min="1" step="1" value="${item.quantity}">
                  </div>
                  <div class="c-total"><span class="line-total">${money(item.subtotal)}</span></div>
                </div>
              </li>`;
						$list.append(li);
					});
				}

				computeAndRenderTotals(data);
			} catch (err) {
				console.error(err);
				$list.html('<li><div class="c-c"><div class="c-data"><h2>載入購物車失敗，請稍後再試</h2></div></div></li>');
			}
		}

		// ---------- 事件：更新數量（PUT /api/cart/items，body: { productId, qty }） ----------
		$list.on('input', '.qty', debounce(async function() {
			const productId = $(this).closest('li').data('product-id');
			const qty = Math.max(1, parseInt($(this).val() || '1', 10));

			try {
				const res = await apiFetch('/api/cart/items', {
					method: 'PUT',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ productId, qty })
				});

				if (!res.ok) {
					const txt = await res.text().catch(() => '');
					throw new Error(`更新數量失敗（${res.status}）${txt ? '：' + txt : ''}`);
				}

				await refreshCartPage();
				window.TibaFit?.updateCartInfo?.();
			} catch (err) {
				console.error(err);
				alert(err.message || '更新數量失敗，請稍後再試');
			}
		}, 500));

		// ---------- 事件：刪除品項（DELETE /api/cart/items?productId=...） ----------
		$list.on('click', '.btn-remove', async function() {
			const productId = $(this).closest('li').data('product-id');
			if (!confirm('確定要從購物車中移除此商品嗎？')) return;

			try {
				const res = await apiFetch(`/api/cart/items?productId=${productId}`, {
					method: 'DELETE'
					// 注意：你的 Controller 只收 query 參數，這裡不要傳 JSON body
				});

				if (!res.ok) {
					const txt = await res.text().catch(() => '');
					throw new Error(`刪除失敗（${res.status}）${txt ? '：' + txt : ''}`);
				}

				await refreshCartPage();
				window.TibaFit?.updateCartInfo?.();
			} catch (err) {
				console.error(err);
				alert(err.message || '刪除失敗，請稍後再試');
			}
		});

		// ---------- 事件：即時重算點數 ----------
		$usePoints.on('input', async function() {
			try {
				const res = await apiFetch('/api/cart/summary');
				if (!res.ok) throw new Error(`重算失敗（${res.status}）`);
				const data = await res.json();
				computeAndRenderTotals(data);
			} catch (err) {
				console.error(err);
			}
		});

		// ---------- 事件：下一步 ----------
		$btnNext.on('click', function(e) {
			if ($(this).hasClass('disabled')) {
				e.preventDefault();
				alert('您的購物車是空的，無法進行下一步！');
			}
		});

		// ---------- 初次載入 ----------
		refreshCartPage();
	});
})(jQuery);
