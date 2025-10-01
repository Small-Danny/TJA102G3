(function() {
	const stockHint = document.getElementById('stockHint');
	const hidPid = document.getElementById('variantProductId');
	const hidSize = document.getElementById('selectedSize');
	const btnCart = document.getElementById('btnAddToCart');
	const skuText = document.getElementById('skuText');
	const priceText = document.getElementById('priceText');
	const qtyInput = document.getElementById('qty'); // 數量輸入框控制

	let currentStock = null;
	const STOCK_API = stockHint ? (stockHint.dataset.stockUrl || '/shop/api/stock') : '/shop/api/stock';

	// 🟡【修改重點】：初始時完全不選尺寸、停用加入購物車
	if (btnCart) {
		btnCart.setAttribute('aria-disabled', 'true');
		btnCart.classList.add('disabled');
	}
	if (qtyInput) qtyInput.disabled = true;
	if (hidSize) hidSize.value = '';
	if (hidPid) hidPid.value = '';

	setHint('請先選擇尺寸以查看庫存', false);

	function setHint(text, isError) {
		if (!stockHint) return;
		stockHint.textContent = text;
		stockHint.classList.toggle('text-danger', !!isError);
		stockHint.classList.toggle('text-secondary', !isError);
	}

	function setPrice(val) {
		if (!priceText) return;
		if (val == null || val === '') return;
		const n = Number(val);
		priceText.textContent = Number.isFinite(n) ? n.toLocaleString('zh-TW') : String(val);
	}

	function parseQty(v) {
		const n = Number(v);
		if (!Number.isFinite(n) || n < 1) return 1;
		return Math.floor(n);
	}

	function enforceQtyLimit() {
		if (!qtyInput) return true;
		let q = parseQty(qtyInput.value);
		qtyInput.value = q;
		if (currentStock == null) return true;
		if (currentStock <= 0) {
			qtyInput.value = 1;
			qtyInput.disabled = true;
			if (btnCart) {
				btnCart.setAttribute('aria-disabled', 'true');
				btnCart.classList.add('disabled');
			}
			return false;
		}
		qtyInput.max = String(currentStock);
		if (q > currentStock) {
			qtyInput.value = currentStock;
			setHint(`超過庫存上限，已自動調整為 ${currentStock} 件`, true);
			return false;
		}
		return true;
	}

	function disablePurchase(msg) {
		currentStock = 0;
		setHint(msg || '此尺寸已下架或無庫存', true);
		if (qtyInput) { qtyInput.value = 1; qtyInput.disabled = true; qtyInput.removeAttribute('max'); }
		if (btnCart) { btnCart.setAttribute('aria-disabled', 'true'); btnCart.classList.add('disabled'); }
	}

	function enablePurchase(stock) {
		currentStock = Number(stock);
		if (!Number.isFinite(currentStock) || currentStock < 0) currentStock = 0;

		if (currentStock > 0) {
			setHint(`庫存：${currentStock} 件`, false);
			if (qtyInput) {
				qtyInput.disabled = false;
				qtyInput.max = String(currentStock);
				enforceQtyLimit();
			}
			if (btnCart) {
				btnCart.removeAttribute('aria-disabled');
				btnCart.classList.remove('disabled');
			}
		} else {
			disablePurchase('目前缺貨');
		}
	}

	function markDisabledChip(chip) {
		if (!chip) return;
		chip.classList.add('disabled', 'opacity-50');
		chip.setAttribute('aria-disabled', 'true');
		chip.removeAttribute('aria-selected');
		chip.classList.remove('active');
	}

	function isChipDisabled(chip) {
		if (!chip) return true;
		const pid = (chip.dataset.pid || '').trim().toLowerCase();
		const ariaDisabled = chip.getAttribute('aria-disabled') === 'true';
		return ariaDisabled || pid === '' || pid === '0' || pid === 'null' || pid === 'undefined';
	}

	document.querySelectorAll('#sizeGroup .size-chip').forEach(chip => {
		if (isChipDisabled(chip)) {
			markDisabledChip(chip);
		} else {
			// ✅【重點】移除預設 active（讓一進頁面不會選中）
			chip.classList.remove('active');
			chip.removeAttribute('aria-selected');
		}
	});

	let inflightCtrl = null;

	async function loadStock(pid) {
		if (!pid) {
			disablePurchase('此尺寸已下架或無庫存');
			return;
		}

		if (inflightCtrl) inflightCtrl.abort();
		inflightCtrl = new AbortController();

		setHint('載入庫存中…', false);

		try {
			const r = await fetch(`${STOCK_API}?productId=${encodeURIComponent(pid)}`, {
				credentials: 'same-origin',
				signal: inflightCtrl.signal,
				headers: { 'Accept': 'application/json' }
			});

			const data = await r.json();
			if (!r.ok) throw new Error('HTTP ' + r.status);
			const stock = Number(data?.stock ?? data?.quantity ?? NaN);
			if (!Number.isNaN(stock)) enablePurchase(stock);
			else disablePurchase('無法取得庫存');
		} catch (err) {
			if (err.name === 'AbortError') return;
			disablePurchase('無法取得庫存，請稍後再試');
		}
	}

	document.addEventListener('click', (e) => {
		const chip = e.target.closest('.size-chip');
		if (!chip) return;

		if (isChipDisabled(chip)) {
			e.preventDefault();
			return;
		}

		e.preventDefault();

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
		let pid = chip.dataset.pid || '';
		const sku = chip.dataset.sku || '';
		const price = chip.dataset.price || '';

		if (pid) {
			const n = Number(pid);
			pid = Number.isFinite(n) ? String(n) : String(pid);
		}

		if (hidSize) hidSize.value = size;
		if (hidPid) hidPid.value = pid;

		if (btnCart) {
			if (pid) btnCart.setAttribute('data-id', pid);
			else btnCart.removeAttribute('data-id');
		}
		if (skuText && sku) skuText.textContent = sku;

		setPrice(price);
		loadStock(pid);
	}, true);

	if (qtyInput) {
		qtyInput.addEventListener('input', () => enforceQtyLimit());
		qtyInput.addEventListener('blur', () => enforceQtyLimit());
	}

	if (btnCart) {
		btnCart.addEventListener('click', async function(event) {
			event.preventDefault();

			if (this.classList.contains('disabled') || this.getAttribute('aria-disabled') === 'true') {
				setHint('請先選擇有庫存的尺寸', true);
				return;
			}

			if (!enforceQtyLimit()) return;

			const productId = this.dataset.id;
			const quantity = qtyInput ? parseQty(qtyInput.value) : 1;

			if (!productId) {
				setHint('無法加入購物車，請重新選擇尺寸', true);
				return;
			}

			try {
				await addItemToCart(productId, quantity);
				Swal.fire({ icon: 'success', title: '成功加入購物車！', showConfirmButton: false, timer: 1500 });
			} catch (error) {
				Swal.fire({ icon: 'error', title: '加入失敗', text: error.message || '請稍後再試' });
			}
		});
	}
})();
