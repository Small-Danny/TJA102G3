// assets/js/cart.js
(function() {
	'use strict';

	const USER_ID = window.USER_ID || 1;

	// 對應 CartController
	const API_CART = '/api/cart/cart-items';  // GET / DELETE
	const API_ITEM = '/api/cart/items';       // POST / PUT
	const API_PROD = '/api/products';         // GET

	const $list = document.getElementById('cartList');
	const $sumSubtotal = document.getElementById('sum-subtotal');
	const $sumOrder = document.getElementById('sum-order');
	const $sumGrand = document.getElementById('sum-grand');
	const $usePoints = document.getElementById('usePoints');
	const $btnNext = document.getElementById('btnNext');

	let rows = []; // {cartItemId, productId, cartItemQuantity, productName, productPrice, productPicture}

	const money = n => '$ ' + Number(n).toLocaleString('en-US', { maximumFractionDigits: 0 });

	async function fetchJson(url, opt) {
		const r = await fetch(url, opt);
		if (!r.ok) throw new Error('HTTP ' + r.status);
		const text = await r.text();
		return text ? JSON.parse(text) : {};
	}


	// 載入購物車
	async function load() {
		// 1) 後端拿購物車 (CartDTO)
		const cartDto = await fetchJson(`${API_CART}?userId=${encodeURIComponent(USER_ID)}`);

		if (!cartDto.items || cartDto.items.length === 0) {
			rows = [];
			render(); recalc();
			return;
		}

		// 2) 後端拿商品（依購物車 productId）
		const ids = [...new Set(cartDto.items.map(c => c.productId))];
		const products = await fetchJson(`${API_PROD}?ids=${ids.join(',')}`);
		const pmap = Object.fromEntries(products.map(p => [p.productId, p]));

		// 3) join
		rows = cartDto.items.map(c => {
			const p = pmap[c.productId] || {};
			return {
				cartItemId: c.cartItemId || null,
				productId: c.productId,
				cartItemQuantity: c.quantity || c.cartItemQuantity,
				productName: p.productName || `#${c.productId}`,
				productPrice: p.productPrice || 0,
				productPicture: p.productPicture || 'https://via.placeholder.com/80x80'
			};
		});

		render(); recalc();
	}

	// 畫面渲染

	function render() {
		$list.innerHTML = '';
		if (rows.length === 0) {
			$list.innerHTML = '<li><div class="c-c"><div class="c-data"><h2>購物車是空的</h2></div></div></li>';
			return;
		}
		rows.forEach((r, idx) => {
			const li = document.createElement('li');
			li.dataset.idx = String(idx);
			li.innerHTML = `
          <div class="c-c">
            <div class="c-data">
              <a class="cr-svg d-flex-all btn-remove" href="javascript:void(0)">
                <img src="assets/images/cross.svg" alt="remove">
              </a>
              <img src="${r.productPicture}" alt="${r.productName}" onerror="this.src='https://via.placeholder.com/80x80'">
              <h2><a href="javascript:void(0)">${r.productName}</a></h2>
            </div>
            <div class="c-price"><span class="orgnl">${money(r.productPrice)}</span></div>
            <div class="c-quality">
              <input class="qty" type="number" min="1" step="1" value="${r.cartItemQuantity}">
            </div>
            <div class="c-total"><span class="line-total">${money(r.productPrice * r.cartItemQuantity)}</span></div>
          </div>
        `;
			$list.appendChild(li);
		});
	}



	// 計算合計
	function recalc() {
		const subtotal = rows.reduce((sum, r) => sum + r.productPrice * r.cartItemQuantity, 0);
		const used = Math.max(0, parseInt(($usePoints && $usePoints.value) || '0', 10));
		const order = Math.max(0, subtotal - used);
		const grand = order;

		$sumSubtotal.textContent = money(subtotal);
		$sumOrder.textContent = money(order);
		$sumGrand.textContent = money(grand);

		window._cartSummary = { subtotal, used_points: used, order_amount: order, grand_total: grand };
	}

	// 數量變更 (PUT /api/cart/items)
	$list.addEventListener('input', async (e) => {
		const input = e.target;
		if (!input.classList.contains('qty')) return;
		const li = input.closest('li');
		const idx = Number(li.dataset.idx);
		const qty = Math.max(1, parseInt(input.value || '1', 10));
		rows[idx].cartItemQuantity = qty;
		li.querySelector('.line-total').textContent = money(rows[idx].productPrice * qty);
		recalc();

		try {
			await fetchJson(API_ITEM, {
				method: 'PUT',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ userId: USER_ID, productId: rows[idx].productId, qty })
			});
		} catch (err) {
			console.error(err);
			alert('更新失敗');
		}
	});

	// 刪除單一商品 (DELETE /api/cart/cart-items)
	$list.addEventListener('click', async (e) => {
		const btn = e.target.closest('.btn-remove');
		if (!btn) return;
		const li = btn.closest('li');
		const idx = Number(li.dataset.idx);
		const pid = rows[idx].productId;
		if (!confirm(`確定刪除 product#${pid} ?`)) return;

		try {
			await fetchJson(`${API_CART}?userId=${USER_ID}&productId=${pid}`, { method: 'DELETE' });
			rows.splice(idx, 1);
			render(); recalc();
		} catch (err) {
			console.error(err);
			alert('刪除失敗');
		}
	});

	if ($usePoints) {
		$usePoints.addEventListener('input', () => {
			let v = parseInt($usePoints.value || '0', 10);
			if (isNaN(v) || v < 0) v = 0;
			$usePoints.value = v;
			recalc();
		});
	}

	// 下一步
	// 下一步：把使用點數與 userId 帶到訂單頁
	$btnNext.addEventListener('click', (e) => {
		e.preventDefault(); // 避免 <a> 先跳轉

		// 取目前使用點數（或用 _cartSummary 的 used_points）
		const used = Math.max(0, parseInt(($usePoints && $usePoints.value) || '0', 10));

		// 存給 cart_order.html 讀
		sessionStorage.setItem('usedPoints', String(used));
		localStorage.setItem('uid', String(USER_ID));

		// 前往訂單明細
		location.href = 'cart_order.html';
	});


	load();
})();
